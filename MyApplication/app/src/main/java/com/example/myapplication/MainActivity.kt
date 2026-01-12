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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.myapplication.data.CartItemDto
import com.example.myapplication.data.ComicDto
import com.example.myapplication.data.LibraryItemDto
import com.example.myapplication.data.NewsPostDto
import com.example.myapplication.data.resolveImageUrl
import com.example.myapplication.ui.UiState
import com.example.myapplication.ui.theme.BazingaMuted
import com.example.myapplication.ui.theme.BazingaRed
import com.example.myapplication.ui.theme.BazingaSurface
import com.example.myapplication.ui.theme.BazingaSurfaceAlt
import com.example.myapplication.ui.theme.BazingaTextMuted
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import kotlinx.coroutines.delay
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
    val email: String = "",
    val role: String = "",
    val subscriptionType: String? = null,
    val subscriptionExpiration: String? = null
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
    object Home : Screen("home", "Main")
    object Library : Screen("library", "Library")
    object Profile : Screen("profile", "Profile")
    object Cart : Screen("cart", "Cart")
    object News : Screen("news", "News")
    object Checkout : Screen("checkout", "Checkout")
    object Reader : Screen("reader/{id}", "Reader") {
        fun createRoute(id: Long) = "reader/$id"
    }
}

@Composable
fun BazingaApp() {
    val navController = rememberNavController()
    val repository = remember { BazingaRepository(BazingaApiClient.api) }
    var authState by remember { mutableStateOf(AuthState()) }
    var cartState by remember { mutableStateOf<UiState<List<CartItemDto>>>(UiState.Success(emptyList())) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState.token) {
        cartState = if (authState.token.isNotBlank()) {
            repository.fetchCart(authState.token)
        } else {
            UiState.Success(emptyList())
        }
    }

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
                        label = { Text(Screen.Home.label) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Library.route,
                        onClick = { navController.navigate(Screen.Library.route) },
                        icon = { Icon(Icons.Filled.Book, contentDescription = "Library") },
                        label = { Text(Screen.Library.label) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Profile.route,
                        onClick = { navController.navigate(Screen.Profile.route) },
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                        label = { Text(Screen.Profile.label) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Cart.route,
                        onClick = { navController.navigate(Screen.Cart.route) },
                        icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
                        label = { Text(Screen.Cart.label) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.News.route,
                        onClick = { navController.navigate(Screen.News.route) },
                        icon = { Icon(Icons.Filled.Article, contentDescription = "News") },
                        label = { Text(Screen.News.label) }
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
                    onAddToCart = { comic, purchaseType ->
                        if (authState.token.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Sign in to add items to your cart.")
                            }
                        } else {
                            scope.launch {
                                when (val result = repository.addToCart(authState.token, comic.id, purchaseType)) {
                                    is UiState.Success -> {
                                        cartState = result
                                        snackbarHostState.showSnackbar("Added to cart!")
                                    }
                                    is UiState.Error -> snackbarHostState.showSnackbar(result.message)
                                    UiState.Loading -> Unit
                                }
                            }
                        }
                    },
                    onNavigateToLibrary = { navController.navigate(Screen.Library.route) }
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
            composable(Screen.Profile.route) {
                ProfileScreen(
                    authState = authState,
                    onSignOut = {
                        authState = AuthState()
                        cartState = UiState.Success(emptyList())
                        navController.navigate(Screen.Home.route)
                    },
                    onNavigateToLibrary = { navController.navigate(Screen.Library.route) }
                )
            }
            composable(Screen.Cart.route) {
                CartScreen(
                    authState = authState,
                    cartState = cartState,
                    onNavigateHome = { navController.navigate(Screen.Home.route) },
                    onNavigateToLibrary = { navController.navigate(Screen.Library.route) },
                    onCheckout = { navController.navigate(Screen.Checkout.route) },
                    onRefreshCart = {
                        if (authState.token.isNotBlank()) {
                            scope.launch {
                                cartState = repository.fetchCart(authState.token)
                            }
                        }
                    },
                    onUpdateQuantity = { itemId, quantity ->
                        if (authState.token.isNotBlank()) {
                            scope.launch {
                                cartState = repository.updateCartQuantity(authState.token, itemId, quantity)
                            }
                        }
                    },
                    onRemoveItem = { itemId ->
                        if (authState.token.isNotBlank()) {
                            scope.launch {
                                cartState = repository.removeCartItem(authState.token, itemId)
                            }
                        }
                    }
                )
            }
            composable(Screen.News.route) {
                NewsScreen(
                    repository = repository,
                    authState = authState
                )
            }
            composable(Screen.Checkout.route) {
                CheckoutScreen(
                    repository = repository,
                    authState = authState,
                    cartState = cartState,
                    onBackToCart = { navController.navigate(Screen.Cart.route) },
                    onOrderComplete = {
                        scope.launch {
                            cartState = UiState.Success(emptyList())
                            snackbarHostState.showSnackbar("Order placed successfully!")
                            navController.navigate(Screen.Home.route)
                        }
                    },
                    onClearCart = {
                        if (authState.token.isNotBlank()) {
                            scope.launch {
                                cartState = repository.clearCart(authState.token)
                            }
                        }
                    }
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
    onAddToCart: (ComicDto, String) -> Unit,
    onNavigateToLibrary: () -> Unit
) {
    var comicsState by remember { mutableStateOf<UiState<List<ComicDto>>>(UiState.Loading) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var filterState by remember { mutableStateOf(FilterState()) }
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
            onAddToCart = { purchaseType ->
                onAddToCart(selectedComic!!, purchaseType)
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
    onAddToCart: (String) -> Unit
) {
    val price = comic.price ?: 4.99
    val isDigitalExclusive = comic.comicType == "ONLY_DIGITAL"
    val subscriptionType = authState.subscriptionType?.lowercase()
    val isUnlimited = subscriptionType == "unlimited"
    val originalPrice = if (isUnlimited) price * 0.5 else price
    val digitalPrice = if (isUnlimited) 0.0 else price * 0.75
    var purchaseType by remember { mutableStateOf(if (isDigitalExclusive) "DIGITAL" else "ORIGINAL") }

    LaunchedEffect(comic.id) {
        purchaseType = if (isDigitalExclusive) "DIGITAL" else "ORIGINAL"
    }

    val selectedPrice = if (purchaseType == "DIGITAL") digitalPrice else originalPrice

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
                if (isDigitalExclusive) {
                    Text(
                        text = "Digital Exclusive",
                        fontWeight = FontWeight.Bold,
                        color = BazingaRed,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (!isDigitalExclusive) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PurchaseTypeCard(
                            title = "Original Copy",
                            subtitle = if (originalPrice == 0.0) "FREE WITH UNLIMITED" else "$${"%.2f".format(originalPrice)}",
                            isSelected = purchaseType == "ORIGINAL",
                            onClick = { purchaseType = "ORIGINAL" }
                        )
                        PurchaseTypeCard(
                            title = "Digital Copy",
                            subtitle = if (digitalPrice == 0.0) "FREE WITH UNLIMITED" else "$${"%.2f".format(digitalPrice)}",
                            isSelected = purchaseType == "DIGITAL",
                            onClick = { purchaseType = "DIGITAL" }
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (authState.token.isBlank()) {
                TextButton(onClick = onDismiss) {
                    Text("Sign in to buy")
                }
            } else {
                TextButton(onClick = { onAddToCart(purchaseType) }) {
                    Text(
                        text = if (selectedPrice == 0.0) "Add to cart - Free" else "Add to cart - $${"%.2f".format(selectedPrice)}"
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun PurchaseTypeCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val background = if (isSelected) BazingaSurfaceAlt else BazingaSurface
    val borderColor = if (isSelected) BazingaRed else BazingaSurfaceAlt
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, fontSize = 11.sp, color = BazingaTextMuted)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(borderColor)
        )
    }
}

@Composable
private fun ProfileScreen(
    authState: AuthState,
    onSignOut: () -> Unit,
    onNavigateToLibrary: () -> Unit
) {
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
            Text(
                text = "Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            if (authState.token.isBlank()) {
                Text(
                    text = "Sign in to view and manage your Bazinga profile.",
                    color = BazingaTextMuted
                )
                Button(onClick = onNavigateToLibrary, modifier = Modifier.fillMaxWidth()) {
                    Text("Go to Sign In")
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = BazingaSurface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = authState.username, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = authState.email, color = BazingaTextMuted, fontSize = 12.sp)
                        Text(text = "Role: ${authState.role.ifBlank { "USER" }}", color = BazingaTextMuted, fontSize = 12.sp)
                        Text(
                            text = "Subscription: ${authState.subscriptionType ?: "Free"}",
                            color = BazingaTextMuted,
                            fontSize = 12.sp
                        )
                        authState.subscriptionExpiration?.let { expiration ->
                            Text(
                                text = "Renews on $expiration",
                                color = BazingaTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Button(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BazingaSurfaceAlt)
                ) {
                    Text("Sign out")
                }
            }
        }
    }
}

@Composable
private fun CartScreen(
    authState: AuthState,
    cartState: UiState<List<CartItemDto>>,
    onNavigateHome: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onCheckout: () -> Unit,
    onRefreshCart: () -> Unit,
    onUpdateQuantity: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit
) {
    val items = (cartState as? UiState.Success)?.data.orEmpty()
    val totalItems = items.sumOf { it.quantity }
    val totalPrice = items.sumOf { it.unitPrice * it.quantity }

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
                Text(text = "Your Cart", fontSize = 24.sp, fontWeight = FontWeight.Black)
                TextButton(onClick = onRefreshCart) {
                    Text("Refresh")
                }
            }
            if (authState.token.isBlank()) {
                Text(
                    text = "Sign in to manage your cart and checkout.",
                    color = BazingaTextMuted
                )
                Button(onClick = onNavigateToLibrary, modifier = Modifier.fillMaxWidth()) {
                    Text("Sign in")
                }
            } else {
                when (cartState) {
                    UiState.Loading -> LoadingState(message = "Loading cart...")
                    is UiState.Error -> ErrorState(message = (cartState as UiState.Error).message)
                    is UiState.Success -> {
                        if (items.isEmpty()) {
                            Text(text = "Your cart is empty.", color = BazingaTextMuted)
                            Button(onClick = onNavigateHome, modifier = Modifier.fillMaxWidth()) {
                                Text("Continue shopping")
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(items) { item ->
                                    CartItemRow(
                                        item = item,
                                        onIncrease = { onUpdateQuantity(item.id, item.quantity + 1) },
                                        onDecrease = { onUpdateQuantity(item.id, item.quantity - 1) },
                                        onRemove = { onRemoveItem(item.id) }
                                    )
                                }
                            }
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = BazingaSurface
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Items")
                                        Text(text = totalItems.toString())
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Total")
                                        Text(text = formatPrice(totalPrice), fontWeight = FontWeight.Bold)
                                    }
                                    Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
                                        Text("Proceed to Checkout")
                                    }
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
private fun CartItemRow(
    item: CartItemDto,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BazingaSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val image = resolveImageUrl(item.comic.image)
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .width(70.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BazingaSurfaceAlt)
            ) {
                if (image != null) {
                    AsyncImage(
                        model = image,
                        contentDescription = item.comic.title,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.comic.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = item.comic.author ?: "Bazinga Studios", fontSize = 11.sp, color = BazingaTextMuted)
                Text(
                    text = if (item.purchaseType == "DIGITAL") "Digital Copy" else "Original Copy",
                    fontSize = 11.sp,
                    color = BazingaTextMuted
                )
                Text(
                    text = formatPrice(item.unitPrice * item.quantity),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onIncrease) {
                    Icon(Icons.Filled.Add, contentDescription = "Increase quantity")
                }
                Text(text = item.quantity.toString(), fontWeight = FontWeight.Bold)
                IconButton(onClick = onDecrease) {
                    Icon(Icons.Filled.Remove, contentDescription = "Decrease quantity")
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove item", tint = BazingaRed)
            }
        }
    }
}

@Composable
private fun CheckoutScreen(
    repository: BazingaRepository,
    authState: AuthState,
    cartState: UiState<List<CartItemDto>>,
    onBackToCart: () -> Unit,
    onOrderComplete: () -> Unit,
    onClearCart: () -> Unit
) {
    val items = (cartState as? UiState.Success)?.data.orEmpty()
    val totalPrice = items.sumOf { it.unitPrice * it.quantity }
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf(authState.email) }
    var address by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var zip by rememberSaveable { mutableStateOf("") }
    var cardNumber by rememberSaveable { mutableStateOf("") }
    var expiry by rememberSaveable { mutableStateOf("") }
    var cvv by rememberSaveable { mutableStateOf("") }
    var isProcessing by rememberSaveable { mutableStateOf(false) }
    var orderComplete by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (authState.token.isBlank()) {
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
                Text(text = "Checkout", fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text(text = "Sign in to complete your purchase.", color = BazingaTextMuted)
                Button(onClick = onBackToCart, modifier = Modifier.fillMaxWidth()) {
                    Text("Back to cart")
                }
            }
        }
        return
    }

    if (items.isEmpty() && !orderComplete) {
        LaunchedEffect(Unit) {
            onBackToCart()
        }
        return
    }

    if (orderComplete) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Order Complete!", fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text(
                    text = "Thank you for your purchase. Your comics will be delivered soon.",
                    color = BazingaTextMuted
                )
                Button(onClick = onOrderComplete, modifier = Modifier.fillMaxWidth()) {
                    Text("Continue shopping")
                }
            }
        }
        return
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(text = "Checkout", fontSize = 24.sp, fontWeight = FontWeight.Black)
            }
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = BazingaSurface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "Shipping information", fontWeight = FontWeight.Bold)
                        TextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("First name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Last name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = zip,
                            onValueChange = { zip = it },
                            label = { Text("ZIP code") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = BazingaSurface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "Payment details", fontWeight = FontWeight.Bold)
                        TextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = { Text("Card number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TextField(
                                value = expiry,
                                onValueChange = { expiry = it },
                                label = { Text("Expiry") },
                                modifier = Modifier.weight(1f)
                            )
                            TextField(
                                value = cvv,
                                onValueChange = { cvv = it },
                                label = { Text("CVV") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = BazingaSurface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Order summary", fontWeight = FontWeight.Bold)
                        items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.comic.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "${if (item.purchaseType == "DIGITAL") "Digital" else "Original"} · Qty ${item.quantity}",
                                        fontSize = 11.sp,
                                        color = BazingaTextMuted
                                    )
                                }
                                Text(text = formatPrice(item.unitPrice * item.quantity))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total", fontWeight = FontWeight.Bold)
                            Text(text = formatPrice(totalPrice), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        if (isProcessing) return@Button
                        isProcessing = true
                        scope.launch {
                            delay(2000)
                            val digitalItems = items.filter { it.purchaseType == "DIGITAL" }
                            digitalItems.forEach { item ->
                                repository.addToLibrary(authState.token, item.comic.id)
                            }
                            onClearCart()
                            isProcessing = false
                            orderComplete = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing
                ) {
                    Text(text = if (isProcessing) "Processing..." else "Pay ${formatPrice(totalPrice)}")
                }
                TextButton(onClick = onBackToCart, modifier = Modifier.fillMaxWidth()) {
                    Text("Back to cart")
                }
            }
        }
    }
}

@Composable
private fun NewsScreen(
    repository: BazingaRepository,
    authState: AuthState
) {
    var newsState by remember { mutableStateOf<UiState<List<NewsPostDto>>>(UiState.Loading) }
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var isSubmitting by rememberSaveable { mutableStateOf(false) }
    val canPost = authState.role == "ADMIN" || authState.role == "EDITOR"
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        newsState = repository.fetchNews()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Bazinga Newsroom", fontSize = 12.sp, color = BazingaRed, fontWeight = FontWeight.Bold)
                    Text(text = "Latest community news", fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Text(
                        text = "Updates from Bazinga editors and administrators. Posts stay live for seven days.",
                        color = BazingaTextMuted
                    )
                }
            }
            if (canPost) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = BazingaSurface
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "Create a news post", fontWeight = FontWeight.Bold)
                            TextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Title") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            TextField(
                                value = content,
                                onValueChange = { content = it },
                                label = { Text("Details") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4
                            )
                            Button(
                                onClick = {
                                    if (authState.token.isBlank()) return@Button
                                    isSubmitting = true
                                    scope.launch {
                                        when (val result = repository.postNews(authState.token, title, content)) {
                                            is UiState.Success -> {
                                                title = ""
                                                content = ""
                                                newsState = repository.fetchNews()
                                            }
                                            is UiState.Error -> Unit
                                            UiState.Loading -> Unit
                                        }
                                        isSubmitting = false
                                    }
                                },
                                enabled = !isSubmitting
                            ) {
                                Text(text = if (isSubmitting) "Posting..." else "Post news")
                            }
                        }
                    }
                }
            }
            item {
                when (newsState) {
                    UiState.Loading -> LoadingState(message = "Loading news...")
                    is UiState.Error -> ErrorState(message = (newsState as UiState.Error).message)
                    is UiState.Success -> Unit
                }
            }
            if (newsState is UiState.Success) {
                val posts = (newsState as UiState.Success<List<NewsPostDto>>).data
                if (posts.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = BazingaSurface
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "No news yet", fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Check back later for updates from the team.",
                                    color = BazingaTextMuted
                                )
                            }
                        }
                    }
                } else {
                    items(posts) { post ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = BazingaSurface
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = post.title, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Posted by ${post.authorUsername} (${post.authorRole}) · ${formatNewsDate(post.createdAt)} · Expires ${formatNewsDate(post.expiresAt)}",
                                    fontSize = 11.sp,
                                    color = BazingaTextMuted
                                )
                                Text(text = post.content, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
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
                                                    email = result.data.email,
                                                    role = result.data.role,
                                                    subscriptionType = result.data.subscriptionType,
                                                    subscriptionExpiration = result.data.subscriptionExpiration
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
                                            email = result.data.email,
                                            role = result.data.role,
                                            subscriptionType = result.data.subscriptionType,
                                            subscriptionExpiration = result.data.subscriptionExpiration
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

private fun formatPrice(amount: Double): String {
    return "$" + "%.2f".format(amount)
}

private fun formatNewsDate(dateString: String): String {
    return runCatching {
        LocalDateTime.parse(dateString).format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }.getOrDefault(dateString)
}
