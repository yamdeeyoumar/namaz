package com.namazguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.namazguide.navigation.NamazGuideNavGraph
import com.namazguide.ui.theme.NamazGuideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NamazGuideTheme {
                Surface {
                    NamazGuideNavGraph()
                }
            }
        }
    }
}
