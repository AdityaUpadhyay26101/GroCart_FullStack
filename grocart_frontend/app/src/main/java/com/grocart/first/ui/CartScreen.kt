package com.grocart.first.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.grocart.first.R
import com.grocart.first.data.InternetItem
import com.grocart.first.data.InternetItemWithQuantity
import kotlinx.coroutines.delay

@Composable
fun CartScreen(
    groViewModel: GroViewModel,
    onHomeButtonClicked: () -> Unit
) {
    val cartItems by groViewModel.cartItems.collectAsState()
    val showPaymentScreen by groViewModel.showPaymentScreen.collectAsState()

    // This logic correctly groups items and counts their quantity.
    val cartItemsWithQuantity = cartItems
        .groupBy { it.itemName } // Or a more unique ID if available
        .map { (_, items) ->
            InternetItemWithQuantity(items.first(), items.size)
        }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cartItems.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Review Items",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }

                // Use the new grouped list here
                items(cartItemsWithQuantity) { itemWithQuantity ->
                    CartCard(
                        item = itemWithQuantity.internetItem,
                        quantity = itemWithQuantity.quantity,
                        onAddItem = { groViewModel.addToDatabase(itemWithQuantity.internetItem) },
                        onRemoveItem = { groViewModel.decreaseItemCount(itemWithQuantity.internetItem) }
                    )
                }

                item {
                    Text(
                        text = "Bill Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
                // The grand total of all items in the cart
                val totalPrice = cartItems.sumOf { it.itemPrice *75 / 100 }
                val handlingCharge = totalPrice * 1 / 100
                val deliveryFee = 30
                val grandTotal = totalPrice + handlingCharge + deliveryFee
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            BillRow("Item Total", totalPrice, FontWeight.Normal)
                            BillRow("Handling Charge", handlingCharge, FontWeight.Light)
                            BillRow("Delivery Fee", deliveryFee, FontWeight.Light)
                            HorizontalDivider(
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.LightGray
                            )
                            BillRow(" To Pay", grandTotal, FontWeight.ExtraBold)
                            FilledTonalButton(
                                onClick = { groViewModel.proceedToPay() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            ) {
                                Text("Proceed to Pay")
                            }
                        }
                    }
                }
            }
        } else {
            // Empty Cart UI
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.emptycart),
                    contentDescription = "Empty Cart",
                    modifier = Modifier.size(250.dp)
                )
                Text(
                    text = "You Cart is Empty",
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(20.dp)
                )
                FilledTonalButton(onClick = {
                    onHomeButtonClicked()
                }) {
                    Text(text = "Browse Products")
                }
            }
        }

        // Fake Payment Screen
        if (showPaymentScreen) {
            FakePaymentScreen(
                groViewModel = groViewModel,
                onPaymentComplete = {
                    groViewModel.completePayment()
                    onHomeButtonClicked()
                },
                onPaymentCancelled = { groViewModel.cancelPayment() }
            )
        }
    }
}


// ✅ UPDATED: CartCard now shows the total price for the line item.
@Composable
fun CartCard(
    item: InternetItem,
    quantity: Int,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit
) {
    // Calculate the total price for this specific item line
    val lineItemTotalPrice = item.itemPrice * quantity

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.itemName,
            modifier = Modifier
                .size(80.dp)
                .padding(end = 8.dp) // Added padding for spacing
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp), // Adjusted padding
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.itemName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            // Shows the price for a single item for clarity
            Text(
                text = "Rs. ${item.itemPrice * 75 /100} / each",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
            )
        }

        // This new column will stack the quantity selector and the subtotal
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            QuantitySelector(
                quantity = quantity,
                onAddItem = onAddItem,
                onRemoveItem = onRemoveItem
            )
            // ADDED: Display the total price for this line item
            Text(
                text = "Rs. ${lineItemTotalPrice * 75 / 100} ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ✅ UPDATED: To make the '+' button visually consistent.
@Composable
fun QuantitySelector(
    quantity: Int,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Increased spacing
        modifier = modifier
    ) {
        // --- Minus Button ---
        OutlinedIconButton(
            onClick = onRemoveItem,
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Remove one item", tint = MaterialTheme.colorScheme.primary)
        }

        // --- Quantity Text ---
        Text(
            text = "$quantity",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.Center
        )

        // --- Plus Button ---
        // Using IconButton with a background for a filled effect
        OutlinedIconButton(
            onClick = onAddItem,
            modifier = Modifier
                .size(32.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)

        ) {
            Icon(Icons.Default.Add, contentDescription = "Add one item", tint = MaterialTheme.colorScheme.primary)
        }
    }
}


// --- NO CHANGES TO THE COMPOSABLES BELOW ---

@Composable
fun FakePaymentScreen(
    groViewModel: GroViewModel,
    onPaymentComplete: () -> Unit,
    onPaymentCancelled: () -> Unit
) {
    val countdown by groViewModel.paymentCountdown.collectAsState()
    var paymentStatus by remember { mutableStateOf("Processing...") }
    var isPaymentFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        for (i in 10 downTo 1) {
            groViewModel.setPaymentCountdown(i)
            delay(1000)
        }
        paymentStatus = "Payment Successful!"
        isPaymentFinished = true
        delay(1500)
        onPaymentComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!isPaymentFinished) {
                    AnimatedContent(
                        targetState = countdown,
                        transitionSpec = {
                            slideInVertically { height -> height } togetherWith
                                    slideOutVertically { height -> -height }
                        }, label = "Countdown Animation"
                    ) { targetCount ->
                        Text(
                            text = "$targetCount",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF008069),
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(
                    text = paymentStatus,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                if (!isPaymentFinished) {
                    TextButton(
                        onClick = onPaymentCancelled,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun BillRow(
    itemName: String,
    itemPrice: Int,
    fontWeight: FontWeight
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = itemName, fontWeight = fontWeight)
        Text(text = "Rs. $itemPrice ", fontWeight = fontWeight)
    }
}
