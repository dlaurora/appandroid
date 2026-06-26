package com.techquote.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.techquote.app.ui.theme.TechQuoteDesign

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = TechQuoteDesign.spacing.touchTargetMin),
        placeholder = { Text(text = placeholder) },
        singleLine = true,
        shape = TechQuoteDesign.shapes.small,
    )
}

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = TechQuoteDesign.spacing.touchTargetMin),
        label = { Text(text = label) },
        supportingText = supportingText?.let { { Text(text = it) } },
        isError = isError,
        enabled = enabled,
        shape = TechQuoteDesign.shapes.small,
    )
}

@Composable
fun CurrencyTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = "Importe visual de ejemplo; no calcula totales.",
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = TechQuoteDesign.spacing.touchTargetMin),
        label = { Text(text = label) },
        prefix = { Text(text = "$") },
        supportingText = supportingText?.let { { Text(text = it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = TechQuoteDesign.shapes.small,
    )
}

@Composable
fun DateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = "Fecha escrita manualmente en esta fase.",
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = TechQuoteDesign.spacing.touchTargetMin),
        label = { Text(text = label) },
        supportingText = supportingText?.let { { Text(text = it) } },
        isError = isError,
        enabled = enabled,
        placeholder = { Text(text = "AAAA-MM-DD") },
        singleLine = true,
        shape = TechQuoteDesign.shapes.small,
        textStyle = MaterialTheme.typography.bodyLarge,
    )
}
