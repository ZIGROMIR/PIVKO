package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

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
    val showNotifications = remember { mutableStateOf(false) }
    val notifications = remember { mutableStateListOf("Напоминание о паре", "Добавлен новый урок") }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val bellOffset = remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val title = when (currentRoute) {
        "week" -> "Моя неделя"
        "day" -> "Мой день"
        "calendar" -> "Календарь"
        "profile" -> "Кабинет студента"
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
                if (currentRoute != "login" && currentRoute != "register") {
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
                            IconButton(
                                onClick = { showNotifications.value = !showNotifications.value },
                                modifier = Modifier.onGloballyPositioned { coordinates ->
                                    bellOffset.value = coordinates.positionInWindow()
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {

                NavHost(navController = navController, startDestination = "day") {
                    composable("login")    { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("day")      { MyDayScreen() }
                    composable("week")     { WeekScreen() }
                    composable("calendar") { CalendarScreen() }
                    composable("profile")  { ProfileScreen() }
                }
            }
        }

                val bellOffsetDp = with(density) {
                    IntOffset(
                        bellOffset.value.x.toDp().roundToPx() - 729,
                        bellOffset.value.y.toDp().roundToPx() - 121
                    )
                }

                AnimatedVisibility(
                    visible = showNotifications.value,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    showNotifications.value = false
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .absoluteOffset { bellOffsetDp }
                        ) {
                            NotificationDrawer(notifications = notifications)
                        }
                    }
                }
            }
        }

@Composable
fun Register(x0: NavHostController) {
    TODO("Not yet implemented")
}

@SuppressLint("ServiceCast")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ImportantScreen() {
    var taskText by remember { mutableStateOf("") }
    val tasks = remember { mutableStateListOf<Task>() }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val vibrator = context.getSystemService(Vibrator::class.java)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(tasks) { index, task ->
                    TaskCard(
                        task = task,
                        onFavoriteClick = {
                            tasks[index] = tasks[index].copy(isFavorite = !task.isFavorite)
                        },
                        onLongPress = {
                            vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                            selectedTask = task
                        }
                    )
                }
            }

            selectedTask?.let { task ->
                TaskOptionsMenu(
                    task = task,
                    onDelete = {
                        val indexToDelete = tasks.indexOf(task)
                        if (indexToDelete != -1) {
                            tasks.removeAt(indexToDelete)
                            if (editingIndex == indexToDelete) {
                                editingIndex = null
                                taskText = ""
                            }
                        }
                        selectedTask = null
                    },
                    onEdit = {
                        val idx = tasks.indexOf(task)
                        if (idx != -1) {
                            editingIndex = idx
                            taskText = task.text
                        }
                        selectedTask = null
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (tasks.isEmpty()) {
                ThinLinesStack()
                Spacer(modifier = Modifier.height(80.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally)
            ) {
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
                            val index = editingIndex
                            if (index != null && index in tasks.indices) {
                                tasks[index] = tasks[index].copy(text = taskText)
                                editingIndex = null
                            } else {
                                tasks.add(Task(text = taskText))
                            }
                            taskText = ""
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (editingIndex != null) "Сохранить изменения" else "Добавить задачу", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}


@Composable
fun ThinLinesStack() {
    val lineColor = Color.Gray.copy(alpha = 0.5f)
    val lineHeight = 1.dp
    val lineCount = 6
    val verticalSpacing = 60.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        repeat(lineCount) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(lineHeight)
                    .background(lineColor)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun TaskCard(task: Task, onFavoriteClick: () -> Unit, onLongPress: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onLongPress() })
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8D8CFF))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        scope.launch {
                            navController.navigate("profile") { launchSingleTop = true }
                            drawerState.close()
                        }
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text("Петров", fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Пётр Петрович", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Билет № 09996696", color = Color.White)
            Text("Группа РИ-320938", color = Color.White)
            Spacer(modifier = Modifier.height(30.dp))

            SidebarButton(
                text = "Важное",
                icon = Icons.Default.Star
            ) {
                scope.launch {
                    navController.navigate("important") { launchSingleTop = true }
                    drawerState.close()
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            SidebarButton(
                text = "Моя неделя",
                iconPainter = painterResource(id = R.drawable.seven)
            ) {
                scope.launch {
                    navController.navigate("week") { launchSingleTop = true }
                    drawerState.close()
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            SidebarButton(
                text = "Мой день",
                icon = Icons.Default.Today
            ) {
                scope.launch {
                    navController.navigate("day") { launchSingleTop = true }
                    drawerState.close()
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            SidebarButton(
                text = "Календарь",
                icon = Icons.Default.CalendarToday
            ) {
                scope.launch {
                    navController.navigate("calendar") { launchSingleTop = true }
                    drawerState.close()
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SidebarButton(
                    text = "Выход",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    modifier = Modifier
                        .weight(3f)
                        .height(48.dp),
                ) {
                    // TODO
                }

                Button(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D8CFF)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconSize: Dp = 20.dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D8CFF)),
        contentPadding = PaddingValues(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(icon, contentDescription = text, modifier = Modifier.size(iconSize), tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SidebarButton(
    text: String,
    iconPainter: Painter,
    modifier: Modifier = Modifier,
    iconSize: Dp = 20.dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D8CFF)),
        contentPadding = PaddingValues(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(painter = iconPainter, contentDescription = text, modifier = Modifier.size(iconSize), tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White, modifier = Modifier.weight(1f))
        }
    }
}




@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "Аватар",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .background(Color(0xFFB7D0F2), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text("Фамилия:  Петров")
                Text("Имя:  Пётр")
                Text("Отчество:  Петрович")
                Text("Институт:  ИРИТ-РтФ")
                Text("Группа:  РИ-320938")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .background(Color(0xFFB7D0F2), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text("Город:  Екатеринбург")
            Text("Почта:  p.petrov@gmail.com")
            Text("Телефон:  8-800-555-35-35")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    var displayedMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val today = remember { LocalDate.now() }
    val firstDayOfWeek = displayedMonth.withDayOfMonth(1).dayOfWeek.value % 7
    val leadingDays = if (firstDayOfWeek == 0) 6 else firstDayOfWeek - 1
    val startDate = displayedMonth.minusDays(leadingDays.toLong())
    val days = List(42) { startDate.plusDays(it.toLong()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = displayedMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru"))
                .replaceFirstChar { it.titlecase() } + " ${displayedMonth.year}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 320.dp)
        ) {
            items(days) { date ->
                val isCurrentMonth = date.month == displayedMonth.month
                val isToday = date == today

                DayItem(
                    day = date.dayOfMonth,
                    color = when {
                        isToday -> Color(0xFFFFC0CB)
                        isCurrentMonth -> Color(0xFF8D8CFF)
                        else -> Color.LightGray
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { displayedMonth = displayedMonth.minusMonths(1) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D8CFF))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Предыдущий месяц")
            }

            Button(
                onClick = { displayedMonth = displayedMonth.plusMonths(1) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D8CFF))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Следующий месяц")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekScreen() {
    val todayDate = remember { LocalDate.now() }
    val startOfWeek = todayDate.with(DayOfWeek.MONDAY)
    val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    var selectedDate by remember { mutableStateOf(todayDate) }

    val scheduleItems = remember {
        mutableStateOf(
            mapOf(
                DayOfWeek.MONDAY to listOf("Математика", "Программирование", "Физика", "История"),
                DayOfWeek.TUESDAY to listOf("Алгоритмы", "Физическая культура", "Иностранный язык"),
                DayOfWeek.WEDNESDAY to listOf("История", "Английский язык", "Математика", "Философия", "Программирование"),
                DayOfWeek.THURSDAY to listOf("Базы данных", "Программирование", "Физика", "Правоведение"),
                DayOfWeek.FRIDAY to listOf("Математический анализ", "Компьютерные сети", "Литература", "Физическая культура"),
                DayOfWeek.SATURDAY to listOf("Проектная деятельность", "Этика делового общения"),
                DayOfWeek.SUNDAY to emptyList()
            )
        )
    }

    val itemColors = remember {
        mutableStateOf(
            mapOf(
                "История" to Color(0xFF8ED6FF),
                "Биология" to Color(0xFF81C784),
                "Математика" to Color(0xFFFFC6F9),
                "Философия" to Color(0xFFFFF4B3),
                "Программирование" to Color(0xFFC7B6FF),
                "Физика" to Color(0xFFAED581),
                "Алгоритмы" to Color(0xFFEF9A9A),
                "Физическая культура" to Color(0xFF90CAF9),
                "Иностранный язык" to Color(0xFFF8B400),
                "Базы данных" to Color(0xFFCE93D8),
                "Правоведение" to Color(0xFFFFCC80),
                "Математический анализ" to Color(0xFF80CBC4),
                "Компьютерные сети" to Color(0xFF81D4FA),
                "Литература" to Color(0xFFF8BBD0),
                "Проектная деятельность" to Color(0xFFD7CCC8),
                "Этика делового общения" to Color(0xFFB39DDB)
            )
        )
    }

    val timeSlots = mapOf(
        "История" to "8:30–10:00",
        "Английский язык" to "10:15–11:45",
        "Математика" to "12:00–13:30",
        "Философия" to "13:30–15:00",
        "Программирование" to "15:15–16:45",
        "Физика" to "17:00–18:30",
        "Алгоритмы" to "8:30–10:00",
        "Физическая культура" to "10:15–11:45",
        "Иностранный язык" to "12:00–13:30",
        "Базы данных" to "8:30–10:00",
        "Правоведение" to "10:15–11:45",
        "Математический анализ" to "8:30–10:00",
        "Компьютерные сети" to "10:15–11:45",
        "Литература" to "12:00–13:30",
        "Проектная деятельность" to "9:00–12:00",
        "Этика делового общения" to "12:30–14:00"
    )

    var showTaskDialog by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
    ) {
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
                val isToday = date == todayDate
                val isSelected = date == selectedDate

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
                        .clickable { selectedDate = date }
                )

                if (index != daysOfWeek.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    text = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ru"))
                        .replaceFirstChar { it.titlecase() },
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
                    text = "${selectedDate.dayOfMonth} ${
                        selectedDate.month.getDisplayName(TextStyle.FULL, Locale("ru"))
                            .replaceFirstChar { it.titlecase() }
                    }",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(scheduleItems.value[selectedDate.dayOfWeek] ?: emptyList()) { subject ->
                val color = itemColors.value[subject] ?: Color.LightGray
                val time = timeSlots[subject] ?: ""
                SubjectCard(subject, time, color) {
                    selectedSubject = subject
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable { showTaskDialog = true }
                .padding(16.dp)
        ) {
            Text("ДЗ...\nМероприятие...", color = Color.Gray)
        }
    }

    if (showTaskDialog) {
        TaskEventDialog(onDismiss = { showTaskDialog = false })
    }

    selectedSubject?.let { subject ->
        SubjectDetailsDialog(
            subject = subject,
            time = timeSlots[subject] ?: "",
            homework = "Прочитать §5", // временные значения
            event = "Тема занятия",
            teacher = "Иванов И.И.",
            room = "Р-221",
            onDismiss = { selectedSubject = null }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEventDialog(onDismiss: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("ДЗ", "Мероприятие")
    val datePickerState = rememberDatePickerState()
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val selectedInstant = datePickerState.selectedDateMillis?.let { Instant.ofEpochMilli(it) }
    val selectedDate = selectedInstant?.atZone(ZoneId.systemDefault())?.toLocalDate()
    var timeText by remember { mutableStateOf("") }

    val subjects = listOf(
        "Математика", "Программирование", "Физика", "История",
        "Алгоритмы", "Физическая культура", "Иностранный язык"
    )
    var selectedSubject by remember { mutableStateOf(subjects.first()) }

    val eventTypes = listOf(
        "Спортивное", "Научное", "Культурное", "Волонтёрское",
        "Академическое", "Внеучебное", "Конференция", "Другое"
    )

    var selectedEventType by remember { mutableStateOf(eventTypes.first()) }
    var inputText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        },
        title = {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
        },
        text = {
            Column {
                if (selectedTab == 0) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSubject,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Предмет") },
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            subjects.forEach { subject ->
                                DropdownMenuItem(
                                    text = { Text(subject) },
                                    onClick = {
                                        selectedSubject = subject
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Домашнее задание") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedDate?.format(dateFormatter) ?: "",
                            onValueChange = {},
                            label = { Text("Дата") },
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = { showDateDialog = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Открыть календарь")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = timeText,
                        onValueChange = { timeText = it },
                        label = { Text("Время (например: 10:00)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedEventType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Тип мероприятия") },
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            eventTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        selectedEventType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Описание мероприятия") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedDate?.format(dateFormatter) ?: "",
                            onValueChange = {},
                            label = { Text("Дата") },
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = { showDateDialog = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Открыть календарь")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = timeText,
                        onValueChange = { timeText = it },
                        label = { Text("Время (например: 10:00)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        }
    )

    if (showDateDialog) {
        DatePickerDialog(
            onDismissRequest = { showDateDialog = false },
            confirmButton = {
                TextButton(onClick = { showDateDialog = false }) {
                    Text("ОК")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateDialog = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SubjectCard(
    subject: String,
    time: String,
    color: Color,
    onClick: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        val availableWidth = maxWidth

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subject,
                color = Color.Black,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(0.7f)
                    .padding(end = 8.dp)
            )

            Text(
                text = time,
                color = Color.DarkGray,
                fontSize = 12.sp,
                modifier = Modifier
                    .width(availableWidth * 0.3f)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyDayScreen() {
    val todayDate = remember { LocalDate.now() }
    val selectedDate = todayDate
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    var showTaskDialog by remember { mutableStateOf(false) }

    val scheduleItems = mapOf(
        DayOfWeek.MONDAY to listOf("Математика", "Программирование", "Физика", "История"),
        DayOfWeek.TUESDAY to listOf("Алгоритмы", "Физическая культура", "Иностранный язык"),
        DayOfWeek.WEDNESDAY to listOf("История", "Английский язык", "Математика", "Философия", "Программирование"),
        DayOfWeek.THURSDAY to listOf("Базы данных", "Программирование", "Физика", "Правоведение"),
        DayOfWeek.FRIDAY to listOf("Математический анализ", "Компьютерные сети", "Литература", "Физическая культура"),
        DayOfWeek.SATURDAY to listOf("Проектная деятельность", "Этика делового общения"),
        DayOfWeek.SUNDAY to emptyList()
    )

    val itemColors = mapOf(
        "История" to Color(0xFF8ED6FF),
        "Биология" to Color(0xFF81C784),
        "Математика" to Color(0xFFFFC6F9),
        "Философия" to Color(0xFFFFF4B3),
        "Программирование" to Color(0xFFC7B6FF),
        "Физика" to Color(0xFFAED581),
        "Алгоритмы" to Color(0xFFEF9A9A),
        "Физическая культура" to Color(0xFF90CAF9),
        "Иностранный язык" to Color(0xFFF8B400),
        "Базы данных" to Color(0xFFCE93D8),
        "Правоведение" to Color(0xFFFFCC80),
        "Математический анализ" to Color(0xFF80CBC4),
        "Компьютерные сети" to Color(0xFF81D4FA),
        "Литература" to Color(0xFFF8BBD0),
        "Проектная деятельность" to Color(0xFFD7CCC8),
        "Этика делового общения" to Color(0xFFB39DDB)
    )

    val timeSlots = mapOf(
        "История" to "8:30–10:00",
        "Английский язык" to "10:15–11:45",
        "Математика" to "12:00–13:30",
        "Философия" to "13:30–15:00",
        "Программирование" to "15:15–16:45",
        "Физика" to "17:00–18:30",
        "Алгоритмы" to "8:30–10:00",
        "Физическая культура" to "10:15–11:45",
        "Иностранный язык" to "12:00–13:30",
        "Базы данных" to "8:30–10:00",
        "Правоведение" to "10:15–11:45",
        "Математический анализ" to "8:30–10:00",
        "Компьютерные сети" to "10:15–11:45",
        "Литература" to "12:00–13:30",
        "Проектная деятельность" to "9:00–12:00",
        "Этика делового общения" to "12:30–14:00"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
    ) {
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
                    text = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ru"))
                        .replaceFirstChar { it.titlecase() },
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
                    text = "${selectedDate.dayOfMonth} ${
                        selectedDate.month.getDisplayName(TextStyle.FULL, Locale("ru"))
                            .replaceFirstChar { it.titlecase() }
                    }",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(scheduleItems[selectedDate.dayOfWeek] ?: emptyList()) { subject ->
                val color = itemColors[subject] ?: Color.LightGray
                val time = timeSlots[subject] ?: "Время не указано"

                SubjectCard(
                    subject = subject,
                    time = time,
                    color = color,
                    onClick = { selectedSubject = subject }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable { showTaskDialog = true }
                .padding(16.dp)
        ) {
            Text("ДЗ...\nМероприятие...", color = Color.Gray)
        }
    }

    selectedSubject?.let { subject ->
        SubjectDetailsDialog(
            subject = subject,
            time = timeSlots[subject] ?: "",
            homework = "Прочитать §5",
            event = "Подготовка к контрольной",
            teacher = "Иванов И.И.",
            room = "Р-221",
            onDismiss = { selectedSubject = null }
        )
    }

    if (showTaskDialog) {
        TaskEventDialog(onDismiss = { showTaskDialog = false })
    }
}

@Composable
fun NotificationDrawer(
    notifications: MutableList<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(280.dp)
            .wrapContentHeight()
            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .background(Color.White),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (notifications.isEmpty()) {
                Text(
                    "Новых уведомлений нет",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                notifications.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item, color = Color.Black, fontSize = 14.sp)
                        TextButton(onClick = { notifications.removeAt(index) }) {
                            Text("Удалить", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                    if (index != notifications.lastIndex) {
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectDetailsDialog(
    subject: String,
    time: String,
    homework: String,
    event: String,
    teacher: String,
    room: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = subject) },
        text = {
            Column {
                Text("Время: $time")
                Text("ДЗ: $homework")
                Text("Тема занятия: $event")
                Text("Аудитория: $room")
                Text("Преподаватель: $teacher")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}