package org.mabd


@Loggable
interface ApiService {
    fun test()
    fun test2(a: Int)
    fun test3(b: Int): Int
}

class RealApiService: ApiService {
    override fun test() {
        println("apiService: test()")
    }

    override fun test2(a: Int) {
        println("apiService: test(a=$a)")
    }

    override fun test3(b: Int): Int {
        println("apiService: test(b=$b)=1")
        return 1
    }

}

class Shit(
    val delegate: ApiService
): ApiService {
    override fun test() {
        delegate.test()
            .also { println("ShitLogger: test()") }
    }

    override fun test2(a: Int) {
        delegate.test2(a)
            .also { println("ShitLogger: test(a=$a)") }
    }

    override fun test3(b: Int): Int {
        return delegate.test3(b)
            .also { println("ShitLogger: test3(b=$b)=$it") }
    }

}


fun main() {
    println("Hello World!")

    val realApiService = RealApiService()
    val a = ApiServiceLoggerImpl(realApiService)

    a.test()
    a.test2(1)
    a.test3(3)
}