package com.studypulse.app.common.util

fun String.convertToSentenceCase(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

fun String.getAbbreviatedName(): String {
    return this.substring(0, 3)
}
