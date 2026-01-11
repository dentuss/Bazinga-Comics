package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.myapplication.data.BazingaApiClient
import com.example.myapplication.data.BazingaRepository
import com.example.myapplication.data.ComicDto
import com.example.myapplication.data.LibraryItemDto
import com.example.myapplication.data.resolveImageUrl
import com.example.myapplication.ui.UiState
import com.example.myapplication.ui.theme.BazingaMuted
import com.example.myapplication.ui.theme.BazingaRed
import com.example.myapplication.ui.theme.BazingaSurface
import com.example.myapplication.ui.theme.BazingaSurfaceAlt
import com.example.myapplication.ui.theme.BazingaTextMuted
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.time.LocalDateTime
import java.time.ZoneId
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BazingaApp()
            }
        }
    }
}

private data class AuthState(
    val token: String = "",
    val username: String = "",
    val email: String = ""
)

private data class FilterState(
    val type: String = "",
    val value: String = ""
)

private data class ComicViewData(
    val id: Long,
    val title: String,
    val creators: String,
    val series: String,
    val character: String,
    val image: String?,
    val comicType: String,
    val createdAt: String?
)

private sealed class Screen(val route: String, val label: String) {
    object Home : Screen("home", "Home")
    object Library : Screen("library", "Library")
    object Reader : Screen("reader/{id}", "Reader") {
        fun createRoute(id: Long) = "reader/$id"
    }
}

@Composable
fun BazingaApp() {
    val navController = rememberNavController()
    val repository = remember { BazingaRepository(BazingaApiClient.api) }
    var authState by rememberSaveable { mutableStateOf(AuthState()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute != Screen.Reader.route) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = { navController.navigate(Screen.Home.route) },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Library.route,
                        onClick = { navController.navigate(Screen.Library.route) },
                        icon = { Icon(Icons.Filled.Book, contentDescription = "Library") },
                        label = { Text("Library") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    repository = repository,
                    authState = authState,
                    onAddToLibrary = { comic ->
                        if (authState.token.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Sign in to add digital comics to your library.")
                            }
                        } else {
                            scope.launch {
                                when (val result = repository.addToLibrary(authState.token, comic.id)) {
                                    is UiState.Success ->
                                        snackbarHostState.showSnackbar("Added to your library!")
                                    is UiState.Error ->
                                        snackbarHostState.showSnackbar(result.message)
                                    UiState.Loading -> Unit
                                }
                            }
                        }
                    },
                    onNavigateToLibrary = { navController.navigate(Screen.Library.route) },
                    onReadComic = { comicId -> navController.navigate(Screen.Reader.createRoute(comicId)) }
                )
            }
            composable(Screen.Library.route) {
                LibraryScreen(
                    repository = repository,
                    authState = authState,
                    onAuthStateChange = { authState = it },
                    onReadComic = { comicId -> navController.navigate(Screen.Reader.createRoute(comicId)) }
                )
            }
            composable(
                Screen.Reader.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val comicId = backStackEntry.arguments?.getLong("id") ?: 0L
                ReaderScreen(
                    repository = repository,
                    comicId = comicId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(
    repository: BazingaRepository,
    authState: AuthState,
    onAddToLibrary: (ComicDto) -> Unit,
    onNavigateToLibrary: () -> Unit,
    onReadComic: (Long) -> Unit
) {
    var comicsState by remember { mutableStateOf<UiState<List<ComicDto>>>(UiState.Loading) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var filterState by rememberSaveable { mutableStateOf(FilterState()) }
    var digitalOnly by rememberSaveable { mutableStateOf(false) }
    var selectedComic by remember { mutableStateOf<ComicDto?>(null) }

    LaunchedEffect(Unit) {
        comicsState = repository.fetchComics()
    }

    val comics = (comicsState as? UiState.Success)?.data.orEmpty()
    val allComics = comics.map {
        ComicViewData(
            id = it.id,
            title = it.title,
            creators = it.author?.ifBlank { "" } ?: "",
            series = it.series?.ifBlank { "" } ?: "",
            character = it.mainCharacter?.ifBlank { "" } ?: "",
            image = it.image,
            comicType = it.comicType ?: "PHYSICAL_COPY",
            createdAt = it.createdAt
        )
    }

    val seriesOptions = listOf("All Series") + allComics.map { it.series }
        .filter { it.isNotBlank() }
        .distinct()
        .sorted()
    val characterOptions = listOf("All Characters") + allComics.map { it.character }
        .filter { it.isNotBlank() }
        .distinct()
        .sorted()
    val creatorOptions = listOf("All Creators") + allComics.map { it.creators }
        .filter { it.isNotBlank() }
        .distinct()
        .sorted()

    val digitalExclusive = allComics.filter { it.comicType == "ONLY_DIGITAL" }

    val filteredComics = run {
        var filtered = if (digitalOnly) digitalExclusive else allComics
        if (searchQuery.isNotBlank()) {
            val query = searchQuery.lowercase()
            filtered = filtered.filter {
                it.title.lowercase().contains(query) ||
                    it.creators.lowercase().contains(query) ||
                    it.series.lowercase().contains(query) ||
                    it.character.lowercase().contains(query)
            }
        }
        if (filterState.value.isNotBlank() && !filterState.value.startsWith("All")) {
            filtered = when (filterState.type) {
                "series" -> filtered.filter { it.series == filterState.value }
                "character" -> filtered.filter { it.character == filterState.value }
                "creator" -> filtered.filter { it.creators.equals(filterState.value, ignoreCase = true) }
                else -> filtered
            }
        }
        filtered
    }

    val isFiltered = searchQuery.isNotBlank() || digitalOnly ||
        (filterState.value.isNotBlank() && !filterState.value.startsWith("All"))

    val newThisWeek = allComics
        .sortedByDescending { parseInstant(it.createdAt) }
        .take(6)
    val digitalRead = digitalExclusive.take(10)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { BazingaHeader(onLibraryClick = onNavigateToLibrary) }
            item { HeroBanner() }
            item { SearchBar(searchQuery = searchQuery, onSearchChange = { searchQuery = it }) }
            item {
                BrowseFilters(
                    seriesOptions = seriesOptions,
                    characterOptions = characterOptions,
                    creatorOptions = creatorOptions,
                    digitalOnly = digitalOnly,
                    onDigitalToggle = { digitalOnly = it },
                    filterState = filterState,
                    onFilterChange = { type, value -> filterState = FilterState(type, value) }
                )
            }
            item {
                when (comicsState) {
                    UiState.Loading -> LoadingState(message = "Loading comics...")
                    is UiState.Error -> ErrorState(message = (comicsState as UiState.Error).message)
                    is UiState.Success -> {
                        if (isFiltered) {
                            FilteredResults(
                                comics = filteredComics,
                                onComicClick = { comic ->
                                    selectedComic = comics.find { it.id == comic.id }
                                }
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                ComicSection(
                                    title = "NEW THIS WEEK",
                                    comics = newThisWeek,
                                    onComicClick = { comic ->
                                        selectedComic = comics.find { it.id == comic.id }
                                    }
                                )
                                ComicSection(
                                    title = "DIGITAL EXCLUSIVE",
                                    comics = digitalRead,
                                    onComicClick = { comic ->
                                        selectedComic = comics.find { it.id == comic.id }
                                    }
                                )
                                UnlimitedBanner()
                                ComicSection(
                                    title = "ALL COMICS",
                                    comics = allComics,
                                    onComicClick = { comic ->
                                        selectedComic = comics.find { it.id == comic.id }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            item { BazingaFooter() }
        }
    }

    if (selectedComic != null) {
        ComicDetailDialog(
            comic = selectedComic!!,
            authState = authState,
            onDismiss = { selectedComic = null },
            onAddToLibrary = {
                onAddToLibrary(selectedComic!!)
                selectedComic = null
            },
            onReadNow = {
                onReadComic(selectedComic!!.id)
                selectedComic = null
            }
        )
    }
}

@Composable
private fun SearchBar(searchQuery: String, onSearchChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Search titles, creators, or series",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search Bazinga comics") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = BazingaSurfaceAlt,
                unfocusedContainerColor = BazingaSurfaceAlt,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun FilterGroup(
    title: String,
    options: List<String>,
    filterState: FilterState,
    onFilterChange: (String, String) -> Unit,
    type: String
) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = BazingaTextMuted
        )
        LazyRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options) { label ->
                FilterChip(
                    selected = filterState.type == type && filterState.value == label,
                    onClick = { onFilterChange(type, label) },
                    label = { Text(text = label, fontSize = 12.sp) }
                )
            }
        }
    }
}

@Composable
private fun BrowseFilters(
    seriesOptions: List<String>,
    characterOptions: List<String>,
    creatorOptions: List<String>,
    digitalOnly: Boolean,
    onDigitalToggle: (Boolean) -> Unit,
    filterState: FilterState,
    onFilterChange: (String, String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Browse by",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = digitalOnly,
                onClick = { onDigitalToggle(!digitalOnly) },
                label = { Text(text = "Digital Exclusive", fontSize = 12.sp) }
            )
            FilterChip(
                selected = !digitalOnly,
                onClick = { onDigitalToggle(false) },
                label = { Text(text = "All Comics", fontSize = 12.sp) }
            )
        }
        FilterGroup(
            title = "Series",
            options = seriesOptions,
            filterState = filterState,
            onFilterChange = onFilterChange,
            type = "series"
        )
        FilterGroup(
            title = "Characters",
            options = characterOptions,
            filterState = filterState,
            onFilterChange = onFilterChange,
            type = "character"
        )
        FilterGroup(
            title = "Creators",
            options = creatorOptions,
            filterState = filterState,
            onFilterChange = onFilterChange,
            type = "creator"
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilteredResults(comics: List<ComicViewData>, onComicClick: (ComicViewData) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FILTERED RESULTS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${comics.size} comics",
                fontSize = 12.sp,
                color = BazingaTextMuted
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            comics.forEach { comic ->
                ComicGridCard(
                    comic = comic,
                    modifier = Modifier.width(160.dp),
                    onClick = { onComicClick(comic) }
                )
            }
        }
    }
}

@Composable
private fun BazingaHeader(onLibraryClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "BAZINGA",
            color = BazingaRed,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp,
            fontSize = 22.sp
        )
        IconButton(onClick = onLibraryClick) {
            Icon(
                imageVector = Icons.Filled.Book,
                contentDescription = "Library",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(BazingaSurfaceAlt, BazingaSurface)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "BAZINGA UNLIMITED",
                fontSize = 12.sp,
                color = BazingaTextMuted,
                fontWeight = FontWeight.SemiBold
            )
            Column {
                Text(
                    text = "Your universe of",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "comics starts here.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Stream exclusive stories and new releases every week.",
                    fontSize = 14.sp,
                    color = BazingaTextMuted,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = {},
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(text = "Start Reading")
                }
            }
        }
    }
}

@Composable
private fun ComicSection(
    title: String,
    comics: List<ComicViewData>,
    onComicClick: (ComicViewData) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "View All",
                fontSize = 12.sp,
                color = BazingaTextMuted
            )
        }
        LazyRow(
            contentPadding = PaddingValues(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(comics) { comic ->
                ComicCard(comic = comic, onClick = { onComicClick(comic) })
            }
        }
    }
}

@Composable
private fun ComicCard(comic: ComicViewData, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BazingaSurface)
        ) {
            val resolvedImage = resolveImageUrl(comic.image)
            if (resolvedImage != null) {
                AsyncImage(
                    model = resolvedImage,
                    contentDescription = comic.title,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(BazingaSurfaceAlt, BazingaSurface)
                            )
                        )
                )
            }
            if (comic.comicType == "ONLY_DIGITAL") {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BazingaRed)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Digital Exclusive",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clip(RoundedCornerShape(12.dp))
            )
            IconButton(
                onClick = onClick,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.Book,
                    contentDescription = "Open comic",
                    tint = Color.White
                )
            }
        }
        Text(
            text = comic.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = comic.creators,
            fontSize = 11.sp,
            color = BazingaTextMuted
        )
    }
}

@Composable
private fun ComicGridCard(comic: ComicViewData, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BazingaSurface)
        ) {
            val resolvedImage = resolveImageUrl(comic.image)
            if (resolvedImage != null) {
                AsyncImage(
                    model = resolvedImage,
                    contentDescription = comic.title,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(BazingaSurfaceAlt, BazingaSurface)
                            )
                        )
                )
            }
            if (comic.comicType == "ONLY_DIGITAL") {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BazingaRed)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Digital Exclusive",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 6.dp)
        ) {
            Text(text = "View Details", fontSize = 12.sp)
        }
    }
}

@Composable
private fun UnlimitedBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BazingaRed)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Unlock Bazinga Unlimited",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Get unlimited access to exclusive digital stories.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 6.dp)
            )
            Button(
                onClick = {},
                modifier = Modifier.padding(top = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = "Join Now", color = BazingaRed)
            }
        }
    }
}

@Composable
private fun BazingaFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "© 2024 BAZINGA COMICS",
            fontSize = 12.sp,
            color = BazingaTextMuted
        )
        Text(
            text = "All heroes. One universe.",
            fontSize = 11.sp,
            color = BazingaMuted,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun LoadingState(message: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(text = message, color = BazingaTextMuted)
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(text = message, color = BazingaRed)
    }
}

@Composable
private fun ComicDetailDialog(
    comic: ComicDto,
    authState: AuthState,
    onDismiss: () -> Unit,
    onAddToLibrary: () -> Unit,
    onReadNow: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Text(text = comic.title, fontWeight = FontWeight.Black, fontSize = 20.sp)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val image = resolveImageUrl(comic.image)
                if (image != null) {
                    AsyncImage(
                        model = image,
                        contentDescription = comic.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Text(text = comic.author ?: "Bazinga Studios", fontSize = 12.sp, color = BazingaTextMuted)
                Text(
                    text = comic.description ?: "Discover the latest adventures from Bazinga Comics.",
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                if (comic.comicType == "ONLY_DIGITAL") {
                    Text(
                        text = "Digital Exclusive",
                        fontWeight = FontWeight.Bold,
                        color = BazingaRed,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            if (comic.comicType == "ONLY_DIGITAL") {
                if (authState.token.isBlank()) {
                    TextButton(onClick = onDismiss) {
                        Text("Sign in to add")
                    }
                } else {
                    TextButton(onClick = onAddToLibrary) {
                        Text("Add to library")
                    }
                }
            }
        },
        dismissButton = {
            if (comic.comicType == "ONLY_DIGITAL" && authState.token.isNotBlank()) {
                TextButton(onClick = onReadNow) {
                    Text("Read now")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LibraryScreen(
    repository: BazingaRepository,
    authState: AuthState,
    onAuthStateChange: (AuthState) -> Unit,
    onReadComic: (Long) -> Unit
) {
    var libraryState by remember { mutableStateOf<UiState<List<LibraryItemDto>>>(UiState.Loading) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var isRegister by rememberSaveable { mutableStateOf(false) }
    var authMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState.token) {
        if (authState.token.isNotBlank()) {
            libraryState = repository.fetchLibrary(authState.token)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Your Library",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (authState.token.isBlank()) {
                Text(
                    text = "Sign in to view your digital collection.",
                    color = BazingaTextMuted
                )
                if (isRegister) {
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(
                    onClick = {
                        authMessage = null
                        if (isRegister) {
                            if (username.isBlank()) {
                                authMessage = "Please enter a username."
                            } else {
                                scope.launch {
                                    when (val result = repository.register(username.trim(), email.trim(), password)) {
                                        is UiState.Success -> {
                                            onAuthStateChange(
                                                AuthState(
                                                    token = result.data.token,
                                                    username = result.data.username,
                                                    email = result.data.email
                                                )
                                            )
                                            authMessage = null
                                        }
                                        is UiState.Error -> authMessage = result.message
                                        UiState.Loading -> Unit
                                    }
                                }
                            }
                        } else {
                            scope.launch {
                                when (val result = repository.login(email.trim(), password)) {
                                    is UiState.Success -> {
                                        onAuthStateChange(
                                            AuthState(
                                                token = result.data.token,
                                                username = result.data.username,
                                                email = result.data.email
                                            )
                                        )
                                        authMessage = null
                                    }
                                    is UiState.Error -> authMessage = result.message
                                    UiState.Loading -> Unit
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isRegister) "Create Account" else "Sign In")
                }
                Button(
                    onClick = {
                        isRegister = !isRegister
                        authMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BazingaSurfaceAlt)
                ) {
                    Text(text = if (isRegister) "Have an account? Sign in" else "New here? Create an account")
                }
                if (authMessage != null) {
                    Text(text = authMessage!!, color = BazingaTextMuted)
                }
            } else {
                when (libraryState) {
                    UiState.Loading -> LoadingState(message = "Loading your library...")
                    is UiState.Error -> ErrorState(message = (libraryState as UiState.Error).message)
                    is UiState.Success -> {
                        val items = (libraryState as UiState.Success<List<LibraryItemDto>>).data
                        if (items.isEmpty()) {
                            Text(
                                text = "Your library is empty. Add a Digital Exclusive comic to start reading.",
                                color = BazingaTextMuted
                            )
                        } else {
                            FlowRow(
                                maxItemsInEachRow = 2,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items.forEach { item ->
                                    LibraryItemCard(
                                        item = item,
                                        modifier = Modifier.width(170.dp),
                                        onRead = { onReadComic(item.comic.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryItemCard(item: LibraryItemDto, modifier: Modifier = Modifier, onRead: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BazingaSurface)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
        ) {
            val image = resolveImageUrl(item.comic.image)
            if (image != null) {
                AsyncImage(
                    model = image,
                    contentDescription = item.comic.title,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(BazingaSurfaceAlt, BazingaSurface)
                            )
                        )
                )
            }
            if (item.comic.comicType == "ONLY_DIGITAL") {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BazingaRed)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Digital Exclusive",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        Text(
            text = item.comic.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = item.comic.author ?: "Bazinga Studios",
            fontSize = 11.sp,
            color = BazingaTextMuted
        )
        Button(
            onClick = onRead,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Read now")
        }
    }
}

@Composable
private fun ReaderScreen(repository: BazingaRepository, comicId: Long, onBack: () -> Unit) {
    var comicsState by remember { mutableStateOf<UiState<List<ComicDto>>>(UiState.Loading) }

    LaunchedEffect(comicId) {
        comicsState = repository.fetchComics()
    }

    val comic = (comicsState as? UiState.Success)?.data?.find { it.id == comicId }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Digital Read",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BazingaRed
                    )
                    Text(
                        text = comic?.title ?: "Bazinga Comic Issue",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = comic?.author ?: "Bazinga Studios",
                        fontSize = 12.sp,
                        color = BazingaTextMuted
                    )
                }
                TextButton(onClick = onBack) {
                    Text(text = "Back")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Page 1",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = comic?.description
                            ?: "Explore the digital-first experience with exclusive panels and story beats.",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
            val image = resolveImageUrl(comic?.image)
            if (image != null) {
                AsyncImage(
                    model = image,
                    contentDescription = comic?.title ?: "Comic cover",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

private fun parseInstant(dateString: String?): Long {
    if (dateString.isNullOrBlank()) {
        return 0L
    }
    return runCatching {
        LocalDateTime.parse(dateString).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }.getOrDefault(0L)
}
