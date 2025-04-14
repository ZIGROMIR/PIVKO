package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import java.time.LocalDate
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.material3.TextField
import androidx.compose.ui.text.style.TextAlign
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val title = when (currentRoute) {
        "week" -> "Моя неделя"
        "day" -> "Мой день"
        "calendar" -> "Календарь"
        else -> "Важное"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideBarContent(navController, drawerState)
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(navController = navController, startDestination = "important") {
                    composable("important") { ImportantScreen() }
                    composable("week") { WeekScreen() }
                    composable("day") { PlaceholderScreen("Мой день") }
                    composable("calendar") { CalendarScreen() }
                }
            }
        }
    }
}

@SuppressLint("ServiceCast")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ImportantScreen() {
    var taskText by remember { mutableStateOf("") }
    val tasks = remember { mutableStateListOf<Task>() }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(tasks) { task ->
                TaskCard(
                    task = task,
                    onFavoriteClick = {
                        val index = tasks.indexOf(task)
                        if (index != -1) {
                            tasks[index] = task.copy(isFavorite = !task.isFavorite)
                        }
                    },
                    onLongPress = {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        selectedTask = task
                    }
                )
            }
        }

        selectedTask?.let { task ->
            TaskOptionsMenu(
                task = task,
                onDelete = {
                    tasks.remove(task)
                    selectedTask = null
                },
                onEdit = {
                    taskText = task.text
                    selectedTask = null
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = taskText,
            onValueChange = { taskText = it },
            label = { Text("Ваша задача") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (taskText.isNotBlank()) {
                    tasks.add(Task(text = taskText))
                    taskText = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Добавить задачу", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(96.dp))
    }
}

@Composable
fun TaskCard(task: Task, onFavoriteClick: () -> Unit, onLongPress: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress()
                    }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8D8CFF))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.text,
                fontSize = 16.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (task.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Избранное",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun TaskOptionsMenu(task: Task, onDelete: () -> Unit, onEdit: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text("Выбранная задача: ${task.text}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
                Text("Изменить")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                Text("Удалить")
            }
        }
    }
}

data class Task(
    val text: String,
    val isFavorite: Boolean = false
)

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Здесь будет экран: $title", fontSize = 20.sp, color = Color.Gray)
    }
}

@Composable
fun SideBarContent(navController: NavHostController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(Color(0xFFB0AEEF))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text("Петров", fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Пётр Петрович", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Билет № 09996696", color = Color.White)
            Text("Группа РИ-320938", color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))

            SidebarButton("Важное", Icons.Default.Star) {
                scope.launch {
                    navController.navigate("important") { launchSingleTop = true }
                    drawerState.close()
                }
            }
            SidebarButton("Моя неделя", Icons.Default.DateRange) {
                scope.launch {
                    navController.navigate("week") { launchSingleTop = true }
                    drawerState.close()
                }
            }
            SidebarButton("Мой день", Icons.Default.Today) {
                scope.launch {
                    navController.navigate("day") { launchSingleTop = true }
                    drawerState.close()
                }
            }
            SidebarButton("Календарь", Icons.Default.CalendarToday) {
                scope.launch {
                    navController.navigate("calendar") { launchSingleTop = true }
                    drawerState.close()
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            SidebarButton("Выход", Icons.Default.ExitToApp) {
            }
        }
    }
}

@Composable
fun SidebarButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D8CFF))
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen() {
    var displayedMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val today = remember { LocalDate.now() }

    val firstDayOfWeek = displayedMonth.withDayOfMonth(1).dayOfWeek.value % 7
    val daysInMonth = displayedMonth.lengthOfMonth()

    val leadingDays = if (firstDayOfWeek == 0) 6 else firstDayOfWeek - 1

    val startDate = displayedMonth.minusDays(leadingDays.toLong())

    val days = List(42) { startDate.plusDays(it.toLong()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = displayedMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru")) + " " + displayedMonth.year,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            items(days) { date ->
                val isCurrentMonth = date.month == displayedMonth.month
                val isToday = date == today

                DayItem(
                    day = date.dayOfMonth,
                    color = when {
                        isToday -> Color(0xFF80FFA5)
                        isCurrentMonth -> Color(0xFF8D8CFF)
                        else -> Color.LightGray
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                displayedMonth = displayedMonth.minusMonths(1)
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Предыдущий месяц")
            }

            IconButton(onClick = {
                displayedMonth = displayedMonth.plusMonths(1)
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Следующий месяц")
            }
        }
    }
}

@Composable
fun DayItem(day: Int, color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(4.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { }
    ) {
        Text(
            text = "$day",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekScreen() {
    val today = remember { LocalDate.now() }
    val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val selectedDate = remember { mutableStateOf(today) }

    // Начальные предметы
    val scheduleItems = remember {
        mutableStateOf(
            mutableMapOf(
                DayOfWeek.MONDAY to mutableListOf("Математика", "Физика"),
                DayOfWeek.TUESDAY to mutableListOf("История", "Химия"),
                DayOfWeek.WEDNESDAY to mutableListOf("Литература", "Биология"),
                DayOfWeek.THURSDAY to mutableListOf("Английский", "Информатика"),
                DayOfWeek.FRIDAY to mutableListOf("География", "Иностранные языки"),
                DayOfWeek.SATURDAY to mutableListOf("Спорт", "Математика"),
                DayOfWeek.SUNDAY to mutableListOf("Отдых", "Подготовка")
            )
        )
    }

    // Цвета предметов (по умолчанию синие)
    val itemColors = remember {
        mutableStateOf(
            mutableMapOf<String, Color>().withDefault { Color(0xFF3388FF) }
        )
    }

    // Данные для всплывающего окна
    val pairDetails = remember { mutableStateOf(mutableMapOf<String, Pair<String, String>>()) }

    // Состояния модалки
    var showDialog by remember { mutableStateOf(false) }
    var currentSubject by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // День недели сверху
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(19.dp))
                .background(Color(0xFFA0A9E1))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            daysOfWeek.forEachIndexed { index, day ->
                val date = startOfWeek.plusDays(index.toLong())
                val isToday = date == today
                val isSelected = date == selectedDate.value

                val textColor = when {
                    isToday -> Color(0xFF3388FF)
                    isSelected -> Color(0xFF80D0FF)
                    else -> Color.White
                }

                Text(
                    text = day,
                    color = textColor,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable { selectedDate.value = date }
                )

                if (index != daysOfWeek.lastIndex) {
                    Divider(
                        color = Color.White,
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Контейнер недели
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(19.dp))
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Название дня и дата
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFA0A9E1))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = selectedDate.value.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ru")),
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFA0A9E1))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${selectedDate.value.dayOfMonth} ${selectedDate.value.month.getDisplayName(TextStyle.FULL, Locale("ru"))}",
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Список предметов для выбранного дня
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(scheduleItems.value[selectedDate.value.dayOfWeek] ?: listOf()) { item ->
                        val color = itemColors.value.getValue(item)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(19.dp))
                                .background(color)
                                .clickable {
                                    currentSubject = item
                                    showDialog = true
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            // Меняем цвет при долгом нажатии
                                            itemColors.value[item] = Color(
                                                red = Random.nextFloat() * 0.4f + 0.5f,
                                                green = Random.nextFloat() * 0.4f + 0.5f,
                                                blue = Random.nextFloat() * 0.4f + 0.5f
                                            )
                                        }
                                    )
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                        }
                    }
                }

                // Кнопка "Добавить пару"
                Button(
                    onClick = {
                        val newSubject = "Новая пара ${(0..99).random()}"
                        scheduleItems.value[selectedDate.value.dayOfWeek]?.add(newSubject)
                        // Обновление состояния для перерисовки списка
                        scheduleItems.value = scheduleItems.value.toMutableMap()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D8CFF))
                ) {
                    Text("Добавить пару", color = Color.White)
                }
            }
        }
    }

    // Диалог с информацией о паре
    if (showDialog) {
        var topic by remember { mutableStateOf(pairDetails.value[currentSubject]?.first ?: "") }
        var homework by remember { mutableStateOf(pairDetails.value[currentSubject]?.second ?: "") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    pairDetails.value[currentSubject] = topic to homework
                    showDialog = false
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Отмена")
                }
            },
            title = {
                Text(text = currentSubject)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Тема занятия") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = homework,
                        onValueChange = { homework = it },
                        label = { Text("Домашняя работа") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Кампус: Главный, Аудитория: 201, Преподаватель: Иванов И.И.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            },
            shape = RoundedCornerShape(19.dp),
            containerColor = Color.White
        )
    }
}
