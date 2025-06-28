# Mimic Data Classes
Data-Class-Style Copy for Regular Classes


## Table Of Content
- [Copy processor]()
    - [What does it do](#what-does-it-do)
    - [How it works](#how-it-works)
    - [Upcoming Features](#upcoming-features)

---

## Copy Processor

### **What does it do**
Adds a copy extension function to any regular (non-data) class annotated with @Copy.

This function mimics Kotlin’s data class copy, allowing you to conveniently clone objects and change only the properties you want.

### **How it works**

```kotlin
@Copy // <- Add this
class Person(val name: String, val age: Int)


// @Copy generates this function:
fun Person.copy(
    name: String = this.name,
    age: Int = this.age
): Person = Person(name, age)
```

### Upcoming Features:
- [ ] Support for classes with default values in constructors

