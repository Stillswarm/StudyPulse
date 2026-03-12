package com.studypulse.common.utils

object StringUtils {
    fun String.convertToSentenceCase(): String {
        return this.lowercase().replaceFirstChar { it.uppercase() }
    }

    fun String.getAbbreviatedName(): String {
        return this.substring(0, 3)
    }
}
