package com.techquote.app.ui.pdf

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.techquote.app.ui.components.ErrorState
import com.techquote.app.ui.components.ListItemCard
import com.techquote.app.ui.components.LoadingState
import com.techquote.app.ui.components.PrimaryButton
import com.techquote.app.ui.components.ScreenContent
import com.techquote.app.ui.components.SecondaryButton
import com.techquote.app.ui.components.SectionHeader
import com.techquote.app.ui.components.TechQuoteScaffold
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.theme.TechQuoteDesign
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun QuotePdfActionsSection(
    uiState: QuotePdfUiState,
    onGeneratePdf: () -> Unit,
    onPreviewPdf: () -> Unit,
    onSharePdf: () -> Unit,
    onSavePdf: () -> Unit,
    onOpenPdf: () -> Unit,
    onRegeneratePdf: () -> Unit,
    onDismissPdfError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
    ) {
        SectionHeader(
            title = "PDF del presupuesto",
            subtitle = "Generación local, previsualización offline y salida segura con content://.",
        )
        when {
            uiState.isGenerating -> PdfGenerationLoadingState()
            uiState.errorMessage != null -> PdfGenerationErrorState(
                message = uiState.errorMessage,
                onRetry = onRegeneratePdf,
                onDismiss = onDismissPdfError,
            )
            uiState.exportUnavailableMessage != null -> QuotePdfExportUnavailableState(
                message = uiState.exportUnavailableMessage,
                onDismiss = onDismissPdfError,
            )
            uiState.ready != null -> PdfDocumentReadyState(ready = uiState.ready)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            PrimaryButton(
                text = if (uiState.ready == null) "Generar PDF" else "Regenerar",
                onClick = if (uiState.ready == null) onGeneratePdf else onRegeneratePdf,
                modifier = Modifier.weight(1f),
                enabled = !uiState.isGenerating,
            )
            SecondaryButton(
                text = "Previsualizar",
                onClick = onPreviewPdf,
                modifier = Modifier.weight(1f),
                enabled = !uiState.isGenerating,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            SecondaryButton(
                text = "Compartir",
                onClick = onSharePdf,
                modifier = Modifier.weight(1f),
                enabled = !uiState.isGenerating,
            )
            SecondaryButton(
                text = "Guardar copia",
                onClick = onSavePdf,
                modifier = Modifier.weight(1f),
                enabled = !uiState.isGenerating,
            )
        }
        SecondaryButton(
            text = "Abrir externo",
            onClick = onOpenPdf,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isGenerating,
        )
    }
}

@Composable
fun PdfGenerationLoadingState(modifier: Modifier = Modifier) {
    LoadingState(
        message = "Generando PDF local...",
        modifier = modifier,
    )
}

@Composable
fun PdfGenerationErrorState(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
    ) {
        ErrorState(
            title = "No se pudo generar el PDF",
            message = message,
            actionLabel = "Reintentar",
            onAction = onRetry,
        )
        SecondaryButton(text = "Cerrar aviso", onClick = onDismiss, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun QuotePdfExportUnavailableState(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItemCard(
        title = "PDF no disponible",
        subtitle = message,
        actionLabel = "Cerrar",
        onClick = onDismiss,
        modifier = modifier,
    )
}

@Composable
fun PdfDocumentReadyState(
    ready: PdfDocumentReadyUiModel,
    modifier: Modifier = Modifier,
) {
    ListItemCard(
        title = ready.fileName,
        subtitle = "Generado ${ready.generatedAtLabel}",
        metadata = "${ready.pageCount} página(s)",
        modifier = modifier,
    )
}

@Composable
fun PdfPreviewScreen(
    uiState: QuotePdfUiState,
    onNavigateBack: () -> Unit,
    onSharePdf: () -> Unit,
    onSavePdf: () -> Unit,
    onOpenPdf: () -> Unit,
    onRegeneratePdf: () -> Unit,
    onDismissPdfError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TechQuoteScaffold(
        title = "Previsualización PDF",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            PdfPreviewToolbar(
                ready = uiState.ready,
                onSharePdf = onSharePdf,
                onSavePdf = onSavePdf,
                onOpenPdf = onOpenPdf,
                onRegeneratePdf = onRegeneratePdf,
            )
            when {
                uiState.isRenderingPreview || uiState.isGenerating -> LoadingState(message = "Preparando previsualización...")
                uiState.errorMessage != null -> PdfGenerationErrorState(
                    message = uiState.errorMessage,
                    onRetry = onRegeneratePdf,
                    onDismiss = onDismissPdfError,
                )
                uiState.previewPages.isEmpty() -> ListItemCard(
                    title = "Sin páginas renderizadas",
                    subtitle = "Generá o regenerá el PDF para previsualizarlo.",
                )
                else -> uiState.previewPages.forEach { page ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = TechQuoteDesign.shapes.small,
                        tonalElevation = TechQuoteDesign.elevations.card,
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Image(
                            bitmap = page.bitmap.asImageBitmap(),
                            contentDescription = "Página ${page.pageIndex + 1} del PDF",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(page.bitmap.width.toFloat() / page.bitmap.height.toFloat())
                                .padding(TechQuoteDesign.spacing.extraSmall),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PdfPreviewToolbar(
    ready: PdfDocumentReadyUiModel?,
    onSharePdf: () -> Unit,
    onSavePdf: () -> Unit,
    onOpenPdf: () -> Unit,
    onRegeneratePdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
    ) {
        if (ready != null) {
            PdfDocumentReadyState(ready = ready)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            SecondaryButton(text = "Compartir", onClick = onSharePdf, modifier = Modifier.weight(1f))
            SecondaryButton(text = "Guardar copia", onClick = onSavePdf, modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            SecondaryButton(text = "Abrir externo", onClick = onOpenPdf, modifier = Modifier.weight(1f))
            SecondaryButton(text = "Regenerar", onClick = onRegeneratePdf, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SavePdfCopyDialog(
    fileName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(text = "Guardar copia PDF") },
        text = { Text(text = "Elegí una ubicación para guardar $fileName.") },
        confirmButton = {
            PrimaryButton(text = "Elegir ubicación", onClick = onConfirm)
        },
        dismissButton = {
            SecondaryButton(text = "Cancelar", onClick = onDismiss)
        },
    )
}

@TechQuotePhonePreviews
@Composable
private fun QuotePdfActionsSectionPreview() {
    TechQuoteTheme {
        QuotePdfActionsSection(
            uiState = previewReadyState(),
            onGeneratePdf = {},
            onPreviewPdf = {},
            onSharePdf = {},
            onSavePdf = {},
            onOpenPdf = {},
            onRegeneratePdf = {},
            onDismissPdfError = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun PdfGenerationLoadingStatePreview() {
    TechQuoteTheme {
        PdfGenerationLoadingState()
    }
}

@TechQuotePhonePreviews
@Composable
private fun PdfGenerationErrorStatePreview() {
    TechQuoteTheme {
        PdfGenerationErrorState(
            message = "No se pudo generar el PDF.",
            onRetry = {},
            onDismiss = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun PdfPreviewScreenPreview() {
    TechQuoteTheme {
        PdfPreviewScreen(
            uiState = previewReadyState(),
            onNavigateBack = {},
            onSharePdf = {},
            onSavePdf = {},
            onOpenPdf = {},
            onRegeneratePdf = {},
            onDismissPdfError = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun PdfPreviewToolbarPreview() {
    TechQuoteTheme {
        PdfPreviewToolbar(
            ready = previewReadyState().ready,
            onSharePdf = {},
            onSavePdf = {},
            onOpenPdf = {},
            onRegeneratePdf = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun SavePdfCopyDialogPreview() {
    TechQuoteTheme {
        SavePdfCopyDialog(
            fileName = "TechQuote_Presupuesto_TQ-2026-000001_Cliente_Demo_2026-06-25.pdf",
            onConfirm = {},
            onDismiss = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun PdfDocumentReadyStatePreview() {
    TechQuoteTheme {
        PdfDocumentReadyState(ready = previewReadyState().ready!!)
    }
}

@TechQuotePhonePreviews
@Composable
private fun QuotePdfExportUnavailableStatePreview() {
    TechQuoteTheme {
        QuotePdfExportUnavailableState(
            message = "El presupuesto no tiene ítems para exportar.",
            onDismiss = {},
        )
    }
}

private fun previewReadyState() = QuotePdfUiState(
    ready = PdfDocumentReadyUiModel(
        fileName = "TechQuote_Presupuesto_TQ-2026-000001_Cliente_Demo_2026-06-25.pdf",
        generatedAtLabel = "2026-06-26 10:00",
        pageCount = 2,
    ),
)
