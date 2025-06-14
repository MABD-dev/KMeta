package org.mabd


/**
 * Testing docs on class
 */
@Loggable
interface ApiService {

    @Deprecated(
        message = "just for testing",
        replaceWith = ReplaceWith("test2(1)")
    )
    fun test()

    fun test2(a: Int)

    /**
     * Testing docs on function with params and return
     * @param b something
     * @return some number
     */
    fun test3(b: Int): Int
}

class RealApiService: ApiService {
    override fun test() {
    }

    override fun test2(a: Int) {
    }

    override fun test3(b: Int): Int {
        return 1
    }

}



fun main() {
    val apiService = ApiServiceLoggerImpl(RealApiService())

    apiService.test()
    apiService.test2(1)
    apiService.test3(3)
}