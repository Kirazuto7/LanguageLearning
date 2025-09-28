package com.example.language_learning.shared.data;

import com.example.language_learning.shared.utils.JsonToStringHelper;
import jakarta.persistence.MappedSuperclass;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEntity {
    @Override
    public String toString() {
        return JsonToStringHelper.toJson(this);
    }
}
