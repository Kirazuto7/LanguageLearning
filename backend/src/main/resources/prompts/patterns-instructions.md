# Shared Regex Patterns for JSON Schemas

This document contains a centralized list of regex patterns to be used within our JSON schema definitions. 
While JSON Schema doesn't support importing these directly, this file serves as the single source of truth to ensure consistency 
when creating or updating schemas.

When adding a `pattern` to a schema property, copy the string directly from here.

## How to Use

To validate a field, construct your final regex pattern by combining the necessary building blocks from the list below.

**Example: Creating a pattern for English text with numbers and punctuation**

1.  Start with the base regex structure: `^[]+$`
2.  Copy the `english-text` pattern: `A-Za-z`
3.  Copy the `numbers` pattern: `0-9`
4.  Copy the `punctuation` pattern: ` ,.?!'\"-;:()\\[\\]…`
5.  Combine them inside the brackets: `^[A-Za-z0-9 ,.?!'\"-;:()\\[\\]…]+$`

**Final JSON Schema Example Snippet:**
<!-- markdownlint-disable -->
```json
{
  "someFieldName": {
    "type": "string",
    "description": "A field for English text that allows punctuation.",
    "pattern": "^[A-Za-z0-9 ,.?!'\"-;:()\\[\\]…]+$"
  }
}
```

---
### `Punctuation`

**Pattern:**
```
 ,.?!'\"-;:()\\[\\]…
```

---

---
### `Numbers`

**Pattern:**
```
0-9
```

---

---
### `English`

**Pattern:**
```
A-Za-z
```

**Vocabulary Pattern:**
```
A-Za-z -
```

---
### `Korean`

**Pattern:**
```
ㄱ-ㅎ가-힣
```

---
### `Japanese`

**All Pattern:**
```
\u3040-\u309F\u30A0-\u30FF\u4E00-\u9FFF\u3400-\u4DBF\uF900-\uFAFF
```

**Kanji Pattern:**
```
\u4E00-\u9FFF\u3400-\u4DBF\uF900-\uFAFF
```

**Hiragana Pattern:**
```
\u3040-\u309F
```

**Katakana Pattern:**
```
\u30A0-\u30FF
```

---
### `Chinese`

**Pattern**
```
\u4E00-\u9FFF\u3400-\u4DBF\uF900-\uFAFF
```

---
### `Thai`

**Pattern**
```
\u0E00-\u0E7F
```

___
### `Latin Extended (e.g., French, German, Spanish, Italian)`

**Pattern**
```
A-Za-z\u00C0-\u00FF\u0100-\u017F
```