# 🎮 PlayLog

안드로이드 Jetpack Compose로 제작한  
**게임 플레이 기록 관리 앱**입니다.

게임 결과, 플레이 흐름, 컨디션 등을 기록하고  
승패 통계와 요약 정보를 한눈에 확인할 수 있습니다.

---

## 📱 주요 기능

- 게임 플레이 기록 추가
- 승 / 패 결과 기록
- 플레이 흐름 선택 (예: 무난한 승리, 후반 역전 등)
- 컨디션 선택 (침착, 불안 등)
- 메모 작성 기능
- 승패 통계 요약
- 가장 많이 나온 플레이 흐름 / 패배 시 컨디션 분석

---

## 🛠 사용 기술

- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **MVVM 패턴**
- **Navigation Compose**
- **ViewModel**

---

## 📂 프로젝트 구조
```
PlayLog
┣ app
┃ ┣ src/main
┃ ┃ ┣ java/com/example/playlog
┃ ┃ ┃ ┣ MainActivity.kt // 앱 진입점
┃ ┃ ┃ ┣ PlayLogViewModel.kt // 상태 관리 (MVVM)
┃ ┃ ┃ ┣ MatchLog.kt // 게임 기록 데이터 모델
┃ ┃ ┃ ┣ HomeScreen.kt // 기록 목록 / 요약 화면
┃ ┃ ┃ ┣ AddScreen.kt // 기록 추가 화면
┃ ┃ ┃ ┗ LogCard.kt // 기록 카드 UI
┃ ┃ ┣ res // 리소스
┃ ┃ ┗ AndroidManifest.xml
┃ ┣ build.gradle.kts
┣ gradle
┣ build.gradle.kts
┗ settings.gradle.kts
```
