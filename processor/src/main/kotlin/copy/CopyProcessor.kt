package org.mabd.copy

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.writeTo
import org.mabd.copy.generators.ClassCopyFunGenerator
import org.mabd.copy.generators.ExtensionFileGenerator
import org.mabd.isDataClass


/**
 * Generates a data-class-like `copy` extension function for this class.
 *
 * Apply to a regular (non-data) class with a primary constructor. The generated extension will let you copy instances
 * while changing any combination of properties, just like Kotlin's data class `copy`.
 *
 * Example:
 * ```
 * @Copy
 * class User(val name: String, val age: Int)
 * // Generates:
 * // fun User.copy(name: String = this.name, age: Int = this.age): User = User(name, age)
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Copy

class CopyProcessor(
    private val env: SymbolProcessorEnvironment
): SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val classDeclarations = getCopyAnnotationDeclarations(resolver)
        if (!classDeclarations.iterator().hasNext()) {
            return emptyList()
        }

        val functions = classDeclarations.mapNotNull { declaration ->
            ClassCopyFunGenerator(declaration).generate(env.logger)
        }

        val packageName = classDeclarations.first().packageName.asString()
        val fileSpec = ExtensionFileGenerator(packageName, functions).generate()
        fileSpec.writeTo(env.codeGenerator, Dependencies(false))

        return emptyList()
    }

    private fun getCopyAnnotationDeclarations(resolver: Resolver): Sequence<KSClassDeclaration> {
        return resolver.getSymbolsWithAnnotation(Copy::class.java.name)
            .filterIsInstance<KSClassDeclaration>()
            .distinct()
            .filter { it.validate() }
            .filter { it.classKind == ClassKind.CLASS }
            .filter { !it.isDataClass() }
    }

}