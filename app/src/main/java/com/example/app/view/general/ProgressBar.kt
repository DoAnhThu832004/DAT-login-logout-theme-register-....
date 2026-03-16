package com.example.app.view.general

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ProgressClipShape(private val progressFraction: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val clippingRectangle = Rect(
            left = 0f,
            top = 0f,
            right = size.width * progressFraction,
            bottom = size.height
        )
        return Outline.Rectangle(clippingRectangle)
    }
}

@Composable
fun JetpackRoundedProgressBar(
    progressPercentage: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = Color(0xFF2196F3),
    backgroundColor: Color = Color(0xFFE0E0E0),
    progressTextColor: Color = Color.White,
    backgroundTextColor: Color = Color.Black,
    cornerRadiusTopLeft: Dp = 12.dp,
    cornerRadiusTopRight: Dp = 12.dp,
    cornerRadiusBottomRight: Dp = 12.dp,
    cornerRadiusBottomLeft: Dp = 12.dp,
    text: String = "",
    textSize: TextUnit = 14.sp,
    animationDurationMillis: Int = 500
) {
    val normalizedProgress = progressPercentage.coerceIn(0f, 100f) / 100f

    val animatedProgressFraction by animateFloatAsState(
        targetValue = normalizedProgress,
        animationSpec = tween(
            durationMillis = animationDurationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "ProgressAnimation"
    )

    val containerShape = RoundedCornerShape(
        topStart = cornerRadiusTopLeft,
        topEnd = cornerRadiusTopRight,
        bottomEnd = cornerRadiusBottomRight,
        bottomStart = cornerRadiusBottomLeft
    )

    Box(
        modifier = modifier
            .clip(containerShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = animatedProgressFraction)
                .background(progressColor)
                .align(Alignment.CenterStart)
        )

        if (text.isNotEmpty()) {
            Text(
                text = text,
                color = backgroundTextColor,
                fontSize = textSize,
                modifier = Modifier.align(Alignment.Center)
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(ProgressClipShape(animatedProgressFraction)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = progressTextColor,
                    fontSize = textSize
                )
            }
        }
    }
}