package com.techquote.app.domain.report

object TechnicalReportStateMachine {
    fun canTransition(from: TechnicalReportStatus, to: TechnicalReportStatus): Boolean {
        return to in allowedTargets(from)
    }

    fun allowedTargets(from: TechnicalReportStatus): Set<TechnicalReportStatus> {
        return when (from) {
            TechnicalReportStatus.DRAFT -> setOf(TechnicalReportStatus.COMPLETED, TechnicalReportStatus.CANCELLED)
            TechnicalReportStatus.COMPLETED -> setOf(TechnicalReportStatus.CANCELLED)
            TechnicalReportStatus.CANCELLED -> emptySet()
        }
    }

    fun canEdit(status: TechnicalReportStatus): Boolean {
        return status == TechnicalReportStatus.DRAFT
    }
}
