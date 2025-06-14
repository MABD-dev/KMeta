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
    val apiService = ApiServiceLoggerImpl(RealApiService())

    apiService.test()
    apiService.test2(1)
    apiService.test3(3)
}