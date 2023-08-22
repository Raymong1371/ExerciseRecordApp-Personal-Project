package com.example.projectcopyapp

import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.TypeConverter
import com.example.projectcopyapp.data.ExerciseDatabase
import com.example.projectcopyapp.data.ExerciseRecordEntity
import com.example.projectcopyapp.ui.theme.ProjectCopyAppTheme
import com.example.projectcopyapp.ui.theme.Purple40
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectCopyAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Move()
                }
            }
        }
    }
}


class UriTypeConverter {
    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
// 운동 기록화면 컴포저블
fun RecordEx(onSubmit: (ExerciseRecord) -> Unit, onSwitchScreen: () -> Unit) {
    var selectedEmotion by remember { mutableStateOf(Emotion.NORMAL) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var exerciseRecord by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
        }
    )
    //DB 사용하기 위한 변수 선언
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = remember {
        ExerciseDatabase.getDatabase(context)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize()
    ) {
        var month by remember { mutableStateOf(0) }
        var day by remember { mutableStateOf(0) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    // 삭제 버튼을 누를 경우 입력한 값들과 사진 초기화
                    selectedEmotion = Emotion.NORMAL
                    startTime = ""
                    endTime = ""
                    exerciseRecord = ""
                    selectedImageUri = null
                    month = 0
                    day = 0
                },
                modifier = Modifier
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = "삭제", color = Purple40, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    // 완료버튼을 누르면 List 화면에 List Card 생성 및 DB 값 저장
                    onSubmit(
                        ExerciseRecord(
                            month = month,
                            day = day,
                            emotion = selectedEmotion,
                            startTime = startTime,
                            endTime = endTime,
                            exerciseRecord = exerciseRecord,
                            selectedImageUri = selectedImageUri
                        )
                    )
                    val newExercise = ExerciseRecordEntity(
                        month = month, day = day, startTime = startTime,
                        endTime = endTime, exerciseRecord = exerciseRecord,
                        selectedImageUri = selectedImageUri, emotion = selectedEmotion.name
                    )
                    scope.launch(Dispatchers.IO) { db.exerciseDao().insertAll(newExercise) }

                    onSwitchScreen() // 버튼 누름과 동시에 화면이 이동
                },
                modifier = Modifier
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple40)

            ) {
                Text(text = "완료", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        Button(
            onClick = { onSwitchScreen() }, // 버튼 클릭 시 단순 화면 전환
            modifier = Modifier
                .padding(end = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40)
        ) {
            Text(text = "List 화면으로이동")
        }


        Text(
            text = "오늘 헬스장 미션 잘하셨나요?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(width = 1.dp, color = Color.Black, shape = MaterialTheme.shapes.medium),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Emotion.values().forEach { emotion ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            selectedEmotion = emotion
                        }
                        .padding(8.dp)
                        .background(
                            color = if (selectedEmotion == emotion) Color.Gray else Color.Transparent,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Image(
                        painter = painterResource(id = emotion.iconRes),
                        contentDescription = emotion.label,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = emotion.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Divider(color = Color.Black, thickness = 1.dp)
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Start Time Input
            TextField(
                value = month.toString(),
                onValueChange = { month = it.toIntOrNull() ?: 0 },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { keyboardController?.hide() }
                ),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                placeholder = { Text("월 입력") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(5.dp))

            TextField(
                value = day.toString(),
                onValueChange = { day = it.toIntOrNull() ?: 0 },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { keyboardController?.hide() }
                ),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                placeholder = { Text("일 입력") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Start Time Input
            TextField(
                value = startTime,
                onValueChange = { startTime = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { keyboardController?.hide() }
                ),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                placeholder = { Text("시작 시간 입력") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(5.dp))

            TextField(
                value = endTime,
                onValueChange = { endTime = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { keyboardController?.hide() }
                ),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                placeholder = { Text("종료 시간 입력") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
            )
        }

        TextField(
            value = exerciseRecord,
            onValueChange = { exerciseRecord = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
            placeholder = { Text("운동 기록을 상세히 입력") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(150.dp)
                .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
        )

        Row {
            IconButton(onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                Icon(
                    Icons.Default.AccountBox,
                    contentDescription = "Image ComeOn",
                    Modifier.size(60.dp)
                )
            }

            selectedImageUri?.let { uri ->
                val context = LocalContext.current
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(context.contentResolver, uri)
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }

                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .background(Purple40)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "첨부사진",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        Divider(
                            color = MaterialTheme.colorScheme.primary,
                            thickness = 2.dp,
                            modifier = Modifier.padding(end = 16.dp, bottom = 8.dp)
                        )

                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // 이미지 클릭 시 이미지 제거
                                    selectedImageUri = null
                                }
                        )
                    }
                }
            }
        }
    }
}


enum class Emotion(val label: String, val iconRes: Int) {  //이모션 관리 클래스
    FAIL("실패", R.drawable.fail),
    REGRET("아쉬움", R.drawable.regret),
    NORMAL("보통", R.drawable.normal),
    GOOD("좋음", R.drawable.good),
    EXCELLENT("최고", R.drawable.excellent)
}


val selectedDateIndexState = mutableStateOf(1)

@Composable
             // List 화면구성 컴포저블
fun ListEx(
    exerciseList: List<ExerciseRecord>,
    onItemClick: (ExerciseRecord) -> Unit,
    onSwitchScreen: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    selectedDateIndex: Int
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedDateIndex by remember { selectedDateIndexState }

    Column(
        modifier = Modifier.fillMaxSize()
    )
    {
        Button(
            onClick = { onSwitchScreen() }, // 버튼 클릭 시 화면 전환
            modifier = Modifier
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40)
        ) {
            Text(text = "Record 화면으로 이동")
        }
        Spacer(modifier = Modifier.height(8.dp))
        // ListEx 화면 달력
        CalendarComposable(
            modifier = Modifier.fillMaxWidth(),
            selectedDate = selectedDate,
            onDateSelected = { date, index ->
                selectedDate = date
                selectedDateIndex = index
            },
            onPreviousMonthClick = {
                selectedDate = selectedDate.minusMonths(1)
                onPreviousMonthClick()
            },
            onNextMonthClick = {
                selectedDate = selectedDate.plusMonths(1)
                onNextMonthClick()
            }
        )
        Divider(color = Color.Black, thickness = 1.dp)

        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(all = 8.dp),
            state = listState
        ) {

            itemsIndexed(exerciseList) { index, record -> //월,일 을 찾아 해당 화면으로 y축이동
                ListExItem(record = record, onItemClick = onItemClick)
                Divider(color = Color.Black, thickness = 1.dp)

                // 선택한 날짜의 인덱스와 현재 아이템의 인덱스가 같으면 해당 위치로 스크롤 이동
                if (index == selectedDateIndex) {
                    // ScrollToItem으로 해당 인덱스 위치로 스크롤 이동
                    LaunchedEffect(index) {
                        listState.animateScrollToItem(index)
                    }
                }
            }
        }
    }
}

// List 화면구성 입력 틀
@Composable
fun ListExItem(record: ExerciseRecord, onItemClick: (ExerciseRecord) -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onItemClick(record) }

    ) {
        // 상단 바 (월,일)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                // 날짜 표시 (월/일)
                Box(
                    modifier = Modifier
                        .background(Purple40)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "${record.month}월 / ${record.day}일",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Divider(color = Color.Black, thickness = 2.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 감정 아이콘 표시
                    Image(
                        painter = painterResource(id = record.emotion.iconRes),
                        contentDescription = record.emotion.label,
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // 운동 기록 세부사항 표시
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 15.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "시작: ${record.startTime}시 - 종료: ${record.endTime}시",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = record.exerciseRecord.toString(),
                            fontSize = 14.sp
                        )
                    }
                }
                record.selectedImageUri?.let { uri ->
                    val context = LocalContext.current
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(uri, flag)
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(context.contentResolver, uri)
                        )
                    } else {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2.0f) // 사진 가로세로 2:1 비율
                    )
                }
            }
        }
    }
}


@Composable
// 달력구성 컴포저블
fun CalendarComposable(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate, Int) -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousMonthClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }

            val headerText = selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월"))
            Text(
                text = headerText,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
            )

            IconButton(onClick = onNextMonthClick) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "일",
                modifier = Modifier.weight(1f),
                color = Color.Red,
                textAlign = TextAlign.Center
            )
            Text(text = "월", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text(text = "화", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text(text = "수", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text(text = "목", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text(text = "금", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text(
                text = "토",
                modifier = Modifier.weight(1f),
                color = Color.Blue,
                textAlign = TextAlign.Center
            )
        }
        val firstDayOfMonth = selectedDate.withDayOfMonth(1)
        val lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())

        val daysInMonth = (1..lastDayOfMonth.dayOfMonth).toList()
        val emptyDaysBefore = (1 until firstDayOfMonth.dayOfWeek.value).toList()
        val db = ExerciseDatabase.getDatabase(LocalContext.current)
        val exerciseList by db.exerciseDao().getAll().collectAsState(initial = emptyList())


        LazyVerticalGrid(
            GridCells.Fixed(7), // 각 행당 7개의 열을 가지도록 설정
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Display empty boxes for days before the first day of the month
            items(emptyDaysBefore) {
                Spacer(modifier = Modifier.size(30.dp))
            }
            // 날짜 찾아 Y축 이동을 위한 코드
            itemsIndexed(daysInMonth) { index, day ->
                val date = selectedDate.withDayOfMonth(day)
                val isSelected = date == selectedDate
                val hasExerciseRecord =
                    exerciseList.any { it.month == date.monthValue && it.day == date.dayOfMonth }

                CalendarDay(
                    date = date,
                    isSelected = isSelected,
                    hasExerciseRecord = hasExerciseRecord,
                    onDateSelected = { selectedDate ->  // 달력에 날짜를 누를시 동작되는 코드
                        val selectedExerciseRecord = exerciseList.find {
                            it.month == selectedDate.monthValue && it.day == selectedDate.dayOfMonth
                        }
                        val selectedDateIndex =
                            selectedExerciseRecord?.let { exerciseList.indexOf(it) }
                        if (selectedDateIndex != null && selectedDateIndex != selectedDateIndexState.value) {
                            selectedDateIndexState.value = selectedDateIndex
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun CalendarDay(
    date: LocalDate,
    isSelected: Boolean,
    hasExerciseRecord: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .clickable { onDateSelected(date) }
            .background(if (isSelected) Color.Gray else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
        if (hasExerciseRecord) { // 운동 기록이 있는 경우 점을 추가로 표시
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color.Green, CircleShape)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}


@Composable
//화면전환 애니메이터 구현 컴포저블
fun Move() {
    var currentScreen by remember { mutableStateOf(Screen.Record) }
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val exerciseRecords = remember { mutableStateListOf<ExerciseRecord>() }
        val screenWidth = maxWidth

        val transitionOffset = if (currentScreen == Screen.Record) {
            -screenWidth / 50
        } else {
            screenWidth / 50
        }
        val db = ExerciseDatabase.getDatabase(LocalContext.current)
        val exerciseList by db.exerciseDao().getAll().collectAsState(initial = emptyList())
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = transitionOffset)
        ) {
            val scope = rememberCoroutineScope()
            when (currentScreen) {
                Screen.Record -> RecordEx(
                    onSubmit = { record ->
                        exerciseRecords.add(record) // 입력된 데이터를 리스트에 추가
                        currentScreen = Screen.List
                    },
                    onSwitchScreen = {
                        currentScreen = Screen.List
                    }
                )

                Screen.List -> {
                    ListEx(
                        exerciseList = exerciseList.map { entity ->    //데이터 베이스에서 가져온 ExerciseRecordEntity 객체들을 ExerciseRecord 객체로 변환시키는 코드
                            ExerciseRecord(
                                month = entity.month,
                                day = entity.day,
                                emotion = Emotion.valueOf(entity.emotion.toString()),
                                startTime = entity.startTime,
                                endTime = entity.endTime,
                                exerciseRecord = entity.exerciseRecord,
                                selectedImageUri = entity.selectedImageUri
                            )
                        },
                        onItemClick = {    // List에 있는 card 들이 'item' 개념이니 그걸 누를시 동작되는 코드
                            currentScreen = Screen.Record
                        },
                        onSwitchScreen = {
                            currentScreen = Screen.Record
                        },
                        onPreviousMonthClick = {},
                        onNextMonthClick = {},
                        selectedDateIndex = selectedDateIndexState.value
                    )
                }
            }
        }
    }
}

enum class Screen {
    Record, List
}

data class ExerciseRecord(   // 운동기록에 대한 데이터클래스
    val month: Int?,
    val day: Int?,
    val emotion: Emotion,
    val startTime: String?,
    val endTime: String?,
    val exerciseRecord: String?,
    val selectedImageUri: Uri?
)


//@Preview(showBackground = true)
//@Composable
//fun Preview() {
//    RecordEx(
//        onSubmit = {},
//        onSwitchScreen = {}
//    )
//}








