package org.mabd

import org.mabd.loggable.Loggable
import org.mabd.loggable.NoLog


/**
 * Testing docs on class
 */
@Loggable
interface ApiService<T> {

    var isAuth: Boolean?

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

    fun test5(a: Int, vararg f: Float)
}

class RealApiService: ApiService<String> {
    override var isAuth: Boolean? = false

    override fun test() {}
    override fun test2(a: Int) {}
    override fun test3(b: Int): Int { return 1 }
    override fun <T: Number, R> test4() {}
    override fun test5(a: Int, vararg f: Float) {}

}



fun main() {
    val apiService = ApiServiceLoggerImpl(RealApiService())

    println(apiService.isAuth)
    apiService.isAuth = true

    apiService.test()
    apiService.test2(1)
    apiService.test3(3)
    apiService.test4<Int, Int>()
    apiService.test5(1, .1f, .2f)

}