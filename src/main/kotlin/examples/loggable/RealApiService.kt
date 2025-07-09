package examples.loggable

//

/**
 * mimics real implementation of [ApiService]
 */
internal class RealApiService : ApiService<String> {
    override var isAuth: Boolean? = false

    override fun testAnnotationsArePreserved() {
        // Real implementation goes here
    }

    override fun testNoLogGenerated(a: Int) {
        // Real implementation goes here
    }

    override fun testDocsArePreserved(b: Int): Int {
        // Real implementation goes here
        return 1
    }

    override fun <T : Number, R> testGenericsArePossible() {
        // Real implementation goes here
    }

    override fun testVararg(
        a: Int,
        vararg f: Float,
    ) {
        // Real implementation goes here
    }
}
