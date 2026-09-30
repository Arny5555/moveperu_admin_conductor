package com.example.moveperu_admin_conductor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.moveperu_admin_conductor.ui.theme.AguaClara
import com.example.moveperu_admin_conductor.ui.theme.Petroleo
import com.example.moveperu_admin_conductor.ui.theme.Senal

@Composable
fun MarcaMovePeruLogo(tamano: Dp = 80.dp, colorHueco: Color = Petroleo) {
    Canvas(modifier = Modifier.size(tamano)) {
        val lado = size.minDimension
        fun p(x: Float, y: Float) = Offset(x / 108f * lado, y / 108f * lado)
        fun l(v: Float) = v / 108f * lado

        drawLine(
            color = Senal,
            start = p(38f, 76f),
            end = p(61f, 58f),
            strokeWidth = l(7f),
            cap = StrokeCap.Round
        )
        val punta = Path().apply {
            moveTo(p(55.6f, 43.9f).x, p(55.6f, 43.9f).y)
            lineTo(p(72.4f, 43.9f).x, p(72.4f, 43.9f).y)
            lineTo(p(64f, 62f).x, p(64f, 62f).y)
            close()
        }
        drawPath(punta, Senal)
        drawCircle(color = Senal, radius = l(13f), center = p(64f, 34f))
        drawCircle(color = colorHueco, radius = l(5.4f), center = p(64f, 34f))
        drawCircle(color = Color.White, radius = l(8f), center = p(38f, 76f))
    }
}

@Composable
fun HeaderMarcaApp(
    subtitulo: String = "Panel de Gestión & Conductores",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MarcaMovePeruLogo(tamano = 56.dp)
        Spacer(Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MovePerú",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Senal
                ) {
                    Text(
                        text = "PRO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = AguaClara
            )
        }
    }
}
