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

#### **How it works:**

1. Annotate your interface:
    ```kotlin
    @Loggable
    interface MyApi {
        fun doSomething(x: Int, y: String): Boolean
        fun getData(): List<String>
    }
    ```
2. The processor generates a class:
    ```kotlin
    class MyApiLoggerImpl(private val delegate: MyApi) : MyApi {
        override fun doSomething(x: Int, y: String): Boolean {
            val result = delegate.doSomething(x, y)
            println("MyApiLoggerImpl: doSomething(x=$x, y=$y)->$result")
            return result
        }

        override fun getData(): List<String> {
            val result = delegate.getData()
            println("MyApiLoggerImpl: getData()->$result")
            return result
        }
    }
    ```
3. Use the generated class to wrap your implementation:
    ```kotlin
    val loggedApi = MyApiLoggerImpl(realApi)
    ```

### Upcoming Features:
- [ ] Handle suspend functions
- [ ] Match functions modifier
- [ ] Support for function default params
- [ ] Match visibility modifiers
- [ ] Handle generics
- [ ] Copy docs from original code
- [ ] Add annotation to function to skip logging


---


## 📚 **Learning Resources**

- [KSP Official Docs](https://kotlinlang.org/docs/ksp-overview.html#symbolprocessorprovider-the-entry-point)
- [KotlinPoet](https://square.github.io/kotlinpoet/)

---

## 🤝 **Contributing / Ideas**

**issues and PRs for interesting KSP patterns are welcome**.
