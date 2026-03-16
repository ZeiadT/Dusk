package iti.mad.dusk.ui.presentation.main

import androidx.compose.ui.graphics.vector.ImageVector

data class FabState(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)