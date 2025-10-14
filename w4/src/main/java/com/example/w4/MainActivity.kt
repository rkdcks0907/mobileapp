package com.example.w4

import android.os.Bundle
import android.os.Message
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.w4.ui.theme.Compose_202511016Theme


private val Unit.body: Any
private val Unit.author: Any

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Compose_202511016Theme {
                @Composable
                fun ProfileCard() {
                    // 표시할 메시지 데이터를 생성합니다.
                    val msg = Message("홍길동", "Android 개발자 & Compose 학습자")
                    Row(
                        // Row 자체에 패딩을 주어 다른 Composable과 간격을 둡니다.
                        modifier = Modifier.padding(all = 8.dp)
                    ) {
                        Image(
                            // painterResource를 사용해 drawable 리소스를 불러옵니다.
                            // 이 예제에서는 'profile_picture'라는 이름의 이미지를 사용합니다.
                            // res/drawable 폴더에 해당 이미지가 있어야 합니다.
                            painter = painterResource(R.drawable.profile_picture),
                            contentDescription = "연락처 프로필 사진",
                            modifier = Modifier
                                // size를 사용해 이미지 크기를 40dp로 고정합니다.
                                .size(40.dp)
                                // clip(CircleShape)으로 이미지를 원형으로 자릅니다.
                                .clip(CircleShape)
                        )

                        // 이미지와 텍스트 사이에 수평 간격을 추가합니다.
                        Spacer(modifier = Modifier.width(8.dp))

                        // Column을 사용해 텍스트들을 세로로 배치합니다.
                        Column {
                            Text(
                                text = msg.author,
                                // MaterialTheme의 색상표를 사용해 다크모드에 자동 대응합니다.
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            // 저자와 메시지 내용 사이에 수직 간격을 추가합니다.
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = msg.body,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }



                    }
                }
                    
                }

    private fun Message(string: String, string2: String) {
        TODO("Not yet implemented")
    }
}





