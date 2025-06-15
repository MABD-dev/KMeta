package org.mabd.loggable

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName

internal fun KSPropertyDeclaration.createPropertySpecs(
    delegateName: String,
    fileName: String,
): PropertySpec {
    val propertyName = this.simpleName.asString()
    val propertySpec = PropertySpec.builder(propertyName, this.type.toTypeName())
        .mutable(this.isMutable)
        .addAnnotations(this)
        .addModifiers(KModifier.OVERRIDE)
        .addModifiers(this)
        .addKdocIfFound(this)
        .getter(this, fileName, delegateName)
        .setter(this, fileName, delegateName)

    return propertySpec.build()
}

internal fun PropertySpec.Builder.addKdocIfFound(
    prop: KSPropertyDeclaration
) = this.apply {
    prop.docString?.let { this.addKdoc(it) }
}

internal fun PropertySpec.Builder.addModifiers(
    prop: KSPropertyDeclaration
) = this.apply {
    addModifiers(prop.modifiers.mapNotNull { it.toKModifier() })
}

internal fun PropertySpec.Builder.addAnnotations(
    func: KSPropertyDeclaration
) = this.apply {
    addAnnotations(func.annotations.map { it.toAnnotationSpec()}.toList())
}

internal fun PropertySpec.Builder.getter(
    prop: KSPropertyDeclaration,
    fileName: String,
    delegateName: String,
) = this.apply {
    val propName = prop.simpleName.asString()
    val func = FunSpec.getterBuilder()
        .addStatement("val result = ${delegateName}.${prop.simpleName.asString()}")

    if (prop.annotations.doLog()) {
        val str ="""println("${fileName}: get ${propName}=\$\{result\}")"""
            .trimIndent()
            .replace("\\", "")
        func.addStatement(str)
    }

    func.addStatement("return result")

    this.getter(func.build())
}

internal fun PropertySpec.Builder.setter(
    prop: KSPropertyDeclaration,
    fileName: String,
    delegateName: String
) = this.apply {
    val propName = prop.simpleName.asString()
    val func = FunSpec.setterBuilder()
        .addParameter("value", prop.type.toTypeName())
        .addStatement("${delegateName}.${prop.simpleName.asString()} = value")

    if (prop.annotations.doLog()) {
        val str ="""println("${fileName}: set:${propName}=\$\{value\}")"""
            .trimIndent()
            .replace("\\", "")
        func.addStatement(str)
    }

    this.setter(func.build())
}