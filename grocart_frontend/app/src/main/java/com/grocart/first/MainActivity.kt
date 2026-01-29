package com.grocart.first

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grocart.first.ui.FirstApp
import com.grocart.first.ui.GroViewModel
import com.grocart.first.data.SessionManager // Ensure this is imported
import com.grocart.first.ui.theme.GrocartFirstTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. SessionManager initialize karein
        val sessionManager = SessionManager(this)

        // 2. ViewModelFactory banayein taaki SessionManager ViewModel ko mil sake
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GroViewModel(sessionManager) as T
            }
        }

        // 3. ViewModel ko Factory ke sath initialize karein
        val groViewModel: GroViewModel by viewModels { viewModelFactory }

        setContent {
            GrocartFirstTheme {
                // A simple Box with a background color ensures the screen isn't black
                // while the internal components are being measured.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    FirstApp(groViewModel = groViewModel)
                }
            }
        }
    }
}