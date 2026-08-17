package com.example.dailytask.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.dailytask.ui.viewmodel.DailyTaskViewModel

@Composable
fun MainScreen(
    viewModel: DailyTaskViewModel,
    modifier: Modifier = Modifier
) {
    TodayScreen(
        viewModel = viewModel,
        modifier = modifier.fillMaxSize()
    )
}


