package com.grocart.first.ui

// Import necessary Compose and Android libraries
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.grocart.first.data.InternetItem
import com.grocart.first.R




// Composable function to display a grid of items for the selected category
@Composable
fun ItemScreen(
    groViewModel: GroViewModel,
    items : List<InternetItem>
) {
    val groUiState by groViewModel.uiState.collectAsState() // Collect UI state from ViewModel
    val selectedCategory = stringResource(groUiState.selectedCategory) // Get selected category from UI state
    val database = remember(items,selectedCategory) {items.filter { it.itemCategory.equals(
        selectedCategory, ignoreCase = true) }  }

    // Display items in a grid with adaptive column width
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp), // Each column is at least 150dp wide
        contentPadding = PaddingValues(10.dp), // Padding around the grid
        verticalArrangement = Arrangement.spacedBy(5.dp), // Spacing between rows
        horizontalArrangement = Arrangement.spacedBy(5.dp) // Spacing between columns
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ){
            Column {

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent

                    ), modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Text(text = "${stringResource(groUiState.selectedCategory)}(${database.size})", fontSize = 20.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp))
                }
            }
        }
        // Display each item as a card
        items(database ) {
            ItemCard(
                stringResourceId = it.itemName,
                imageResourceId = it.imageUrl,
                itemQuantity = it.itemQuantity,
                itemPrice = it.itemPrice,
                groViewModel = groViewModel
            )
        }
    }
}



@Composable
fun InternetItemScreen(
    groViewModel: GroViewModel,
    itemUiState: GroViewModel.ItemUiState
){
    when(itemUiState){
        is GroViewModel.ItemUiState.Loading -> {
            LoadingScreen()
        }
        is GroViewModel.ItemUiState.Success -> {
            ItemScreen(groViewModel = groViewModel, items = itemUiState.items)
        }
        else -> {
            ErrorScreen(gromViewModel = groViewModel)
        }
    }

}
// Composable function to display individual item card with image, price, quantity, and add-to-cart button
@Composable
fun ItemCard(
    stringResourceId: String, // Resource ID for item name
    imageResourceId: String,  // Resource ID for item image
    itemQuantity: String,  // Quantity string (e.g., "1 kg")
    itemPrice: Int,         // Original price of item
    groViewModel: GroViewModel
) {
    val context = LocalContext.current // Get current context for Toast

    // Outer column for arranging all item content
    Column(modifier = Modifier.width(150.dp).padding(5.dp)) {
        // Card for item image and discount tag
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(249,219,242,50)),

        ) {
            Box {
                // Item image
                AsyncImage(
                    model = imageResourceId,
                    contentDescription = stringResourceId,
                    modifier = Modifier.fillMaxWidth()
                        .height((110.dp))
                )

                // Discount label positioned at top-end
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.End
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(244, 67, 54, 255) // Red background
                        )
                    ) {
                        Text(
                            text = "25% off", // Static discount tex
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Item name
        Text(
            text =stringResourceId, // Display item name
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 5.dp),
            maxLines = 1,
            textAlign = TextAlign.Left
        )

        // Row for price and quantity info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Original price with strike-through
                Text(
                    text = "Rs . $itemPrice",
                    fontSize = 12.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = Color(109, 109, 109, 255),
                    textDecoration = TextDecoration.LineThrough
                )
                // Discounted price (75% of original)
                Text(
                    text = "Rs. ${itemPrice * 75 / 100}",
                    fontSize = 20.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = Color(255, 116, 105, 255)
                )
            }

            // Quantity label
            Text(
                text = itemQuantity,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                color = Color(114, 114, 114, 255)
            )
        }

        // Add to cart button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .clickable {
                    groViewModel.addToDatabase(
                        InternetItem(
                            itemName = stringResourceId,
                            imageUrl = imageResourceId,
                            itemQuantity = itemQuantity,
                            itemPrice = itemPrice,
                            itemCategory = ""
                        )
                    )
                    // Show toast when item added to cart
                    Toast.makeText(context, "Added to Cart",
                        Toast.LENGTH_SHORT)
                        .show()
                },
            colors = CardDefaults.cardColors(
                containerColor = Color(57, 68, 179, 135) // Green background
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button text
                Text(
                    text = "Add to Cart",
                    fontSize = 21.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 5.dp).height(50.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingScreen(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
        Image(painter = painterResource(id = R.drawable.loading), contentDescription = "Loading")
    }
}

@Composable
fun ErrorScreen(gromViewModel: GroViewModel){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ){
        Image(painter = painterResource(id = R.drawable.error), contentDescription = "Error")
        Text(text = "Oops! Internet unavailable. Please check your connection.", fontSize = 28.sp, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(20.dp), textAlign = TextAlign.Center)
        Button(onClick = {
            gromViewModel.getFirstItem()
        }) {
            Text(text = "Retry")
        }

    }
}