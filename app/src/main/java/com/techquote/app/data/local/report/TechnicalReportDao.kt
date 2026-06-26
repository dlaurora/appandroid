package com.techquote.app.data.local.report

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

data class TechnicalReportWithAttachmentsEntity(
    @Embedded val report: TechnicalReportEntity,
    val clientDisplayName: String,
    @Relation(
        parentColumn = "id",
        entityColumn = "reportId",
    )
    val attachments: List<ReportAttachmentEntity>,
)

data class TechnicalReportSummaryEntity(
    val id: String,
    val reportNumber: String,
    val clientId: String,
    val clientDisplayName: String,
    val relatedQuoteId: String?,
    val title: String,
    val serviceDate: String,
    val status: String,
    val updatedAt: Long,
    val isArchived: Boolean,
    val attachmentCount: Int,
)

@Dao
abstract class TechnicalReportDao {
    @Upsert
    protected abstract suspend fun upsertReport(report: TechnicalReportEntity)

    @Upsert
    protected abstract suspend fun upsertAttachments(attachments: List<ReportAttachmentEntity>)

    @Query("DELETE FROM report_attachments WHERE reportId = :reportId")
    protected abstract suspend fun deleteAttachments(reportId: String)

    @Transaction
    open suspend fun saveReportWithAttachments(report: TechnicalReportEntity, attachments: List<ReportAttachmentEntity>) {
        upsertReport(report)
        deleteAttachments(report.id)
        if (attachments.isNotEmpty()) upsertAttachments(attachments)
    }

    @Transaction
    @Query(
        """
        SELECT r.*, COALESCE(NULLIF(c.businessName, ''), NULLIF(c.fullName, ''), 'Cliente sin nombre') AS clientDisplayName
        FROM technical_reports r
        INNER JOIN clients c ON c.id = r.clientId
        WHERE r.id = :id
        LIMIT 1
        """,
    )
    abstract fun observeReportWithAttachments(id: String): Flow<TechnicalReportWithAttachmentsEntity?>

    @Transaction
    @Query(
        """
        SELECT r.*, COALESCE(NULLIF(c.businessName, ''), NULLIF(c.fullName, ''), 'Cliente sin nombre') AS clientDisplayName
        FROM technical_reports r
        INNER JOIN clients c ON c.id = r.clientId
        WHERE r.id = :id
        LIMIT 1
        """,
    )
    abstract suspend fun getReportWithAttachments(id: String): TechnicalReportWithAttachmentsEntity?

    @Query(
        """
        SELECT
            r.id,
            r.reportNumber,
            r.clientId,
            COALESCE(NULLIF(c.businessName, ''), NULLIF(c.fullName, ''), 'Cliente sin nombre') AS clientDisplayName,
            r.relatedQuoteId,
            r.title,
            r.serviceDate,
            r.status,
            r.updatedAt,
            r.isArchived,
            (SELECT COUNT(*) FROM report_attachments a WHERE a.reportId = r.id) AS attachmentCount
        FROM technical_reports r
        INNER JOIN clients c ON c.id = r.clientId
        WHERE r.isArchived = :isArchived
          AND (:status = '' OR r.status = :status)
          AND (
            :textQuery = ''
            OR r.normalizedReportNumber LIKE '%' || :textQuery || '%'
            OR r.normalizedTitle LIKE '%' || :textQuery || '%'
            OR r.normalizedStatus LIKE '%' || :textQuery || '%'
            OR r.normalizedTechnicianName LIKE '%' || :textQuery || '%'
            OR r.normalizedDeviceOrAsset LIKE '%' || :textQuery || '%'
            OR r.normalizedProblemReported LIKE '%' || :textQuery || '%'
            OR c.normalizedFullName LIKE '%' || :textQuery || '%'
            OR c.normalizedBusinessName LIKE '%' || :textQuery || '%'
          )
        ORDER BY
            CASE WHEN :sort = 'REPORT_NUMBER' THEN r.reportNumber END DESC,
            CASE WHEN :sort = 'SERVICE_DATE' THEN r.serviceDate END DESC,
            CASE WHEN :sort = 'UPDATED_AT' THEN r.updatedAt END DESC,
            r.updatedAt DESC,
            r.reportNumber DESC
        """,
    )
    abstract fun observeReports(
        isArchived: Boolean,
        textQuery: String,
        status: String,
        sort: String,
    ): Flow<List<TechnicalReportSummaryEntity>>

    @Query("SELECT lastNumber FROM report_number_counters WHERE year = :year LIMIT 1")
    protected abstract suspend fun getLastNumber(year: String): Int?

    @Upsert
    protected abstract suspend fun upsertCounter(counter: ReportNumberCounterEntity)

    @Transaction
    open suspend fun nextReportNumber(year: String): String {
        val next = (getLastNumber(year) ?: 0) + 1
        upsertCounter(ReportNumberCounterEntity(year = year, lastNumber = next))
        return "TR-$year-${next.toString().padStart(6, '0')}"
    }

    @Query("UPDATE technical_reports SET isArchived = :isArchived, updatedAt = :updatedAt WHERE id = :id")
    abstract suspend fun setArchived(id: String, isArchived: Boolean, updatedAt: Long)
}
