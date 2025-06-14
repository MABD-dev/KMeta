package org.mabd


/**
 * Testing docs on class
 */
@Loggable
interface ApiService<T> {

    @Deprecated(
        message = "just for testing",
        replaceWith = ReplaceWith("test2(1)")
    )
    fun test()

    @NoLog
    fun test2(a: Int)

    /**
     * Testing docs on function with params and return
     * @param b something
     * @return some number
     */
    fun test3(b: Int): Int

    fun <T: Number, R> test4()
}

class RealApiService: ApiService<String> {
    override fun test() {
    }

    override fun test2(a: Int) {
    }

    override fun test3(b: Int): Int {
        return 1
    }

    override fun <T: Number, R> test4() {
    }

}



fun main() {
    val apiService = ApiServiceLoggerImpl(RealApiService())

    apiService.test()
    apiService.test2(1)
    apiService.test3(3)
    apiService.test4<Int, Int>()
}