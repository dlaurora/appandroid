package com.techquote.app.ui.clients

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.techquote.app.domain.client.ClientFieldErrors
import com.techquote.app.ui.theme.TechQuoteTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ClientsScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun clientsListShowsEmptyState() {
        composeRule.setContent {
            TechQuoteTheme {
                ClientsListScreen(
                    uiState = ClientsListUiState(isLoading = false, clients = emptyList()),
                    onSearchQueryChange = {},
                    onNavigateBack = {},
                    onOpenClient = {},
                    onCreateClient = {},
                    onOpenArchived = {},
                    onArchiveClient = {},
                    onRestoreClient = {},
                    onRetry = {},
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("Sin clientes").assertIsDisplayed()
    }

    @Test
    fun clientsListShowsClientContent() {
        composeRule.setContent {
            TechQuoteTheme {
                ClientsListScreen(
                    uiState = ClientsListUiState(
                        isLoading = false,
                        clients = listOf(clientUi(id = "client-1", title = "Cliente Demo Norte")),
                    ),
                    onSearchQueryChange = {},
                    onNavigateBack = {},
                    onOpenClient = {},
                    onCreateClient = {},
                    onOpenArchived = {},
                    onArchiveClient = {},
                    onRestoreClient = {},
                    onRetry = {},
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("Cliente Demo Norte").assertIsDisplayed()
    }

    @Test
    fun clientFormShowsValidationErrorNearIdentityFields() {
        composeRule.setContent {
            TechQuoteTheme {
                ClientFormScreen(
                    uiState = ClientFormUiState(
                        fieldErrors = ClientFieldErrors(identity = "Ingresá un nombre o una empresa."),
                    ),
                    onNavigateBack = {},
                    onFullNameChange = {},
                    onBusinessNameChange = {},
                    onPhoneChange = {},
                    onEmailChange = {},
                    onAddressChange = {},
                    onNotesChange = {},
                    onSave = {},
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("Ingresá un nombre o una empresa.").assertIsDisplayed()
    }

    @Test
    fun clientFormSaveButtonInvokesCallback() {
        var clicked = false
        composeRule.setContent {
            TechQuoteTheme {
                ClientFormScreen(
                    uiState = ClientFormUiState(fullName = "Cliente Demo Norte"),
                    onNavigateBack = {},
                    onFullNameChange = {},
                    onBusinessNameChange = {},
                    onPhoneChange = {},
                    onEmailChange = {},
                    onAddressChange = {},
                    onNotesChange = {},
                    onSave = { clicked = true },
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("Guardar cliente").performClick()

        assertTrue(clicked)
    }

    @Test
    fun clientDetailConfirmsArchiveBeforeCallback() {
        var archived = false
        composeRule.setContent {
            TechQuoteTheme {
                ClientDetailScreen(
                    uiState = ClientDetailUiState(
                        isLoading = false,
                        client = clientUi(id = "client-1", title = "Cliente Demo Norte"),
                    ),
                    onNavigateBack = {},
                    onEditClient = {},
                    onArchiveClient = { archived = true },
                    onRestoreClient = {},
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("Archivar").performClick()
        composeRule.onNodeWithText("Archivar cliente").assertIsDisplayed()
        composeRule.onNodeWithText("Confirmar archivo").performClick()

        assertTrue(archived)
    }

    private fun clientUi(
        id: String,
        title: String,
        isArchived: Boolean = false,
    ) = ClientUiModel(
        id = id,
        title = title,
        subtitle = "Empresa Demo",
        phone = "55550100",
        email = "demo@example.test",
        address = "Zona demo",
        notes = "Nota demo",
        createdAt = 1000L,
        updatedAt = 2000L,
        isArchived = isArchived,
    )
}
