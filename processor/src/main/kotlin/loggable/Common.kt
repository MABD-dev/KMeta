package org.mabd.loggable

import com.google.devtools.ksp.symbol.KSAnnotation

internal fun Sequence<KSAnnotation>.doLog(): Boolean = this
    .filter { it.shortName.asString() == NoLog::class.java.simpleName }
    .toList()
    .isEmpty()
