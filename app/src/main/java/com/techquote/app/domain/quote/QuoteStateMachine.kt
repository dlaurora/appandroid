package com.techquote.app.domain.quote

object QuoteStateMachine {
    fun canTransition(from: QuoteStatus, to: QuoteStatus): Boolean {
        return to in allowedTargets(from)
    }

    fun allowedTargets(from: QuoteStatus): Set<QuoteStatus> {
        return when (from) {
            QuoteStatus.DRAFT -> setOf(QuoteStatus.SENT, QuoteStatus.CANCELLED)
            QuoteStatus.SENT -> setOf(QuoteStatus.APPROVED, QuoteStatus.REJECTED, QuoteStatus.EXPIRED, QuoteStatus.CANCELLED)
            QuoteStatus.APPROVED -> setOf(QuoteStatus.CANCELLED)
            QuoteStatus.REJECTED,
            QuoteStatus.EXPIRED,
            QuoteStatus.CANCELLED,
            -> emptySet()
        }
    }

    fun canEdit(status: QuoteStatus): Boolean {
        return status == QuoteStatus.DRAFT
    }
}
