package com.media.guardian.data

enum class SortOrder {
    ASC, DESC
}

enum class SortColumn {
    NAME, DATE, SIZE
}

data class SortOption(val column: SortColumn, val order: SortOrder)
