package mimicDataClass.common

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import common.createGeneratedAnnotation

class ExtensionFileGenerator(
    private val packageName: String,
    private val fileName: String,
    private val functions: List<FunSpec>,
) {
    fun generate(): FileSpec =
        FileSpec.Companion
            .builder(packageName, fileName)
            .addAnnotation(createGeneratedAnnotation())
            .apply { functions.forEach { this.addFunction(it) } }
            .build()
}
