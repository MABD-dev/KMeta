package org.mabd.loggable

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Loggable


/**
 * Mark function with this annotation to skip logging in that function
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class NoLog


class LoggableProcessor(
    private val env: SymbolProcessorEnvironment
): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("org.mabd.Loggable")

        symbols
            .filterIsInstance<KSClassDeclaration>()
            .forEach { symbol ->
                val klass = symbol as? KSClassDeclaration ?: return@forEach

                if (klass.classKind != ClassKind.INTERFACE) {
                    env.logger.error("Loggable is only applicable for interfaces only")
                }
                val fileSpec = klass.createFile()
                fileSpec.writeTo(env.codeGenerator, Dependencies(false))
            }
        return emptyList()
    }

    private fun KSClassDeclaration.createFile(): FileSpec {
        val packageName = this.packageName.asString()
        val interfaceName = this.simpleName.asString()
        val fileName = "${interfaceName}LoggerImpl"

        val typeParameters = this.typeParameters.map { it.toTypeVariable() }

        val loggerClassName = ClassName(packageName, fileName)
        val interfaceClassName = ClassName(packageName, interfaceName)
            .parameterizedBy(typeParameters)

        val delegateName = "delegate"

        val delegateProp = PropertySpec.builder(delegateName, interfaceClassName)
            .initializer(delegateName)
            .addModifiers(KModifier.PRIVATE)
            .build()

        val constructor = FunSpec.constructorBuilder()
            .addParameter(delegateName, interfaceClassName)
            .build()

        val loggerClass = TypeSpec.classBuilder(loggerClassName)
            .primaryConstructor(constructor)
            .addProperty(delegateProp)
            .addSuperinterface(interfaceClassName)
            .addTypeVariables(this.typeParameters.map { it.toTypeVariable() })

        this.docString?.let { loggerClass.addKdoc(it) }

        val properties = this
            .getDeclaredProperties()
            .map { it.createPropertySpecs() }
            .toList()
        loggerClass.addProperties(properties)

        val functions = this
            .getDeclaredFunctions()
            .map { it.createFunctionSpecs(delegateName, fileName) }
            .toList()
        loggerClass.addFunctions(functions)

        return FileSpec.builder(loggerClassName)
            .addType(loggerClass.build())
            .build()
    }

}

class LoggableProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return LoggableProcessor(environment)
    }

}
