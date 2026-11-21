package com.rodrigocarreon.rodadalibre.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Small rounded status message (loading, warnings) shown over a screen. */
@Composable
fun StatusPill(
    text: String,
    modifier: Modifier = Modifier,
    showSpinner: Boolean = false,
    iconRes: Int? = null
) {
    // Sizes are the original pill (16/8 dp padding, 16 dp spinner, bodySmall 12 sp) scaled x1.2
    // Live region so TalkBack announces it, like the Snackbar it replaces
    Surface(modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite }, shape = RoundedCornerShape(50), shadowElevation = 5.dp) {
        Row(
            modifier = Modifier.padding(horizontal = 19.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (showSpinner) {
                CircularProgressIndicator(modifier = Modifier.size(19.dp), strokeWidth = 2.4.dp)
            }
            if (iconRes != null) {
                // Decorative: the text already says it, so screen readers skip the icon
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color(0xFFF9A825),
                    modifier = Modifier.size(19.dp)
                )
            }
            Text(text, style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp, lineHeight = 19.sp))
        }
    }
}
