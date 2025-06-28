package org.mabd.mimic_data_class.generators

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import org.mabd.common.createGeneratedAnnotation


class ExtensionFileGenerator (
    private val packageName: String,
    private val functions: List<FunSpec>
) {

    fun generate(): FileSpec {
        val fileName = "CopyExtension"

        return FileSpec.builder(packageName, fileName)
            .addAnnotation(createGeneratedAnnotation())
            .apply { functions.forEach { this.addFunction(it) } }
            .build()
    }

}