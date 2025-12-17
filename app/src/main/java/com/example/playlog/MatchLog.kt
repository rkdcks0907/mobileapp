package com.example.playlog

import java.util.UUID

enum class GameTitle(val label: String) {
    LOL("리그 오브 레전드"),
    TFT("롤토체스"),
    ETC("기타")
}

enum class Result(val label: String) {
    WIN("승"),
    LOSE("패")
}

enum class Flow(val label: String) {
    EARLY_GOOD("초반 유리"),
    MID_ROUGH("중반 말림"),
    LATE_COMEBACK("후반 역전"),
    CLEAN_WIN("무난한 승리"),
    THREW("던짐/실수로 역전당함")
}

enum class Mood(val label: String) {
    CALM("침착"),
    TILTED("멘탈 나감"),
    CONFIDENT("자신감"),
    TIRED("피곤")
}

data class MatchLog(
    val id: String = UUID.randomUUID().toString(),
    val game: GameTitle = GameTitle.LOL,
    val champion: String = "",
    val result: Result = Result.WIN,
    val flow: Flow = Flow.CLEAN_WIN,
    val mood: Mood = Mood.CALM,
    val memo: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)
