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
import androidx.compose.ui.geometry.Size
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
private val BgColor       = Color(0xFF1A0A00)   // very dark orange-brown
private val ButtonIdle    = Color(0xFFFFB300)   // amber yellow
private val ButtonActive  = Color(0xFFFF6D00)   // deep orange
private val GlowColor     = Color(0xFFFFE500)   // bright yellow glow
private val GlowRed       = Color(0xFFFF4500)   // orange-red glow active
private val TextPrimary   = Color(0xFFFFD600)   // bright yellow
private val TextSecondary = Color(0xFFFFAB40)   // orange
private val AccentBlue    = Color(0xFFFFCA28)   // yellow accent

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
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Push-to-play paw button (hold to emit)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(360.dp)
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
                    drawPawButton(circleColor, glowAlpha, isPlaying)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Frequency slider (larger thumb)
            Text(
                text = "FREQUENCY",
                color = TextPrimary,
                fontSize = 13.sp,
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
                    inactiveTrackColor = Color(0xFF3D1F00)
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(AccentBlue, CircleShape)
                    )
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "5 kHz", color = TextPrimary, fontSize = 13.sp)
                Text(text = "22 kHz", color = TextPrimary, fontSize = 13.sp)
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
                color = TextPrimary,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Terms of Use",
                    color = AccentBlue,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.clickable { showTerms = true }
                )
                Text(
                    text = "v${BuildConfig.VERSION_NAME}",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

private fun DrawScope.drawPawButton(fill: Color, glowAlpha: Float, isPlaying: Boolean) {
    val w = size.width
    val h = size.height
    val glow = if (isPlaying) GlowRed else GlowColor

    // Main pad — large oval in lower-center
    val mainCx = w * 0.50f
    val mainCy = h * 0.62f
    val mainRx = w * 0.30f
    val mainRy = h * 0.26f

    // Glow rings around main pad
    if (glowAlpha > 0f) {
        for (ring in 5 downTo 1) {
            val expand = ring * 11f
            drawOval(
                color = glow.copy(alpha = glowAlpha * 0.06f * ring),
                topLeft = Offset(mainCx - mainRx - expand, mainCy - mainRy - expand),
                size = Size((mainRx + expand) * 2f, (mainRy + expand) * 2f)
            )
        }
    }

    // Main palm pad
    drawOval(
        color = glow.copy(alpha = 0.12f + glowAlpha * 0.20f),
        topLeft = Offset(mainCx - mainRx - 4f, mainCy - mainRy - 4f),
        size = Size((mainRx + 4f) * 2f, (mainRy + 4f) * 2f)
    )
    drawOval(
        color = fill,
        topLeft = Offset(mainCx - mainRx, mainCy - mainRy),
        size = Size(mainRx * 2f, mainRy * 2f)
    )

    // 4 toe pads arranged in an arc above the main pad
    val tr = w * 0.105f   // outer toe radius
    val trM = w * 0.118f  // inner toe radius (slightly larger)
    drawCircle(color = fill, radius = tr,  center = Offset(w * 0.185f, h * 0.345f))  // far left
    drawCircle(color = fill, radius = trM, center = Offset(w * 0.370f, h * 0.225f))  // mid left
    drawCircle(color = fill, radius = trM, center = Offset(w * 0.630f, h * 0.225f))  // mid right
    drawCircle(color = fill, radius = tr,  center = Offset(w * 0.815f, h * 0.345f))  // far right
}

@Composable
private fun StepButton(label: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TermsDialog(onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2A1000),
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
                    color = TextPrimary,
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
