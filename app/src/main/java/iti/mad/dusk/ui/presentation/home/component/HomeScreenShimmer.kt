package iti.mad.dusk.ui.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreenShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerBlock(width = 160.dp, height = 14.dp, brush = brush)
            ShimmerBlock(width = 72.dp, height = 22.dp, radius = 20.dp, brush = brush)
        }

        Spacer(Modifier.height(20.dp))

        ShimmerBlock(width = 180.dp, height = 80.dp, radius = 8.dp, brush = brush)
        Spacer(Modifier.height(10.dp))

        ShimmerBlock(width = 120.dp, height = 16.dp, brush = brush)
        Spacer(Modifier.height(6.dp))

        ShimmerBlock(width = 100.dp, height = 13.dp, brush = brush)
        Spacer(Modifier.height(28.dp))

        ShimmerBlock(width = null, height = 1.dp, radius = 1.dp, brush = brush)
        Spacer(Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(5) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ShimmerBlock(width = 20.dp, height = 20.dp, radius = 10.dp, brush = brush)
                    ShimmerBlock(width = 36.dp, height = 13.dp, brush = brush)
                    ShimmerBlock(width = 48.dp, height = 10.dp, brush = brush)
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        ShimmerBlock(width = null, height = 40.dp, radius = 12.dp, brush = brush)
        Spacer(Modifier.height(14.dp))

        ShimmerBlock(width = null, height = 120.dp, radius = 20.dp, brush = brush)

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerBlock(
                width = null,
                height = 48.dp,
                radius = 12.dp,
                brush = brush,
                modifier = Modifier.weight(1f)
            )
            ShimmerBlock(
                width = null,
                height = 48.dp,
                radius = 12.dp,
                brush = brush,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ShimmerBlock(
    height: Dp,
    brush: Brush,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    radius: Dp = 6.dp,
) {
    val base = modifier
        .height(height)
        .clip(RoundedCornerShape(radius))
        .background(brush)

    Box(
        modifier = if (width != null) base.width(width) else base.fillMaxWidth()
    )
}