package com.techquote.app.domain.report.usecase

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.quote.QuoteRepository
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.report.ReportAttachment
import com.techquote.app.domain.report.TechnicalReport
import com.techquote.app.domain.report.TechnicalReportInput
import com.techquote.app.domain.report.TechnicalReportOperationResult
import com.techquote.app.domain.report.TechnicalReportRepository
import com.techquote.app.domain.report.TechnicalReportStateMachine
import com.techquote.app.domain.report.TechnicalReportStatus
import com.techquote.app.domain.report.TechnicalReportTextNormalizer
import com.techquote.app.domain.report.TechnicalReportValidator
import com.techquote.app.domain.report.TechnicalReportWithAttachments
import javax.inject.Inject
import javax.inject.Named

class CreateTechnicalReportUseCase @Inject constructor(
    private val reportRepository: TechnicalReportRepository,
    private val clientRepository: ClientRepository,
    private val validator: TechnicalReportValidator,
    @param:Named("reportIdGenerator")
    private val idGenerator: () -> String,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(input: TechnicalReportInput): TechnicalReportOperationResult<TechnicalReportWithAttachments> {
        return try {
            val client = clientRepository.getClient(input.clientId)
            val validation = validator.validate(input, client)
            if (!validation.isValid) return TechnicalReportOperationResult.ValidationError(validation.errors)
            val now = clock()
            val reportId = idGenerator()
            val reportNumber = reportRepository.nextReportNumber(input.serviceDate)
            val report = buildReport(
                id = reportId,
                reportNumber = reportNumber,
                input = input,
                status = TechnicalReportStatus.DRAFT,
                createdAt = now,
                updatedAt = now,
                isArchived = false,
            )
            val saved = TechnicalReportWithAttachments(report, requireNotNull(client).displayName(), emptyList())
            reportRepository.saveReport(saved.report, saved.attachments)
            TechnicalReportOperationResult.Success(saved)
        } catch (_: Exception) {
            TechnicalReportOperationResult.StorageError
        }
    }
}

class CreateTechnicalReportFromQuoteUseCase @Inject constructor(
    private val reportRepository: TechnicalReportRepository,
    private val quoteRepository: QuoteRepository,
    private val clientRepository: ClientRepository,
    private val validator: TechnicalReportValidator,
    @param:Named("reportIdGenerator")
    private val idGenerator: () -> String,
    @param:Named("clock")
    private val clock: () -> Long,
    @param:Named("todayProvider")
    private val todayProvider: () -> String,
) {
    suspend operator fun invoke(quoteId: String): TechnicalReportOperationResult<TechnicalReportWithAttachments> {
        return try {
            val quoteWithItems = quoteRepository.getQuote(quoteId) ?: return TechnicalReportOperationResult.NotFound
            val quote = quoteWithItems.quote
            if (quote.status != QuoteStatus.APPROVED) return TechnicalReportOperationResult.InvalidQuoteStatus
            val input = TechnicalReportInput(
                clientId = quote.clientId,
                relatedQuoteId = quote.id,
                title = quote.title,
                serviceDate = todayProvider(),
                technicianName = "",
                deviceOrAsset = "",
                problemReported = quote.description,
                diagnosis = "",
                workPerformed = "",
                recommendations = "",
            )
            val client = clientRepository.getClient(input.clientId)
            val validation = validator.validate(input, client)
            if (!validation.isValid) return TechnicalReportOperationResult.ValidationError(validation.errors)
            val now = clock()
            val reportId = idGenerator()
            val reportNumber = reportRepository.nextReportNumber(input.serviceDate)
            val report = buildReport(
                id = reportId,
                reportNumber = reportNumber,
                input = input,
                status = TechnicalReportStatus.DRAFT,
                createdAt = now,
                updatedAt = now,
                isArchived = false,
            )
            val saved = TechnicalReportWithAttachments(report, requireNotNull(client).displayName(), emptyList())
            reportRepository.saveReport(saved.report, saved.attachments)
            TechnicalReportOperationResult.Success(saved)
        } catch (_: Exception) {
            TechnicalReportOperationResult.StorageError
        }
    }
}

class UpdateTechnicalReportUseCase @Inject constructor(
    private val reportRepository: TechnicalReportRepository,
    private val clientRepository: ClientRepository,
    private val validator: TechnicalReportValidator,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String, input: TechnicalReportInput): TechnicalReportOperationResult<TechnicalReportWithAttachments> {
        return try {
            val existing = reportRepository.getReport(id) ?: return TechnicalReportOperationResult.NotFound
            if (!TechnicalReportStateMachine.canEdit(existing.report.status)) return TechnicalReportOperationResult.InvalidTransition
            val client = clientRepository.getClient(input.clientId)
            val validation = validator.validate(input, client)
            if (!validation.isValid) return TechnicalReportOperationResult.ValidationError(validation.errors)
            val candidate = buildReport(
                id = existing.report.id,
                reportNumber = existing.report.reportNumber,
                input = input,
                status = existing.report.status,
                createdAt = existing.report.createdAt,
                updatedAt = existing.report.updatedAt,
                isArchived = existing.report.isArchived,
            )
            val changed = candidate.editableFingerprint() != existing.report.editableFingerprint()
            val updatedReport = if (changed) candidate.copy(updatedAt = clock()) else existing.report
            val updated = TechnicalReportWithAttachments(
                report = updatedReport,
                clientDisplayName = requireNotNull(client).displayName(),
                attachments = existing.attachments,
            )
            reportRepository.saveReport(updated.report, updated.attachments)
            TechnicalReportOperationResult.Success(updated)
        } catch (_: Exception) {
            TechnicalReportOperationResult.StorageError
        }
    }
}

class ChangeTechnicalReportStatusUseCase @Inject constructor(
    private val reportRepository: TechnicalReportRepository,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String, status: TechnicalReportStatus): TechnicalReportOperationResult<TechnicalReportWithAttachments> {
        return try {
            val existing = reportRepository.getReport(id) ?: return TechnicalReportOperationResult.NotFound
            if (!TechnicalReportStateMachine.canTransition(existing.report.status, status)) {
                return TechnicalReportOperationResult.InvalidTransition
            }
            val updated = existing.copy(report = existing.report.copy(status = status, updatedAt = clock()))
            reportRepository.saveReport(updated.report, updated.attachments)
            TechnicalReportOperationResult.Success(updated)
        } catch (_: Exception) {
            TechnicalReportOperationResult.StorageError
        }
    }
}

class DuplicateTechnicalReportUseCase @Inject constructor(
    private val reportRepository: TechnicalReportRepository,
    private val clientRepository: ClientRepository,
    @param:Named("reportIdGenerator")
    private val idGenerator: () -> String,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String): TechnicalReportOperationResult<TechnicalReportWithAttachments> {
        return try {
            val existing = reportRepository.getReport(id) ?: return TechnicalReportOperationResult.NotFound
            val client = clientRepository.getClient(existing.report.clientId)
            if (client == null || client.isArchived) return TechnicalReportOperationResult.NotFound
            val now = clock()
            val reportId = idGenerator()
            val reportNumber = reportRepository.nextReportNumber(existing.report.serviceDate)
            val report = existing.report.copy(
                id = reportId,
                reportNumber = reportNumber,
                status = TechnicalReportStatus.DRAFT,
                createdAt = now,
                updatedAt = now,
                isArchived = false,
            )
            val duplicated = TechnicalReportWithAttachments(report, client.displayName(), emptyList())
            reportRepository.saveReport(duplicated.report, duplicated.attachments)
            TechnicalReportOperationResult.Success(duplicated)
        } catch (_: Exception) {
            TechnicalReportOperationResult.StorageError
        }
    }
}

class ArchiveTechnicalReportUseCase @Inject constructor(
    private val reportRepository: TechnicalReportRepository,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String, archived: Boolean): TechnicalReportOperationResult<TechnicalReportWithAttachments> {
        return try {
            val existing = reportRepository.getReport(id) ?: return TechnicalReportOperationResult.NotFound
            val updated = existing.copy(report = existing.report.copy(isArchived = archived, updatedAt = clock()))
            reportRepository.saveReport(updated.report, updated.attachments)
            TechnicalReportOperationResult.Success(updated)
        } catch (_: Exception) {
            TechnicalReportOperationResult.StorageError
        }
    }
}

private fun buildReport(
    id: String,
    reportNumber: String,
    input: TechnicalReportInput,
    status: TechnicalReportStatus,
    createdAt: Long,
    updatedAt: Long,
    isArchived: Boolean,
): TechnicalReport {
    return TechnicalReport(
        id = id,
        reportNumber = reportNumber,
        clientId = input.clientId,
        relatedQuoteId = input.relatedQuoteId,
        title = TechnicalReportTextNormalizer.cleanDisplay(input.title).ifBlank { "Informe $reportNumber" },
        serviceDate = input.serviceDate,
        technicianName = TechnicalReportTextNormalizer.cleanDisplay(input.technicianName),
        deviceOrAsset = TechnicalReportTextNormalizer.cleanDisplay(input.deviceOrAsset),
        problemReported = input.problemReported.trim(),
        diagnosis = input.diagnosis.trim(),
        workPerformed = input.workPerformed.trim(),
        recommendations = input.recommendations.trim(),
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
    )
}

private fun TechnicalReport.editableFingerprint(): List<Any?> {
    return listOf(
        clientId,
        relatedQuoteId,
        title,
        serviceDate,
        technicianName,
        deviceOrAsset,
        problemReported,
        diagnosis,
        workPerformed,
        recommendations,
    )
}

private fun Client.displayName(): String {
    return businessName.ifBlank { fullName }.ifBlank { "Cliente sin nombre" }
}
