package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.CoastalTeal
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.MangoGold
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusDangerBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.ui.theme.SurfaceSubtle

@Composable
fun TomoyoBrandEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, HairlineBorder)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_tomoyo_emblem),
            contentDescription = "Logo resmi TOMOYO EDUCATION",
            contentScale = ContentScale.Fit,
            modifier = Modifier.padding(6.dp)
        )
    }
}

/**
 * Architectural silhouette of Tugu Mangga Indramayu with coastal horizon lines.
 */
@Composable
fun TuguManggaSilhouette(
    modifier: Modifier = Modifier,
    strokeColor: Color = MangoGold.copy(alpha = 0.75f),
    fillColor: Color = MangoGold.copy(alpha = 0.16f)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        // Stepped monument base
        val basePath = Path().apply {
            moveTo(cx - w * 0.36f, h * 0.92f)
            lineTo(cx + w * 0.36f, h * 0.92f)
            lineTo(cx + w * 0.28f, h * 0.80f)
            lineTo(cx - w * 0.28f, h * 0.80f)
            close()
        }
        drawPath(basePath, color = fillColor)
        drawPath(basePath, color = strokeColor, style = Stroke(width = 2f, join = StrokeJoin.Round))

        // Twin curved pillars rising to cradle the mango crown
        val leftPillar = Path().apply {
            moveTo(cx - w * 0.19f, h * 0.80f)
            quadraticTo(cx - w * 0.23f, h * 0.52f, cx - w * 0.08f, h * 0.36f)
        }
        val rightPillar = Path().apply {
            moveTo(cx + w * 0.19f, h * 0.80f)
            quadraticTo(cx + w * 0.23f, h * 0.52f, cx + w * 0.08f, h * 0.36f)
        }
        drawPath(leftPillar, color = strokeColor, style = Stroke(width = 3.2f, cap = StrokeCap.Round))
        drawPath(rightPillar, color = strokeColor, style = Stroke(width = 3.2f, cap = StrokeCap.Round))

        // Central spire inside arch
        drawLine(
            color = strokeColor.copy(alpha = 0.55f),
            start = Offset(cx, h * 0.80f),
            end = Offset(cx, h * 0.42f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )

        // Iconic Indramayu Mango crown silhouette
        val mangoCrown = Path().apply {
            moveTo(cx, h * 0.10f)
            cubicTo(cx + w * 0.16f, h * 0.12f, cx + w * 0.19f, h * 0.28f, cx + w * 0.05f, h * 0.37f)
            cubicTo(cx - w * 0.01f, h * 0.40f, cx - w * 0.13f, h * 0.34f, cx - w * 0.12f, h * 0.22f)
            cubicTo(cx - w * 0.11f, h * 0.14f, cx - w * 0.04f, h * 0.10f, cx, h * 0.10f)
            close()
        }
        drawPath(mangoCrown, color = fillColor)
        drawPath(mangoCrown, color = strokeColor, style = Stroke(width = 2.6f, join = StrokeJoin.Round))

        // Mango leaf accent
        val leafPath = Path().apply {
            moveTo(cx, h * 0.10f)
            quadraticTo(cx + w * 0.09f, h * 0.03f, cx + w * 0.15f, h * 0.09f)
            quadraticTo(cx + w * 0.07f, h * 0.12f, cx, h * 0.10f)
        }
        drawPath(leafPath, color = strokeColor, style = Stroke(width = 2f, join = StrokeJoin.Round))

        // Coastal horizon lines
        drawLine(
            color = strokeColor.copy(alpha = 0.45f),
            start = Offset(w * 0.06f, h * 0.96f),
            end = Offset(w * 0.94f, h * 0.96f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Minimalist Batik Paoman Indramayu geometric divider (coastal pearl & mango-petal rhythm).
 */
@Composable
fun BatikPaomanDivider(
    modifier: Modifier = Modifier,
    darkBackground: Boolean = false
) {
    val lineColor = if (darkBackground) {
        Color.White.copy(alpha = 0.22f)
    } else {
        InkPrimary.copy(alpha = 0.14f)
    }
    val accentColor = if (darkBackground) {
        MangoGold.copy(alpha = 0.72f)
    } else {
        BrandCrimson.copy(alpha = 0.48f)
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        val midY = size.height / 2f
        val midX = size.width / 2f

        drawLine(
            color = lineColor,
            start = Offset(0f, midY),
            end = Offset(midX - 58f, midY),
            strokeWidth = 1.4f
        )
        drawLine(
            color = lineColor,
            start = Offset(midX + 58f, midY),
            end = Offset(size.width, midY),
            strokeWidth = 1.4f
        )

        // Left & right coastal pearls
        drawCircle(color = accentColor, radius = 3f, center = Offset(midX - 46f, midY))
        drawCircle(color = accentColor, radius = 3f, center = Offset(midX + 46f, midY))

        // Left diamond
        val leftDiamond = Path().apply {
            moveTo(midX - 36f, midY)
            lineTo(midX - 24f, midY - 7f)
            lineTo(midX - 12f, midY)
            lineTo(midX - 24f, midY + 7f)
            close()
        }
        drawPath(leftDiamond, color = lineColor, style = Stroke(width = 1.6f))

        // Center Batik Paoman mango-petal motif
        val centerPetal = Path().apply {
            moveTo(midX - 10f, midY)
            quadraticTo(midX, midY - 12f, midX + 10f, midY)
            quadraticTo(midX, midY + 12f, midX - 10f, midY)
            close()
        }
        drawPath(centerPetal, color = accentColor, style = Stroke(width = 1.8f))
        drawCircle(color = accentColor, radius = 2.8f, center = Offset(midX, midY))

        // Right diamond
        val rightDiamond = Path().apply {
            moveTo(midX + 12f, midY)
            lineTo(midX + 24f, midY - 7f)
            lineTo(midX + 36f, midY)
            lineTo(midX + 24f, midY + 7f)
            close()
        }
        drawPath(rightDiamond, color = lineColor, style = Stroke(width = 1.6f))
    }
}

@Composable
fun EditorialCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

enum class StatusTone { SUCCESS, WARNING, DANGER, TEAL, NEUTRAL }

@Composable
fun StatusTag(
    label: String,
    tone: StatusTone,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (tone) {
        StatusTone.SUCCESS -> StatusSuccessBg to StatusSuccess
        StatusTone.WARNING -> StatusWarningBg to StatusWarning
        StatusTone.DANGER -> StatusDangerBg to StatusDanger
        StatusTone.TEAL -> Color(0xFFF0FDFA) to CoastalTeal
        StatusTone.NEUTRAL -> SurfaceSubtle to InkPrimary
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.25f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun LoadingSkeletonCard(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )
    EditorialCard(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .height(14.dp)
                .clip(RoundedCornerShape(6.dp))
                .alpha(alpha)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .alpha(alpha)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(14.dp)
                .clip(RoundedCornerShape(6.dp))
                .alpha(alpha)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    EditorialCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Inbox,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (action != null) {
                Spacer(modifier = Modifier.height(14.dp))
                action()
            }
        }
    }
}

@Composable
fun ErrorStateCard(
    message: String,
    modifier: Modifier = Modifier
) {
    EditorialCard(
        modifier = modifier,
        containerColor = StatusDangerBg,
        borderColor = StatusDanger.copy(alpha = 0.3f)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = "Kesalahan",
                tint = StatusDanger,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = StatusDanger
            )
        }
    }
}
