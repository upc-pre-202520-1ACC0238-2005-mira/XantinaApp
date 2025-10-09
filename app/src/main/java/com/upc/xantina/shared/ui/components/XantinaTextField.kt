package com.upc.xantina.shared.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.shared.ui.theme.XantinaCardBackground
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary
import com.upc.xantina.shared.ui.theme.XantinaTextPlaceholder

@Composable
fun XantinaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = XantinaTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = XantinaTextPlaceholder,
                fontSize = 14.sp
            )
        },
        modifier = modifier
            .fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = XantinaCardBackground,
            unfocusedContainerColor = XantinaCardBackground,
            focusedBorderColor = XantinaPrimary,
            unfocusedBorderColor = XantinaTextSecondary.copy(alpha = 0.3f),
            focusedTextColor = XantinaTextPrimary,
            unfocusedTextColor = XantinaTextPrimary,
            errorBorderColor = Color.Red,
            errorTextColor = Color.Red
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        isError = errorMessage != null,
        supportingText = if (errorMessage != null) {
            {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        } else null
    )
}
