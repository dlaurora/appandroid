package com.techquote.app.data.local.report

import com.techquote.app.domain.report.ReportAttachment
import com.techquote.app.domain.report.TechnicalReport
import com.techquote.app.domain.report.TechnicalReportSortOption
import com.techquote.app.domain.report.TechnicalReportStatus
import com.techquote.app.domain.report.TechnicalReportSummary
import com.techquote.app.domain.report.TechnicalReportTextNormalizer
import com.techquote.app.domain.report.TechnicalReportWithAttachments
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TechnicalReportLocalDataSource @Inject constructor(
    private val dao: TechnicalReportDao,
) {
    fun observeReports(
        includeArchived: Boolean,
        query: String,
        status: TechnicalReportStatus?,
        sort: TechnicalReportSortOption,
    ): Flow<List<TechnicalReportSummary>> {
        return dao.observeReports(
            isArchived = includeArchived,
            textQuery = TechnicalReportTextNormalizer.normalizeSearch(query),
            status = status?.name.orEmpty(),
            sort = sort.name,
        ).map { list -> list.map { it.toDomain() } }
    }

    fun observeReport(id: String): Flow<TechnicalReportWithAttachments?> {
        return dao.observeReportWithAttachments(id).map { it?.toDomain() }
    }

    suspend fun getReport(id: String): TechnicalReportWithAttachments? {
        return dao.getReportWithAttachments(id)?.toDomain()
    }

    suspend fun nextReportNumber(serviceDate: String): String {
        return dao.nextReportNumber(serviceDate.take(4))
    }

    suspend fun saveReport(report: TechnicalReport, attachments: List<ReportAttachment>) {
        dao.saveReportWithAttachments(report.toEntity(), attachments.sortedBy { it.displayOrder }.map { it.toEntity() })
    }
}
