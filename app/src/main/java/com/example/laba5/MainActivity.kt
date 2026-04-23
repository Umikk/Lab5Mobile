package com.example.laba5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.laba5.data.datastore.SearchPreferences
import com.example.laba5.data.db.AppDatabase
import com.example.laba5.data.entity.AirportEntity
import com.example.laba5.data.repository.AirportRepository
import com.example.laba5.ui.SearchViewModel
import com.example.laba5.ui.theme.Laba5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Laba5Theme {

                val db = AppDatabase.create(this)
                val prefs = SearchPreferences(this)

                val repository = AirportRepository(
                    db.airportDao(),
                    db.favoriteDao()
                )

                val viewModel = remember {
                    SearchViewModel(repository, prefs)
                }

                LaunchedEffect(Unit) {
                    viewModel.loadSavedQuery()
                }

                SearchScreen(viewModel)
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: SearchViewModel) {

    var selectedAirport by remember { mutableStateOf<AirportEntity?>(null) }

    val query by viewModel.query.collectAsState()
    val airports by viewModel.airports.collectAsState(initial = emptyList())
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())

    if (selectedAirport == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            TextField(
                value = query,
                onValueChange = { viewModel.setQuery(it) },
                label = { Text("Search airport") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                // ⭐ ИЗБРАННЫЕ
                if (query.isEmpty()) {
                    items(
                        items = favorites,
                        key = { it.departureCode + it.destinationCode }
                    ) { fav ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("DEPART")
                                    Text("${fav.departureCode}  ${fav.fromName}")

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text("ARRIVE")
                                    Text("${fav.destinationCode}  ${fav.toName}")
                                }

                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Удалить",
                                    modifier = Modifier.clickable {
                                        viewModel.toggleFavorite(
                                            fav.departureCode,
                                            fav.destinationCode,
                                            true
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // 🔍 АЭРОПОРТЫ
                items(
                    items = airports,
                    key = { it.iataCode }
                ) { airport ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedAirport = airport }
                            .padding(8.dp)
                    ) {

                        Text(
                            text = airport.iataCode,
                            modifier = Modifier.width(60.dp)
                        )

                        Text(text = airport.name)
                    }
                }

                if (airports.isEmpty() && query.isNotEmpty()) {
                    item {
                        Text("Ничего не найдено")
                    }
                }
            }
        }

    } else {

        FlightsScreen(
            airport = selectedAirport!!,
            viewModel = viewModel,
            onBack = { selectedAirport = null }
        )
    }
}

@Composable
fun FlightsScreen(
    airport: AirportEntity,
    viewModel: SearchViewModel,
    onBack: () -> Unit
) {

    val flights by viewModel.getFlights(airport.iataCode)
        .collectAsState(initial = emptyList())

    val favorites by viewModel.favorites.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(onClick = onBack) {
            Text("Назад")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Flights from: ${airport.name}")

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(
                items = flights,
                key = { it.iataCode }
            ) { flight ->

                val isFavorite = favorites.any {
                    it.departureCode == airport.iataCode &&
                            it.destinationCode == flight.iataCode
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(Modifier.weight(1f)) {

                            Text("DEPART")
                            Text("${airport.iataCode}  ${airport.name}")

                            Spacer(modifier = Modifier.height(4.dp))

                            Text("ARRIVE")
                            Text("${flight.iataCode}  ${flight.name}")
                        }

                        Text(
                            text = if (isFavorite) "★" else "☆",
                            modifier = Modifier
                                .clickable {
                                    viewModel.toggleFavorite(
                                        airport.iataCode,
                                        flight.iataCode,
                                        isFavorite
                                    )
                                }
                                .padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}