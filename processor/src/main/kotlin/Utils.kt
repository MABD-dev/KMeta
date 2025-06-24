package org.mabd

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

fun KSClassDeclaration.isDataClass(): Boolean {
    return this.modifiers.contains(Modifier.DATA)
}
