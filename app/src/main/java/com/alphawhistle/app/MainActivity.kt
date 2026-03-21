package com.alphawhistle.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

// ── Colors ────────────────────────────────────────────────────────────────────
private val BgColor       = Color(0xFF0A0E1A)
private val ButtonIdle    = Color(0xFF1E2A3E)
private val ButtonActive  = Color(0xFFD32F2F)   // Red when active
private val GlowColor     = Color(0xFF42A5F5)
private val GlowRed       = Color(0xFFEF5350)
private val TextPrimary   = Color(0xFFECF0FF)
private val TextSecondary = Color(0xFF6B82A8)
private val AccentBlue    = Color(0xFF2196F3)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhistleScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhistleScreen(vm: MainViewModel = viewModel()) {
    var showTerms by remember { mutableStateOf(false) }
    val isPlaying = vm.isPlaying
    val frequency = vm.frequency

    val circleColor by animateColorAsState(
        targetValue = if (isPlaying) ButtonActive else ButtonIdle,
        animationSpec = tween(120),
        label = "circleColor"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0f,
        animationSpec = tween(150),
        label = "glowAlpha"
    )

    if (showTerms) {
        TermsDialog(onDismiss = { showTerms = false })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // ── Main content ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App title
            Text(
                text = "Dog Whistle",
                color = TextPrimary,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Frequency readout
            Text(
                text = formatHz(frequency),
                color = TextPrimary,
                fontSize = 42.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isPlaying) "EMITTING" else "READY",
                color = if (isPlaying) GlowRed else TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Push-to-play circle button (340.dp, red when active)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(340.dp)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)
                            vm.startPlaying()
                            waitForUpOrCancellation()
                            vm.stopPlaying()
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawWhistleButton(circleColor, glowAlpha, isPlaying)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isPlaying) "▶" else "◉",
                        color = TextPrimary,
                        fontSize = 36.sp
                    )
                    Text(
                        text = if (isPlaying) "RELEASE" else "HOLD",
                        color = TextPrimary.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Frequency slider (larger thumb)
            Text(
                text = "FREQUENCY",
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = frequency,
                onValueChange = { vm.updateFrequency(it) },
                valueRange = 5000f..22000f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = AccentBlue,
                    activeTrackColor = AccentBlue,
                    inactiveTrackColor = Color(0xFF1E2A3E)
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(AccentBlue, CircleShape)
                    )
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "5 kHz", color = TextSecondary, fontSize = 11.sp)
                Text(text = "22 kHz", color = TextSecondary, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Coarse step buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepButton("−500 Hz") { vm.updateFrequency((frequency - 500f).coerceIn(5000f, 22000f)) }
                StepButton("−100 Hz") { vm.updateFrequency((frequency - 100f).coerceIn(5000f, 22000f)) }
                StepButton("+100 Hz") { vm.updateFrequency((frequency + 100f).coerceIn(5000f, 22000f)) }
                StepButton("+500 Hz") { vm.updateFrequency((frequency + 500f).coerceIn(5000f, 22000f)) }
            }
        }

        // ── Footer ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "© 1st · 2026",
                color = TextSecondary,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "Terms of Use",
                color = AccentBlue,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.clickable { showTerms = true }
            )
        }
    }
}

private fun DrawScope.drawWhistleButton(fill: Color, glowAlpha: Float, isPlaying: Boolean) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = size.minDimension / 2
    val glow = if (isPlaying) GlowRed else GlowColor

    if (glowAlpha > 0f) {
        for (ring in 5 downTo 1) {
            drawCircle(
                color = glow.copy(alpha = glowAlpha * 0.07f * ring),
                radius = radius + ring * 12f,
                center = center
            )
        }
    }

    drawCircle(color = glow.copy(alpha = 0.15f + glowAlpha * 0.25f), radius = radius + 4f, center = center)
    drawCircle(color = fill, radius = radius, center = center)
}

@Composable
private fun StepButton(label: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TermsDialog(onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F1622),
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = "Terms of Use",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Text(
                    text = TERMS_TEXT,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("I Understand", color = AccentBlue)
            }
        }
    )
}

private val TERMS_TEXT = """
Dog Whistle — Terms of Use
Last Updated: 2026

IMPORTANT: Please read these terms carefully before using this application.

1. NO VETERINARY OR PROFESSIONAL ADVICE
This application is provided for general training and entertainment purposes only. The audio tones generated by Dog Whistle do not constitute veterinary advice, diagnosis, or treatment of any kind. Always consult a licensed veterinarian before using any device, sound, or tool to train, calm, or communicate with your animal.

2. CONSULT YOUR VET BEFORE USE
Every animal is different. Before using this app with your pet, check with your veterinarian to ensure high-frequency audio is appropriate for your pet's breed, age, health condition, and hearing sensitivity. Animals with pre-existing hearing conditions, anxiety disorders, or other health issues may react adversely to audio tones.

3. NO LIABILITY
1st ("Company") expressly disclaims all responsibility and liability for any harm, injury, distress, or adverse reaction experienced by any animal, person, or third party arising from the use or misuse of this application. You use this app entirely at your own risk.

4. ANIMAL WELFARE
Monitor your pet closely during use. If your animal shows any signs of distress, discomfort, fear, or aggression — stop use immediately and contact your veterinarian. Do not use this app to deliberately cause distress to animals.

5. HEARING SAFETY
High-frequency tones, even those inaudible to humans, may still affect individuals with hearing aids, cochlear implants, or other auditory devices. Do not play tones at excessive volumes near animals or people. Prolonged exposure to any sound frequency may cause hearing damage.

6. NO GUARANTEE OF RESULTS
The Company makes no representation or warranty that this application will achieve any specific training, behavioral, or medical result. Individual results will vary.

7. CHILDREN AND SUPERVISION
This app should not be used by unsupervised children. Adult supervision is required when using this application around animals.

8. ACCEPTANCE OF TERMS
By continuing to use Dog Whistle, you confirm that you have read, understood, and agreed to these Terms of Use in full.
""".trimIndent()

private fun formatHz(hz: Float): String {
    val n = hz.toInt()
    return if (n >= 1000) {
        String.format(Locale.US, "%,d Hz", n)
    } else {
        "$n Hz"
    }
}
