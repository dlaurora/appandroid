package com.techquote.app.data.local.report

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.techquote.app.data.local.client.ClientEntity
import com.techquote.app.data.local.quote.QuoteEntity

@Entity(
    tableName = "technical_reports",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.NO_ACTION,
        ),
        ForeignKey(
            entity = QuoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["relatedQuoteId"],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["reportNumber"], unique = true),
        Index(value = ["clientId"]),
        Index(value = ["relatedQuoteId"]),
        Index(value = ["isArchived"]),
        Index(value = ["status"]),
        Index(value = ["updatedAt"]),
        Index(value = ["serviceDate"]),
        Index(value = ["normalizedReportNumber"]),
        Index(value = ["normalizedTitle"]),
        Index(value = ["normalizedStatus"]),
        Index(value = ["normalizedTechnicianName"]),
        Index(value = ["normalizedDeviceOrAsset"]),
        Index(value = ["normalizedProblemReported"]),
    ],
)
data class TechnicalReportEntity(
    @PrimaryKey val id: String,
    val reportNumber: String,
    val clientId: String,
    val relatedQuoteId: String?,
    val title: String,
    val serviceDate: String,
    val technicianName: String,
    val deviceOrAsset: String,
    val problemReported: String,
    val diagnosis: String,
    val workPerformed: String,
    val recommendations: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean,
    val normalizedReportNumber: String,
    val normalizedTitle: String,
    val normalizedStatus: String,
    val normalizedTechnicianName: String,
    val normalizedDeviceOrAsset: String,
    val normalizedProblemReported: String,
)

@Entity(
    tableName = "report_attachments",
    foreignKeys = [
        ForeignKey(
            entity = TechnicalReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["reportId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["reportId"]),
        Index(value = ["displayOrder"]),
    ],
)
data class ReportAttachmentEntity(
    @PrimaryKey val id: String,
    val reportId: String,
    val localUri: String,
    val fileName: String,
    val mimeType: String,
    val createdAt: Long,
    val displayOrder: Int,
    val width: Int?,
    val height: Int?,
    val fileSizeBytes: Long?,
)

@Entity(tableName = "report_number_counters")
data class ReportNumberCounterEntity(
    @PrimaryKey val year: String,
    val lastNumber: Int,
)
