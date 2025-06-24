package org.mabd.copy.generators

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec


class ExtensionFileGenerator (
    private val packageName: String,
    private val functions: Sequence<FunSpec>
) {

    fun generate(): FileSpec {
        val fileName = "CopyExtension"

        return FileSpec.builder(packageName, fileName)
            .apply { functions.forEach { this.addFunction(it) } }
            .build()
    }

}