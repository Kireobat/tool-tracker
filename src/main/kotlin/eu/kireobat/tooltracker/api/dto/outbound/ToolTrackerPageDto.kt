package eu.kireobat.tooltracker.api.dto.outbound

data class ToolTrackerPageDto<T>(
    var page: List<T>,
    var totalItems: Long = 0,
    var currentPage: Int = 0,
    var pageSize: Int = 10,
    var totalPages: Int = 0,
    var hasNextPage: Boolean = false,
    var hasPreviousPage: Boolean = false
) {
    init {
        totalPages = if (pageSize > 0) {
            ((totalItems + pageSize.toLong() - 1L) / pageSize.toLong()).toInt()
        } else {
            0
        }
        hasNextPage = currentPage < totalPages -1
        hasPreviousPage = currentPage >= 1
    }
}