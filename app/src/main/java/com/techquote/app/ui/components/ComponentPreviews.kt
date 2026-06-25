package com.techquote.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.techquote.app.ui.model.DemoStatus
import com.techquote.app.ui.previews.TechQuoteComponentPreviews
import com.techquote.app.ui.theme.TechQuoteDesign
import com.techquote.app.ui.theme.TechQuoteTheme

@TechQuoteComponentPreviews
@Composable
private fun ButtonsPreview() {
    TechQuoteTheme {
        Column(
            modifier = Modifier.padding(TechQuoteDesign.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            PrimaryButton(text = "PrimaryButton", onClick = {})
            SecondaryButton(text = "SecondaryButton", onClick = {})
            DangerButton(text = "DangerButton", onClick = {})
        }
    }
}

@TechQuoteComponentPreviews
@Composable
private fun StatusChipPreview() {
    TechQuoteTheme {
        Column(
            modifier = Modifier.padding(TechQuoteDesign.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            DemoStatus.entries.forEach { status ->
                StatusChip(status = status)
            }
        }
    }
}

@TechQuoteComponentPreviews
@Composable
private fun CardsPreview() {
    TechQuoteTheme {
        Column(
            modifier = Modifier.padding(TechQuoteDesign.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            QuickActionCard(
                title = "QuickActionCard",
                description = "Acción simulada sin persistencia.",
                actionLabel = "Abrir",
                onClick = {},
            )
            ListItemCard(
                title = "ListItemCard",
                subtitle = "Elemento de lista visual.",
                metadata = "Metadata segura y ficticia.",
                status = DemoStatus.Draft,
                actionLabel = "Detalle",
                onClick = {},
            )
        }
    }
}

@TechQuoteComponentPreviews
@Composable
private fun StatesPreview() {
    TechQuoteTheme {
        Column(
            modifier = Modifier.padding(TechQuoteDesign.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            EmptyState(
                title = "EmptyState",
                message = "Sin datos demo para mostrar.",
                actionLabel = "Crear demo",
                onAction = {},
            )
            LoadingState(message = "LoadingState")
            ErrorState(
                title = "ErrorState",
                message = "Error visual simulado.",
                onAction = {},
            )
        }
    }
}

@TechQuoteComponentPreviews
@Composable
private fun FormFieldsPreview() {
    TechQuoteTheme {
        Column(
            modifier = Modifier.padding(TechQuoteDesign.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            SearchField(value = "", onValueChange = {}, placeholder = "SearchField")
            FormTextField(label = "FormTextField", value = "Texto demo", onValueChange = {})
            CurrencyTextField(label = "CurrencyTextField", value = "12000", onValueChange = {})
            DateField(label = "DateField", value = "2026-06-25", onValueChange = {})
        }
    }
}

@TechQuoteComponentPreviews
@Composable
private fun ConfirmDeleteDialogPreview() {
    TechQuoteTheme {
        ConfirmDeleteDialog(
            title = "ConfirmDeleteDialog",
            message = "Confirmación visual; no se elimina información.",
            confirmLabel = "Eliminar demo",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
