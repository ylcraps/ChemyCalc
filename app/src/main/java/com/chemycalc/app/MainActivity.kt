package com.chemycalc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.chemycalc.app.ui.theme.ChemyCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChemyCalcTheme {
                ChemyCalcApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    ChemyCalcTheme {
        ChemyCalcApp()
    }
}
