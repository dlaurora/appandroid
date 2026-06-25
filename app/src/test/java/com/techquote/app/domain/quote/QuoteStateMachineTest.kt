package com.techquote.app.domain.quote

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteStateMachineTest {
    @Test
    fun allowsDocumentedInitialTransitions() {
        assertTrue(QuoteStateMachine.canTransition(QuoteStatus.DRAFT, QuoteStatus.SENT))
        assertTrue(QuoteStateMachine.canTransition(QuoteStatus.DRAFT, QuoteStatus.CANCELLED))
        assertTrue(QuoteStateMachine.canTransition(QuoteStatus.SENT, QuoteStatus.APPROVED))
        assertTrue(QuoteStateMachine.canTransition(QuoteStatus.SENT, QuoteStatus.REJECTED))
        assertTrue(QuoteStateMachine.canTransition(QuoteStatus.SENT, QuoteStatus.EXPIRED))
        assertTrue(QuoteStateMachine.canTransition(QuoteStatus.SENT, QuoteStatus.CANCELLED))
        assertTrue(QuoteStateMachine.canTransition(QuoteStatus.APPROVED, QuoteStatus.CANCELLED))
    }

    @Test
    fun rejectsSilentReturnToDraftAndOtherInvalidTransitions() {
        assertFalse(QuoteStateMachine.canTransition(QuoteStatus.REJECTED, QuoteStatus.DRAFT))
        assertFalse(QuoteStateMachine.canTransition(QuoteStatus.EXPIRED, QuoteStatus.DRAFT))
        assertFalse(QuoteStateMachine.canTransition(QuoteStatus.APPROVED, QuoteStatus.SENT))
        assertFalse(QuoteStateMachine.canTransition(QuoteStatus.CANCELLED, QuoteStatus.SENT))
    }

    @Test
    fun onlyDraftQuotesAreFullyEditable() {
        assertTrue(QuoteStateMachine.canEdit(QuoteStatus.DRAFT))
        assertFalse(QuoteStateMachine.canEdit(QuoteStatus.SENT))
        assertFalse(QuoteStateMachine.canEdit(QuoteStatus.APPROVED))
    }
}
