# Guide: Handling Cyclic Dependencies with MapStruct and Builder DTOs

This guide explains how to solve a common issue when using MapStruct with JPA entities that have bidirectional (cyclic) relationships and immutable DTOs that use the builder pattern.

## The Problem

When mapping entities with bidirectional relationships (e.g., `User` <-> `LessonBook`), a naive MapStruct implementation will enter an infinite loop, causing a `StackOverflowError`.

```
UserMapper maps User -> UserDTO
  -> maps User.lessonBooks -> List<LessonBookDTO>
    -> LessonBookMapper maps LessonBook -> LessonBookDTO
      -> maps LessonBook.user -> UserDTO
        -> UserMapper maps User -> UserDTO (Infinite Loop!)
```

The standard solution is to use `@Context` to track mapped objects. However, this introduces a new problem when your DTOs use a builder pattern (e.g., with Lombok's `@Builder`): a `ClassCastException`.

This happens because MapStruct's context caches the **builder** instance, not the final DTO. On the return trip of the cycle, it tries to cast the builder to the DTO type, which fails.

## The Solution

The solution involves a combination of a custom context class and the `@ObjectFactory` annotation to take control of object creation.

### Step 1: Create `CycleAvoidingMappingContext`

First, create a class to manage the mapping context and track instances. This class uses an `IdentityHashMap` to track objects by reference.

```java
package com.example.language_learning.mappers;

import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

import java.util.IdentityHashMap;
import java.util.Map;

public class CycleAvoidingMappingContext {
    private final Map<Object, Object> knownInstances = new IdentityHashMap<>();

    @BeforeMapping
    public <T> T getMappedInstance(Object source, @TargetType Class<T> targetType) {
        return targetType.cast(knownInstances.get(source));
    }

    @BeforeMapping
    public void storeMappedInstance(Object source, @MappingTarget Object target) {
        knownInstances.put(source, target);
    }
}
```

### Step 2: Update Mappers to Use the Context

For every mapper involved in a cycle, you must:
1.  Change the mapper from an `interface` to an `abstract class`.
2.  Add `@Context CycleAvoidingMappingContext context` to all mapping method signatures.

**Before:**
```java
@Mapper
public interface UserStructMapper {
    UserDTO toDto(User entity);
}
```

**After:**
```java
@Mapper
public abstract class UserStructMapper {
    public abstract UserDTO toDto(User entity, @Context CycleAvoidingMappingContext context);
    // ... other methods
}
```

### Step 3: Implement `@ObjectFactory` for Builder DTOs

This is the key to solving the `ClassCastException`. For each mapper that handles a builder-style DTO, add an `@ObjectFactory` method.

This factory method does two things:
1.  Checks the context for an existing instance to break the cycle.
2.  If no instance exists, it creates a **new, empty DTO** using the builder and returns it. MapStruct will then populate this empty DTO.

```java
@Mapper(componentModel = "spring")
public abstract class UserStructMapper {

    public abstract UserDTO toDto(User entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public UserDTO createDto(User entity, @Context CycleAvoidingMappingContext context) {
        // 1. Check context to break the cycle
        UserDTO existingDto = context.getMappedInstance(entity, UserDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        // 2. Create an empty DTO for MapStruct to populate
        return UserDTO.builder().build();
    }
}
```

### Step 4: Update Service/Facade Layer Calls

Finally, whenever you initiate a mapping operation, you must create a `new CycleAvoidingMappingContext()`. This ensures that instance tracking is fresh for each top-level mapping request.

In your `DtoMapper.java` facade:

```java
@Component
@RequiredArgsConstructor
public class DtoMapper {
    private final UserStructMapper userStructMapper;

    public UserDTO toDto(User entity) {
        // Always create a new context for a top-level call
        return userStructMapper.toDto(entity, new CycleAvoidingMappingContext());
    }
}
```

## When to Use This Pattern

You must apply this entire pattern whenever you add a new entity/DTO that meets **both** of these conditions:
1.  It is part of a **bidirectional (cyclic) relationship**.
2.  Its corresponding DTO is **immutable and uses the builder pattern**.

For simpler mappings that don't involve cycles, this setup is not necessary.

### Step 5 (Optional but Recommended): Handling Back-References with `@AfterMapping`

When mapping a parent entity that contains a list of child entities (e.g., a `PracticeLesson` with a list of `Question`s), MapStruct won't automatically set the back-reference from the child to the parent. This can lead to uninitialized properties and build warnings like `Unmapped target property: "lesson"`.

You can solve this by adding an `@AfterMapping` method to the parent's mapper. This method will execute after the initial mapping is complete and allows you to manually set the parent on each child object.

**Example: `PracticeLessonStructMapper.java`**

```java
@Mapper(...)
public abstract class PracticeLessonStructMapper {

    public abstract PracticeLesson toEntity(PracticeLessonDTO dto, @Context CycleAvoidingMappingContext context);

    // ... other methods ...

    @AfterMapping
    protected void setLessonOnQuestions(@MappingTarget PracticeLesson lesson) {
        if (lesson.getQuestions() != null) {
            lesson.getQuestions().forEach(lessonQuestion -> lessonQuestion.setLesson(lesson));
        }
    }
}
```

This ensures that your entity graph is fully and correctly connected after the mapping process.