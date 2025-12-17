@file:OptIn(ExperimentalMaterial3Api::class)


package com.example.playlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.material3.ExtendedFloatingActionButton


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PlayLogApp() }
    }
}

@Composable
fun PlayLogApp(vm: PlayLogViewModel = viewModel()) {
    val nav = rememberNavController()

    MaterialTheme {
        Scaffold(
            topBar = { TopBar(nav) },
            floatingActionButton = {
                FloatingActionButton(onClick = { nav.navigate("add") }) {
                    Text("기록")
                }



    }
        ) { padding ->
            NavHost(
                navController = nav,
                startDestination = "home",
                modifier = Modifier.padding(padding)
            ) {
                composable("home") { HomeScreen(vm) }
                composable("add") { AddScreen(vm, onDone = { nav.popBackStack() }) }
            }
        }
    }
}

@Composable
private fun TopBar(nav: NavHostController) {
    val route = nav.currentBackStackEntryAsState().value?.destination?.route
    TopAppBar(title = { Text(if (route == "add") "기록 추가" else "PlayLog") })
}

@Composable
fun HomeScreen(vm: PlayLogViewModel) {
    val (win, lose) = vm.stats()
    val topFlow = vm.mostFrequentFlow()
    val loseMood = vm.mostFrequentMoodOnLose()

    val total = vm.logs.size

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("오늘의 요약", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("총 기록: $total")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AssistChip(onClick = {}, label = { Text("승 $win") })
                    AssistChip(onClick = {}, label = { Text("패 $lose") })
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Spacer(Modifier.height(16.dp))

        Text("기록", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        if (vm.logs.isEmpty()) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("아직 기록이 없습니다.")
                    Spacer(Modifier.height(6.dp))
                    Text("오른쪽 아래 버튼으로 첫 기록을 추가해보세요.")
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = vm.logs.size,
                    key = { index -> vm.logs[index].id }
                ) { index ->
                    val log = vm.logs[index]
                    LogCard(log = log, onDelete = { vm.delete(log.id) })
                }
            }
        }
    }
}


@Composable
fun LogCard(log: MatchLog, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "${log.game.label} • ${log.champion.ifBlank { "미입력" }}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "흐름: ${log.flow.label} · 컨디션: ${log.mood.label}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                AssistChip(
                    onClick = {},
                    label = { Text(log.result.label) }
                )
            }

            if (log.memo.isNotBlank()) {
                Text("메모: ${log.memo}", style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDelete) {
                    Text("삭제")
                }
            }
        }
    }
}

@Composable
fun AddScreen(vm: PlayLogViewModel, onDone: () -> Unit) {
    var game by remember { mutableStateOf(GameTitle.LOL) }
    var champion by remember { mutableStateOf("") }
    var result by remember { mutableStateOf(Result.WIN) }
    var flow by remember { mutableStateOf(Flow.CLEAN_WIN) }
    var mood by remember { mutableStateOf(Mood.CALM) }
    var memo by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DropdownRow("게임", game.label, GameTitle.values().map { it.label }) { selected ->
            game = GameTitle.values().find { it.label == selected } ?: game
        }


        OutlinedTextField(
            value = champion,
            onValueChange = { champion = it },
            label = { Text("챔피언/덱/캐릭터") },
            modifier = Modifier.fillMaxWidth()
        )

        SegmentedRow(
            label = "결과",
            left = "승",
            right = "패",
            isLeft = result == Result.WIN,
            onLeft = { result = Result.WIN },
            onRight = { result = Result.LOSE }
        )

        DropdownRow("플레이 흐름", flow.label, Flow.values().map { it.label }) { selected ->
            flow = Flow.values().find { it.label == selected } ?: flow
        }

        DropdownRow("컨디션", mood.label, Mood.values().map { it.label }) { selected ->
            mood = Mood.values().find { it.label == selected } ?: mood
        }


        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            label = { Text("한 줄 메모") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                vm.add(
                    MatchLog(
                        game = game,
                        champion = champion.trim(),
                        result = result,
                        flow = flow,
                        mood = mood,
                        memo = memo.trim()
                    )
                )
                onDone()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("저장") }
    }
}

@Composable
private fun SegmentedRow(
    label: String,
    left: String,
    right: String,
    isLeft: Boolean,
    onLeft: () -> Unit,
    onRight: () -> Unit
) {
    Column {
        Text(label, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = isLeft, onClick = onLeft, label = { Text(left) })
            FilterChip(selected = !isLeft, onClick = onRight, label = { Text(right) })
        }
    }
}

@Composable
private fun DropdownRow(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))

        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Text("▼") }
            )
            Spacer(
                Modifier.matchParentSize().clickable { expanded = true }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = {
                            onSelect(opt)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
