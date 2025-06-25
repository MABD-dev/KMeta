package org.mabd.copy.generators

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import org.mabd.createGeneratedAnnotation


class ExtensionFileGenerator (
    private val packageName: String,
    private val functions: Sequence<FunSpec>
) {

    fun generate(): FileSpec {
        val fileName = "CopyExtension"

        return FileSpec.builder(packageName, fileName)
            .addAnnotation(createGeneratedAnnotation())
            .apply { functions.forEach { this.addFunction(it) } }
            .build()
    }

}