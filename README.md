# Symbol Processors Playground


A playground for building and sharing Kotlin Symbol Processor (KSP) annotation processors.
This repository contains educational and production-style KSP processors created while learning and experimenting with symbol processing in Kotlin.

---

## ✨ Purpose

- **Learn**: Deepen your understanding of KSP and Kotlin metaprogramming.
- **Experiment**: Try out various code generation and analysis patterns.
- **Share**: Help others by providing minimal, focused, real-world KSP use cases.


# Table Of Content:
- [@Loggable](#1-loggable--logging-decorator-for-interfaces)
- [@Copy](#2-copy--data-class-style-copy-for-regular-classes)

## 📦 Processors in This Repo

### 1. `@Loggable` — Logging Decorator for Interfaces

Automatically generate a decorator implementation for any interface annotated with `@Loggable`.  
The generated class logs function calls, input parameters, and return values.

#### **Features**

- Generates a decorator (e.g., `MyApiLoggerImpl`) for every interface annotated with `@Loggable`.
- All function calls are delegated and logged with their parameters and results.
- All properties are delegated and logged
- Preserves all function modifiers (`suspend`, `operator`, etc.), annotations, and KDoc.
- Supports per-function logging opt-out via the `@NoLog` annotation.
- Retains KDoc from both interfaces and functions in the generated code.
- Interface and function generics are supported


#### **How it works:**

1. **Annotate your interface:**

    ```kotlin
    @Loggable
    interface MyApi {
   
       var isDebug: Boolean
   
        /**
         * Does something important.
         */
        suspend fun doSomething(x: Int, y: String): Boolean

        @NoLog
        fun getRawData(): List<String>
   
        fun <T>getSomething(): List<T>
    }
    ```

2. **After build, the processor generates:**

    ```kotlin
    class MyApiLoggerImpl(private val delegate: MyApi) : MyApi {

      override var isDebug: Boolean
         get() {
            val result = delegate.isDebug
            println("MyApiLoggerImpl: get isDebug:=${result}")
            return result
         }
         set(`value`) {
            delegate.isDebug = value
            println("MyApiLoggerImpl: set isDebug:=${value}")
         }
   
        /**
         * Does something important.
         */
        override suspend fun doSomething(x: Int, y: String): Boolean {
            val result = delegate.doSomething(x, y)
            println("MyApiLoggerImpl: doSomething(x=$x, y=$y)->$result")
            return result
        }

        @NoLog
        override fun getRawData(): List<String> {
            val result = delegate.getRawData()
            // Logging is skipped for this function!
            return result
        }
   
        
         override fun <T> getSomething(a: T)  {
            val result = delegate.getSomething<T>(a)
            println("MyApiLoggerImpl: doSomething(a=$a)")
            return result
         }
    }
    ```

3. **Wrap your real implementation with the logger:**

    ```kotlin
    val api: MyApi = MyApiLoggerImpl(realApi)
    ```


#### **Customization**

- **Skip logging** for specific functions by annotating them with `@NoLog`.
- All interface-level and function-level documentation and annotations are preserved.

---

### 2. `@Copy` — Data-Class-Style Copy for Regular Classes
Adds a copy extension function to any regular (non-data) class annotated with @Copy.
This function mimics Kotlin’s data class copy, allowing you to conveniently clone objects and change only the properties you want.

#### How It Works:
```kotlin
@Copy
class Person(val name: String, val age: Int)
```

Generated code
```kotlin
fun Person.copy(
    name: String = this.name,
    age: Int = this.age
): Person = Person(name, age)
```


#### Upcoming Features:
- [ ] Non-Primary Constructor Parameters
- [ ] Non-Property Parameters
- [ ] Support for classes with default values in constructors
- [ ] KDoc and annotations
- [ ] Multiple packages. Generate extension file per package


---


## 📚 **Learning Resources**

- [KSP Official Docs](https://kotlinlang.org/docs/ksp-overview.html#symbolprocessorprovider-the-entry-point)
- [KotlinPoet](https://square.github.io/kotlinpoet/)

---

## 🤝 **Contributing / Ideas**

**issues and PRs for interesting KSP patterns are welcome**.
