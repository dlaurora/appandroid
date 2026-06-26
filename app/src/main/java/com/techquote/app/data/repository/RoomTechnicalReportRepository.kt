package com.techquote.app.data.repository

import com.techquote.app.data.local.report.TechnicalReportLocalDataSource
import com.techquote.app.domain.report.ReportAttachment
import com.techquote.app.domain.report.TechnicalReport
import com.techquote.app.domain.report.TechnicalReportRepository
import com.techquote.app.domain.report.TechnicalReportSortOption
import com.techquote.app.domain.report.TechnicalReportStatus
import com.techquote.app.domain.report.TechnicalReportSummary
import com.techquote.app.domain.report.TechnicalReportWithAttachments
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomTechnicalReportRepository @Inject constructor(
    private val localDataSource: TechnicalReportLocalDataSource,
) : TechnicalReportRepository {
    override fun observeReports(
        includeArchived: Boolean,
        query: String,
        status: TechnicalReportStatus?,
        sort: TechnicalReportSortOption,
    ): Flow<List<TechnicalReportSummary>> {
        return localDataSource.observeReports(includeArchived, query, status, sort)
    }

    override fun observeReport(id: String): Flow<TechnicalReportWithAttachments?> {
        return localDataSource.observeReport(id)
    }

    override suspend fun getReport(id: String): TechnicalReportWithAttachments? {
        return localDataSource.getReport(id)
    }

    override suspend fun nextReportNumber(serviceDate: String): String {
        return localDataSource.nextReportNumber(serviceDate)
    }

    override suspend fun saveReport(report: TechnicalReport, attachments: List<ReportAttachment>) {
        localDataSource.saveReport(report, attachments)
    }
}
