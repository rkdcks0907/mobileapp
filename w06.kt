package com.example.w06

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.w06.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

// -------------------- MainActivity --------------------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BubbleGameScreen()
                }
            }
        }
    }
}

// -------------------- 데이터 클래스 --------------------

data class Bubble(
    val id: Int,
    val color: Color,
    val radius: Float,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val creationTime: Long = System.currentTimeMillis()
)

// -------------------- 게임 상태 --------------------

class GameState {
    var bubbles by mutableStateOf<List<Bubble>>(emptyList())
    var score by mutableStateOf(0)
    var timeLeft by mutableStateOf(30)
    var isGameOver by mutableStateOf(false)

    fun reset() {
        bubbles = emptyList()
        score = 0
        timeLeft = 30
        isGameOver = false
    }
}

// -------------------- 버블 생성 함수 --------------------

fun makeNewBubble(maxWidth: Float, maxHeight: Float): Bubble {
    val id = Random.nextInt()
    val radius = Random.nextInt(30, 60).toFloat()
    val x = Random.nextFloat() * (maxWidth - radius * 2) + radius
    val y = Random.nextFloat() * (maxHeight - radius * 2) + radius
    val vx = Random.nextFloat() * 8f - 4f
    val vy = Random.nextFloat() * 8f - 4f
    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Cyan, Color.Magenta, Color.Yellow
    )
    val color = colors.random()
    return Bubble(id, color, radius, x, y, vx, vy)
}

// -------------------- 버블 위치 업데이트 --------------------

fun updateBubblePositions(
    bubbles: List<Bubble>,
    maxWidth: Float,
    maxHeight: Float
): List<Bubble> {
    val newBubbles = bubbles.map { bubble ->
        var newX = bubble.x + bubble.vx
        var newY = bubble.y + bubble.vy
        var newVx = bubble.vx
        var newVy = bubble.vy

        // 벽 충돌 시 방향 반전
        if (newX - bubble.radius < 0) {
            newX = bubble.radius
            newVx = -newVx
        } else if (newX + bubble.radius > maxWidth) {
            newX = maxWidth - bubble.radius
            newVx = -newVx
        }

        if (newY - bubble.radius < 0) {
            newY = bubble.radius
            newVy = -newVy
        } else if (newY + bubble.radius > maxHeight) {
            newY = maxHeight - bubble.radius
            newVy = -newVy
        }

        bubble.copy(x = newX, y = newY, vx = newVx, vy = newVy)
    }
    return newBubbles
}

// -------------------- UI Composable --------------------

@Composable
fun BubbleGameScreen() {
    val gameState = remember { GameState() }

    // 화면 크기 (px)
    var widthPx by remember { mutableStateOf(0f) }
    var heightPx by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    // 팝 중인 버블 아이디 리스트
    val poppingBubbles = remember { mutableStateListOf<Int>() }

    // 점수 감소 애니메이션 플래그
    var scoreReduced by remember { mutableStateOf(false) }

    // 점수 감소 애니메이션 해제 타이머
    LaunchedEffect(scoreReduced) {
        if (scoreReduced) {
            delay(500)
            scoreReduced = false
        }
    }

    // 타이머 카운트다운
    LaunchedEffect(gameState.isGameOver) {
        if (!gameState.isGameOver) {
            while (gameState.timeLeft > 0) {
                delay(1000L)
                gameState.timeLeft--
            }
            gameState.isGameOver = true
        }
    }

    // 버블 생성 및 위치 업데이트 루프
    LaunchedEffect(key1 = gameState.isGameOver, key2 = widthPx, key3 = heightPx) {
        if (!gameState.isGameOver && widthPx > 0 && heightPx > 0) {
            while (true) {
                delay(16L)
                if (gameState.bubbles.isEmpty()) {
                    gameState.bubbles = List(3) { makeNewBubble(widthPx, heightPx) }
                }
                if (Random.nextFloat() < 0.05f && gameState.bubbles.size < 15) {
                    gameState.bubbles = gameState.bubbles + makeNewBubble(widthPx, heightPx)
                }
                gameState.bubbles = updateBubblePositions(gameState.bubbles, widthPx, heightPx)
            }
        }
    }

    // 점수 감소 UI효과용 alpha
    val scoreAlpha by animateFloatAsState(
        targetValue = if (scoreReduced) 0.4f else 1f,
        animationSpec = tween(durationMillis = 500)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .pointerInput(Unit) {
                detectTapGestures {
                    // 빈 공간 터치 시 점수 1 감소 (0 미만 안 됨)
                    if (!gameState.isGameOver && gameState.score > 0) {
                        gameState.score--
                        scoreReduced = true
                    }
                }
            }
            .onSizeChanged() {
                widthPx = it.width.toFloat()
                heightPx = it.height.toFloat()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상태바 (점수, 시간)
        GameStatusRow(
            score = gameState.score,
            timeLeft = gameState.timeLeft,
            scoreAlpha = scoreAlpha
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (gameState.isGameOver) {
                EndGameScreen(
                    score = gameState.score,
                    onRestart = { gameState.reset() },
                    onExit = { /* 종료 로직 필요하면 추가 */ }
                )
            } else {
                gameState.bubbles.forEach { bubble ->
                    val isPopping = poppingBubbles.contains(bubble.id)
                    BubbleComposable(
                        bubble = bubble,
                        isPopping = isPopping,
                        onPop = {
                            if (!poppingBubbles.contains(it.id)) {
                                poppingBubbles.add(it.id)
                            }
                        }
                    )
                }
            }
        }
    }

    // 팝 상태 관리 및 점수 증가, 버블 제거
    poppingBubbles.forEach { bubbleId ->
        LaunchedEffect(bubbleId) {
            delay(300) // 팝 애니메이션 시간
            gameState.score++
            gameState.bubbles = gameState.bubbles.filterNot { it.id == bubbleId }
            poppingBubbles.remove(bubbleId)
        }
    }
}

@Composable
fun GameStatusRow(score: Int, timeLeft: Int, scoreAlpha: Float = 1f) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Score: $score",
            color = Color.White.copy(alpha = scoreAlpha),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Time: $timeLeft",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BubbleComposable(bubble: Bubble, isPopping: Boolean, onPop: (Bubble) -> Unit) {
    val alphaAnim by animateFloatAsState(
        targetValue = if (isPopping) 0f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    Canvas(
        modifier = Modifier
            .offset { IntOffset(bubble.x.toInt() - bubble.radius.toInt(), bubble.y.toInt() - bubble.radius.toInt()) }
            .size((bubble.radius * 2).dp)
            .alpha(alphaAnim)
            .clickable(enabled = !isPopping) { onPop(bubble) }
    ) {
        drawCircle(
            color = bubble.color,
            radius = bubble.radius
        )
    }
}

@Composable
fun EndGameScreen(score: Int, onRestart: () -> Unit, onExit: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Game Over!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your Score: $score",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Button(onClick = onRestart, modifier = Modifier.padding(8.dp)) {
                    Text("Restart")
                }
                Button(onClick = onExit, modifier = Modifier.padding(8.dp)) {
                    Text("Exit")
                }
            }
        }
    }
}
