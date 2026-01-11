package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import com.example.myapplication.ui.theme.BazingaMuted
import com.example.myapplication.ui.theme.BazingaRed
import com.example.myapplication.ui.theme.BazingaSurface
import com.example.myapplication.ui.theme.BazingaSurfaceAlt
import com.example.myapplication.ui.theme.BazingaTextMuted

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

data class ComicItem(
    val title: String,
    val creator: String,
    val isDigital: Boolean = false
)

@Composable
fun BazingaApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { BazingaHeader() }
            item { HeroBanner() }
            item { BrowseFilters() }
            item { ComicSection(title = "NEW THIS WEEK", comics = sampleNewThisWeek()) }
            item { ComicSection(title = "DIGITAL EXCLUSIVE", comics = sampleDigitalExclusive()) }
            item { UnlimitedBanner() }
            item { ComicSection(title = "ALL COMICS", comics = sampleAllComics()) }
            item { BazingaFooter() }
        }
    }
}

@Composable
fun BazingaHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "BAZINGA",
                color = BazingaRed,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp,
                fontSize = 22.sp
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "Wishlist",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Cart",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun HeroBanner() {
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrowseFilters() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Browse by",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        FlowRow(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All Series", "Stormbreakers", "X-Men", "Avengers", "Digital Read", "Bazinga Unlimited")
                .forEach { label ->
                    FilterChip(
                        selected = label == "All Series",
                        onClick = {},
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    )
                }
        }
    }
}

@Composable
fun ComicSection(title: String, comics: List<ComicItem>) {
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
                ComicCard(comic = comic)
            }
        }
    }
}

@Composable
fun ComicCard(comic: ComicItem) {
    Column(modifier = Modifier.width(140.dp)) {
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BazingaSurface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BazingaSurfaceAlt, BazingaSurface)
                        )
                    )
            )
            if (comic.isDigital) {
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
            text = comic.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = comic.creator,
            fontSize = 11.sp,
            color = BazingaTextMuted
        )
    }
}

@Composable
fun UnlimitedBanner() {
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "Join Now")
            }
        }
    }
}

@Composable
fun BazingaFooter() {
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

fun sampleNewThisWeek() = listOf(
    ComicItem("Stormbreakers: Rise", "J. Lawson"),
    ComicItem("Nightwatch", "R. Chen"),
    ComicItem("Quantum Trail", "S. Rivera"),
    ComicItem("Guardians Unbound", "L. Okafor")
)

fun sampleDigitalExclusive() = listOf(
    ComicItem("Digital Nova", "A. Vega", isDigital = true),
    ComicItem("Echoes of Steel", "M. Hart", isDigital = true),
    ComicItem("Warp Horizon", "T. Brooks", isDigital = true)
)

fun sampleAllComics() = listOf(
    ComicItem("Shadowline", "N. Patel"),
    ComicItem("Iron Pulse", "K. Morrison"),
    ComicItem("Solar Edge", "D. White"),
    ComicItem("Crimson Oath", "E. Kim"),
    ComicItem("Atlas Protocol", "F. Gupta"),
    ComicItem("Valkyrie Run", "C. Flores")
)

@Preview(showBackground = true)
@Composable
fun BazingaPreview() {
    MyApplicationTheme {
        BazingaApp()
    }
}
