package com.techquote.app.ui.quotes

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.ui.theme.TechQuoteTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class QuotesScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun quotesListShowsEmptyState() {
        composeRule.setContent {
            TechQuoteTheme {
                QuotesListScreen(
                    uiState = QuotesListUiState(isLoading = false),
                    onSearchQueryChange = {},
                    onStatusFilterChange = {},
                    onSortChange = {},
                    onNavigateBack = {},
                    onOpenQuote = {},
                    onCreateQuote = {},
                    onOpenArchived = {},
                    onRetry = {},
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("Sin presupuestos").assertIsDisplayed()
    }

    @Test
    fun quoteDetailShowsStatusActions() {
        composeRule.setContent {
            TechQuoteTheme {
                QuoteDetailScreen(
                    uiState = QuoteDetailUiState(
                        isLoading = false,
                        quote = previewQuoteDetail(),
                    ),
                    onNavigateBack = {},
                    onEditQuote = {},
                    onChangeStatus = {},
                    onDuplicateQuote = {},
                    onArchiveQuote = {},
                    onRestoreQuote = {},
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("Enviar").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("$ 1210.00").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun quoteFormSaveInvokesCallback() {
        var saved = false
        composeRule.setContent {
            TechQuoteTheme {
                QuoteFormScreen(
                    uiState = QuoteFormUiState(
                        clientId = "client-1",
                        clients = listOf(QuoteClientOptionUiModel("client-1", "Cliente Demo Norte")),
                        lineItems = listOf(
                            QuoteLineItemEditorUiState(
                                type = QuoteLineItemType.SERVICE,
                                name = "Servicio demo",
                                quantity = "1",
                                unitPrice = "1000.00",
                                totalLabel = "$ 1000.00",
                            ),
                        ),
                        totalLabel = "$ 1000.00",
                    ),
                    onNavigateBack = {},
                    onClientSelected = {},
                    onTitleChange = {},
                    onDescriptionChange = {},
                    onIssueDateChange = {},
                    onValidUntilChange = {},
                    onDiscountTypeChange = {},
                    onDiscountValueChange = {},
                    onTaxEnabledChange = {},
                    onTaxLabelChange = {},
                    onTaxRateChange = {},
                    onNotesChange = {},
                    onTermsChange = {},
                    onAddServiceItem = {},
                    onAddProductItem = {},
                    onAddManualItem = {},
                    onUpdateLine = { _, _ -> },
                    onRemoveLine = {},
                    onSave = { saved = true },
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("Guardar presupuesto").performScrollTo().performClick()

        assertTrue(saved)
    }
}

private fun previewQuoteDetail() = QuoteDetailUiModel(
    id = "quote-1",
    quoteNumber = "TQ-2026-000001",
    title = "Instalación demo",
    clientLabel = "Cliente Demo Norte",
    status = QuoteStatus.DRAFT,
    issueDate = "2026-06-25",
    validUntil = "2026-07-25",
    subtotalLabel = "$ 1000.00",
    discountLabel = "$ 0.00",
    taxLabel = "IVA 21%: $ 210.00",
    totalLabel = "$ 1210.00",
    notes = "Nota demo",
    termsAndConditions = "Condiciones demo",
    isArchived = false,
    canEdit = true,
    allowedStatuses = listOf(QuoteStatus.SENT, QuoteStatus.CANCELLED),
    items = listOf(
        QuoteLineItemUiModel(
            id = "line-1",
            type = QuoteLineItemType.SERVICE,
            name = "Servicio demo",
            description = "Descripción demo",
            quantityLabel = "1",
            unitPriceLabel = "$ 1000.00",
            discountLabel = "Sin descuento",
            totalLabel = "$ 1000.00",
        ),
    ),
)
