# Symbol Processors Playground


A playground for building and sharing Kotlin Symbol Processor (KSP) annotation processors.
This repository contains educational and production-style KSP processors created while learning and experimenting with symbol processing in Kotlin.

---

## ✨ Purpose

- **Learn**: Deepen your understanding of KSP and Kotlin metaprogramming.
- **Experiment**: Try out various code generation and analysis patterns.
- **Share**: Help others by providing minimal, focused, real-world KSP use cases.


# Table Of Content:
- [@Loggable](docs/Loggable-README.md)
- [@Copy](docs/Mimic-Data-Class-README.md)
- [Learning Resources](#-learning-resources)
- [Contribution](#-contributing--ideas)

***

## **@Loggable**
```kotlin
@Loggable // <- add this
interface ApiService {
    fun fetchUserNames(): List<String>
}

// Generated code
public class ApiService2LoggerImpl(
    private val `delegate`: ApiService2,
): ApiService {
    
    override fun fetchUserNames(): List<String> {
        val result = delegate.fetchUserNames()
        println("ApiService2LoggerImpl: fetchUserNames()->$result")
        return result
    }
    
}
```
view full `@Loggable` docs [here](docs/Loggable-README.md)

## **@Copy**
```kotlin
@Copy // <- add this
class Person(
    val name: String,
    val age: Int
)

// generated code
fun Person.copy(
    name = this.name,
    age = this.age
): Person = Person(name, age)
```
view full `@Copy` docs [here](docs/Mimic-Data-Class-README.md#copy-processor)


***

## 📚 **Learning Resources**

- [KSP Official Docs](https://kotlinlang.org/docs/ksp-overview.html#symbolprocessorprovider-the-entry-point)
- [KotlinPoet](https://square.github.io/kotlinpoet/)

---

## 🤝 **Contributing / Ideas**

**issues and PRs for interesting KSP patterns are welcome**.
