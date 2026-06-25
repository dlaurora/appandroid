package com.techquote.app.ui.model

enum class DemoContentState {
    Content,
    Empty,
    Loading,
    Error,
}

enum class DemoStatus(val label: String) {
    Draft("Borrador"),
    Sent("Enviado"),
    Approved("Aprobado"),
    Rejected("Rechazado"),
    Error("Error"),
    Warning("Advertencia"),
    Success("Correcto"),
}

data class DashboardSummaryUi(
    val monthLabel: String,
    val quotesLabel: String,
    val reportsLabel: String,
    val totalLabel: String,
    val status: DemoStatus,
)

data class QuickActionUi(
    val title: String,
    val description: String,
    val actionLabel: String,
)

data class DemoClientUi(
    val id: String,
    val displayName: String,
    val category: String,
    val note: String,
    val status: DemoStatus,
)

data class DemoCatalogItemUi(
    val id: String,
    val title: String,
    val kind: String,
    val amountLabel: String,
    val description: String,
    val status: DemoStatus,
)

data class DemoQuoteUi(
    val id: String,
    val title: String,
    val clientLabel: String,
    val totalLabel: String,
    val updatedLabel: String,
    val status: DemoStatus,
)

data class DemoReportUi(
    val id: String,
    val title: String,
    val clientLabel: String,
    val updatedLabel: String,
    val status: DemoStatus,
)

data class SettingsItemUi(
    val title: String,
    val description: String,
    val status: DemoStatus? = null,
)

data class LegalDocumentUi(
    val title: String,
    val sections: List<String>,
)
