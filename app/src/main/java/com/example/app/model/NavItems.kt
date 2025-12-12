package com.example.app.model

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItems(
    val label: String,
    val icon: ImageVector
)
data class NavItemsDrawer(
    val label: String,
    val icon: ImageVector,
    val route: String
)
