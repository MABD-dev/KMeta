# Symbol Processors Playground


A playground for building and sharing Kotlin Symbol Processor (KSP) annotation processors.
This repository contains educational and production-style KSP processors created while learning and experimenting with symbol processing in Kotlin.

---

## ✨ Purpose

- **Learn**: Deepen your understanding of KSP and Kotlin metaprogramming.
- **Experiment**: Try out various code generation and analysis patterns.
- **Share**: Help others by providing minimal, focused, real-world KSP use cases.

---

## 📦 Processors in This Repo

### 1. `@Loggable` — Logging Decorator for Interfaces

Automatically generate a decorator implementation for any interface annotated with `@Loggable`.  
The generated class logs function calls, input parameters, and return values.

#### **Features**

- Generates a decorator (e.g., `MyApiLoggerImpl`) for every interface annotated with `@Loggable`.
- All function calls are delegated and logged with their parameters and results.
- Preserves all function modifiers (`suspend`, `operator`, etc.), annotations, and KDoc.
- Supports per-function logging opt-out via the `@NoLog` annotation.
- Retains KDoc from both interfaces and functions in the generated code.
- Interface and function generics are supported


#### **How it works:**

1. **Annotate your interface:**

    ```kotlin
    @Loggable
    interface MyApi {
        /**
         * Does something important.
         */
        suspend fun doSomething(x: Int, y: String): Boolean

        @NoLog
        fun getRawData(): List<String>
    }
    ```

2. **After build, the processor generates:**

    ```kotlin
    class MyApiLoggerImpl(private val delegate: MyApi) : MyApi {
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
    }
    ```

3. **Wrap your real implementation with the logger:**

    ```kotlin
    val api: MyApi = MyApiLoggerImpl(realApi)
    ```


#### **Customization**

- **Skip logging** for specific functions by annotating them with `@NoLog`.
- All interface-level and function-level documentation and annotations are preserved.


### Upcoming Features:
- [X] Copy modifiers to generated functions
- [X] Copy docs from original code
- [X] Add annotation to function to skip logging
- [X] Handle generics
- [ ] Support for function default params
- [ ] Support varargs
- [ ] Support properties


---


## 📚 **Learning Resources**

- [KSP Official Docs](https://kotlinlang.org/docs/ksp-overview.html#symbolprocessorprovider-the-entry-point)
- [KotlinPoet](https://square.github.io/kotlinpoet/)

---

## 🤝 **Contributing / Ideas**

**issues and PRs for interesting KSP patterns are welcome**.
