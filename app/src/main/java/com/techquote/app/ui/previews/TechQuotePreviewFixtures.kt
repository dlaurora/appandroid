package com.techquote.app.ui.previews

import com.techquote.app.ui.model.DashboardSummaryUi
import com.techquote.app.ui.model.DemoCatalogItemUi
import com.techquote.app.ui.model.DemoClientUi
import com.techquote.app.ui.model.DemoQuoteUi
import com.techquote.app.ui.model.DemoReportUi
import com.techquote.app.ui.model.DemoStatus
import com.techquote.app.ui.model.LegalDocumentUi
import com.techquote.app.ui.model.QuickActionUi
import com.techquote.app.ui.model.SettingsItemUi

object TechQuotePreviewFixtures {
    val dashboardSummary = DashboardSummaryUi(
        monthLabel = "Junio 2026",
        quotesLabel = "8 presupuestos demo",
        reportsLabel = "3 informes demo",
        totalLabel = "$ 148.500 simulados",
        status = DemoStatus.Warning,
    )

    val quickActions = listOf(
        QuickActionUi(
            title = "Nuevo presupuesto",
            description = "Crear un presupuesto local para un cliente activo.",
            actionLabel = "Crear",
        ),
        QuickActionUi(
            title = "Nuevo informe",
            description = "Preparar informe visual con datos ficticios.",
            actionLabel = "Crear demo",
        ),
        QuickActionUi(
            title = "Clientes",
            description = "Gestionar clientes guardados en este dispositivo.",
            actionLabel = "Ver clientes",
        ),
        QuickActionUi(
            title = "Catálogo",
            description = "Gestionar servicios y productos locales.",
            actionLabel = "Ver catálogo",
        ),
    )

    val clients = listOf(
        DemoClientUi(
            id = "client-demo-north",
            displayName = "Cliente Demo Norte",
            category = "Mantenimiento técnico",
            note = "Ficha visual sin contacto real.",
            status = DemoStatus.Success,
        ),
        DemoClientUi(
            id = "client-demo-south",
            displayName = "Cliente Demo Sur",
            category = "Instalación planificada",
            note = "Pendiente de completar datos en fase futura.",
            status = DemoStatus.Warning,
        ),
        DemoClientUi(
            id = "client-demo-central",
            displayName = "Cliente Demo Central",
            category = "Soporte recurrente",
            note = "Datos ficticios para navegación.",
            status = DemoStatus.Draft,
        ),
    )

    val catalog = listOf(
        DemoCatalogItemUi(
            id = "catalog-demo-service",
            title = "Servicio técnico demo",
            kind = "Servicio",
            amountLabel = "$ 24.000",
            description = "Descripción genérica sin cálculo real.",
            status = DemoStatus.Approved,
        ),
        DemoCatalogItemUi(
            id = "catalog-demo-product",
            title = "Producto estándar demo",
            kind = "Producto",
            amountLabel = "$ 18.500",
            description = "Ítem ficticio para probar listas.",
            status = DemoStatus.Draft,
        ),
        DemoCatalogItemUi(
            id = "catalog-demo-kit",
            title = "Kit de instalación demo",
            kind = "Combo visual",
            amountLabel = "$ 42.000",
            description = "No representa inventario real.",
            status = DemoStatus.Warning,
        ),
    )

    val quotes = listOf(
        DemoQuoteUi(
            id = "quote-demo-001",
            title = "Presupuesto demo 001",
            clientLabel = "Cliente Demo Norte",
            totalLabel = "$ 86.500",
            updatedLabel = "Actualizado hoy",
            status = DemoStatus.Draft,
        ),
        DemoQuoteUi(
            id = "quote-demo-002",
            title = "Presupuesto demo 002",
            clientLabel = "Cliente Demo Sur",
            totalLabel = "$ 62.000",
            updatedLabel = "Actualizado ayer",
            status = DemoStatus.Sent,
        ),
        DemoQuoteUi(
            id = "quote-demo-003",
            title = "Presupuesto demo 003",
            clientLabel = "Cliente Demo Central",
            totalLabel = "$ 148.500",
            updatedLabel = "Actualizado esta semana",
            status = DemoStatus.Approved,
        ),
    )

    val reports = listOf(
        DemoReportUi(
            id = "report-demo-001",
            title = "Informe demo de visita",
            clientLabel = "Cliente Demo Norte",
            updatedLabel = "Borrador local",
            status = DemoStatus.Draft,
        ),
        DemoReportUi(
            id = "report-demo-002",
            title = "Informe demo técnico",
            clientLabel = "Cliente Demo Central",
            updatedLabel = "Listo para revisar",
            status = DemoStatus.Success,
        ),
    )

    val settings = listOf(
        SettingsItemUi(
            title = "Tema",
            description = "Seguir sistema, claro u oscuro. Preferencia visual pendiente.",
            status = DemoStatus.Draft,
        ),
        SettingsItemUi(
            title = "Datos locales",
            description = "Clientes, catálogo y presupuestos se guardan en Room privado de la app.",
            status = DemoStatus.Warning,
        ),
        SettingsItemUi(
            title = "Privacidad",
            description = "MVP local-first sin permisos ni red.",
            status = DemoStatus.Success,
        ),
    )

    val privacyPolicy = LegalDocumentUi(
        title = "Política de privacidad",
        sections = listOf(
            "Borrador local para revisión. TechQuote guarda clientes, catálogo y presupuestos localmente en este dispositivo.",
            "La app no usa red, no declara permisos, no integra analíticas, no genera PDFs y no comparte archivos en la fase actual.",
            "Los presupuestos pueden contener datos personales o comerciales según lo que ingrese el usuario.",
            "La política final requiere revisión profesional antes de publicación comercial.",
        ),
    )

    val termsOfUse = LegalDocumentUi(
        title = "Términos de uso",
        sections = listOf(
            "Borrador local para revisión. TechQuote no reemplaza asesoramiento legal, fiscal, contable ni certificaciones profesionales.",
            "Los presupuestos son registros locales de trabajo. No son facturas, recibos, certificados, contratos ni documentos fiscales.",
            "El usuario debe validar precios, impuestos, descuentos, términos y datos de clientes antes de usar un presupuesto.",
            "Los términos finales requieren revisión profesional antes de publicación comercial.",
        ),
    )
}
