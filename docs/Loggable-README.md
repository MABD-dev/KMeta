# Loggable Processor

Logging Decorator for Interfaces


## Table Of Content:
- [What does it do](#what-it-does)
- [How it works](#how-it-works)
- [Customization](#customization)

---

### **What does it do**
Automatically generate a decorator implementation for any interface annotated with `@Loggable`.  

The generated class logs function calls, input parameters, and return values.


### **Features**

- Generates a decorator (e.g., `MyApiLoggerImpl`) for every interface annotated with `@Loggable`.
- All function calls are delegated and logged with their parameters and results.
- All properties are delegated and logged
- Preserves all function modifiers (`suspend`, `operator`, etc.), annotations, and KDoc.
- Supports per-function logging opt-out via the `@NoLog` annotation.
- Retains KDoc from both interfaces and functions in the generated code.
- Interface and function generics are supported



### **How it works**
**Annotate your interface:**
```kotlin
@Loggable  // <-- add this
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

**After build, the processor generates:**
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

### **Customization**

- **Skip logging** for specific functions by annotating them with `@NoLog`.
- All interface-level and function-level documentation and annotations are preserved.

