package com.example.tipcalculatorcustomfont

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalculatorcustomfont.ui.theme.TipCalculatorCustomFontTheme
import java.text.NumberFormat
import kotlin.math.ceil
import kotlin.math.max

private fun formatCurrency(value: Double): String =
    NumberFormat.getCurrencyInstance().format(value)

private fun computeTip(
    amount: Double,
    tipPercent: Double,
    customTipAmount: Double?,   // if not null, overrides percentage
    roundUp: Boolean
): Double {
    val rawTip = if (customTipAmount != null) customTipAmount
    else (tipPercent / 100.0) * amount
    val nonNegative = max(rawTip, 0.0)
    return if (roundUp) ceil(nonNegative) else nonNegative
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipCalculatorCustomFontTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TipCalcLayout(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TipCalcLayout(modifier: Modifier = Modifier) {
    var amountInput by remember { mutableStateOf("") }
    var tipPercentInput by remember { mutableStateOf("") }
    var customTipInput by remember { mutableStateOf("") }   // NEW: custom tip ($)
    var roundUp by remember { mutableStateOf(false) }

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipPercentInput.toDoubleOrNull() ?: 20.0
    val customTip = customTipInput.toDoubleOrNull()          // null if blank/invalid

    val tip = computeTip(amount, tipPercent, customTip, roundUp)
    val total = amount + tip

    Column(
        modifier = modifier
            .statusBarsPadding()
            .safeDrawingPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(Alignment.Start)
        )

        EditNumberField(
            label = R.string.bill_amount,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            value = amountInput,
            onValueChange = { amountInput = it },
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        )

        EditNumberField(
            label = R.string.tip_percent,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            value = tipPercentInput,
            onValueChange = { tipPercentInput = it },
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        )

        // NEW: Custom Tip Amount ($) — overrides percentage when filled
        EditNumberField(
            label = R.string.custom_tip_amount,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            value = customTipInput,
            onValueChange = { customTipInput = it },
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        )

        RoundTipRow(
            roundUp = roundUp,
            onRoundUpChange = { roundUp = it },
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.tip_amount, formatCurrency(tip)),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = stringResource(R.string.total_amount, formatCurrency(total)),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(150.dp))
    }
}

@Composable
fun EditNumberField(
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(stringResource(label)) },
        singleLine = true,
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun RoundTipRow(
    roundUp: Boolean,
    onRoundUpChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.round_tip))
        Spacer(Modifier.weight(1f))
        Switch(
            checked = roundUp,
            onCheckedChange = onRoundUpChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TipCalcLayoutPreview() {
    TipCalculatorCustomFontTheme {
        TipCalcLayout()
    }
}