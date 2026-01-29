package com.grocart.first.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.grocart.first.data.InternetItem
import com.grocart.first.R

/**
 * 1. InternetItemScreen: Main entry point for this file.
 * Handles different UI states from the ViewModel.
 */
@Composable
fun InternetItemScreen(
    groViewModel: GroViewModel,
    itemUiState: GroViewModel.ItemUiState
){
    when(itemUiState){
        is GroViewModel.ItemUiState.Loading -> LoadingScreen()
        is GroViewModel.ItemUiState.Success -> {
            // ✅ MySQL data successfully fetched
            ItemScreen(groViewModel = groViewModel, items = itemUiState.items)
        }
        else -> ErrorScreen(gromViewModel = groViewModel)
    }
}

/**
 * 2. ItemScreen: Displays the grid of items after filtering by category.
 */
@Composable
fun ItemScreen(
    groViewModel: GroViewModel,
    items : List<InternetItem>
) {
    val groUiState by groViewModel.uiState.collectAsState()
    val selectedCategory = stringResource(groUiState.selectedCategory)

    // ✅ DEBUG: Verify if list is coming from DB
    Log.d("GROCART_DEBUG", "Total items from server: ${items.size}")

    // Filter matching "Fresh Fruits", "Beverages", etc.
    val database = remember(items, selectedCategory) {
        // 🛡️ Safety check: Agar items empty hain toh loop avoid karein
        if (items.isEmpty()) {
            emptyList()
        } else {
            items.filter { it.itemCategory.trim().equals(selectedCategory.trim(), ignoreCase = true) }
        }
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
            ) {
                Text(
                    text = "$selectedCategory (${database.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }

        // Display items mapping to DB columns
        items(database) { currentItem ->
            ItemCard(
                itemName = currentItem.itemName,       // Matches stringResourceId
                imageUrl = currentItem.imageUrl,       // Matches imageResourceId
                quantityLabel = currentItem.itemQuantity,
                itemPrice = currentItem.itemPrice,
                itemCategory = currentItem.itemCategory,
                groViewModel = groViewModel
            )
        }
    }
}

/**
 * 3. ItemCard: Individual card for each product.
 */
@Composable
fun ItemCard(
    itemName: String,
    imageUrl: String,
    quantityLabel: String,
    itemPrice: Int,
    itemCategory: String,
    groViewModel: GroViewModel
) {
    val context = LocalContext.current

    Column(modifier = Modifier.width(150.dp).padding(5.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(249,219,242,50))) {
            Box {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = itemName,
                    modifier = Modifier.fillMaxWidth().height(110.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(244, 67, 54, 255))) {
                        Text(text = "25% off", fontSize = 15.sp, modifier = Modifier.padding(horizontal = 5.dp))
                    }
                }
            }
        }

        Text(text = itemName, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 5.dp), maxLines = 1)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = "Rs. $itemPrice", fontSize = 12.sp, color = Color.Gray, textDecoration = TextDecoration.LineThrough)
                Text(text = "Rs. ${itemPrice * 75 / 100}", fontSize = 20.sp, color = Color(255, 116, 105, 255))
            }
            // Display string quantity (e.g., "1 kg")
            Text(text = quantityLabel, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
        }

        Card(
            modifier = Modifier.fillMaxWidth().clickable {
                // ✅ Add to Cart logic with proper DB mapping
                groViewModel.addToCart(
                    InternetItem(
                        itemName = itemName,
                        imageUrl = imageUrl,
                        itemQuantity = quantityLabel,
                        itemPrice = itemPrice,
                        itemCategory = itemCategory
                    )
                )
                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show()
            },
            colors = CardDefaults.cardColors(containerColor = Color(57, 68, 179, 135))
        ) {
            Text(text = "Add to Cart", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        }
    }
}

/**
 * 4. Helper Screens
 */
@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(gromViewModel: GroViewModel) {
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Image(painter = painterResource(id = R.drawable.error), contentDescription = "Error")
        Button(onClick = { gromViewModel.getFirstItem() }) { Text("Retry") }
    }
}