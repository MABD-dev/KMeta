package org.mabd


@Loggable
interface ApiService {
    fun test()
    fun test2(a: Int)
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
    println("Hello World!")

    val realApiService = RealApiService()
    val a = ApiServiceLoggerImpl(realApiService)

    a.test()
    a.test2(1)
    a.test3(3)
}