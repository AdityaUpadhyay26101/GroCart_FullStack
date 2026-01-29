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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    // Collect states from updated GroViewModel
    val cartItems by groViewModel.cartItems.collectAsState()
    val showPaymentScreen by groViewModel.showPaymentScreen.collectAsState()

    val cartItemsWithQuantity = cartItems
        .groupBy { it.itemName }
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

                items(cartItemsWithQuantity) { itemWithQuantity ->
                    CartCard(
                        item = itemWithQuantity.internetItem,
                        quantity = itemWithQuantity.quantity,
                        // Fix: Using local cart methods instead of Database
                        onAddItem = { groViewModel.addToCart(itemWithQuantity.internetItem) },
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

                val totalPrice = cartItems.sumOf { it.itemPrice * 75 / 100 }
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
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                            ) {
                                Text("Proceed to Pay")
                            }
                        }
                    }
                }
            }
        } else {
            EmptyCartUI(onHomeButtonClicked)
        }

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

@Composable
fun EmptyCartUI(onHomeButtonClicked: () -> Unit) {
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
            text = "Your Cart is Empty",
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(20.dp)
        )
        FilledTonalButton(onClick = onHomeButtonClicked) {
            Text(text = "Browse Products")
        }
    }
}

@Composable
fun CartCard(
    item: InternetItem,
    quantity: Int,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit
) {
    val lineItemTotalPrice = (item.itemPrice * 75 / 100) * quantity

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.itemName,
            modifier = Modifier.size(80.dp).padding(end = 8.dp)
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
            Text(text = item.itemName, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = "Rs. ${item.itemPrice * 75 / 100} / each", fontSize = 14.sp, fontWeight = FontWeight.Light)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            QuantitySelector(quantity = quantity, onAddItem = onAddItem, onRemoveItem = onRemoveItem)
            Text(
                text = "Rs. $lineItemTotalPrice",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        OutlinedIconButton(
            onClick = onRemoveItem,
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Remove", tint = MaterialTheme.colorScheme.primary)
        }
        Text(text = "$quantity", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
        OutlinedIconButton(
            onClick = onAddItem,
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

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
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)).clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Card(modifier = Modifier.fillMaxWidth(0.8f).padding(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (!isPaymentFinished) {
                    AnimatedContent(targetState = countdown, label = "") { targetCount ->
                        Text(text = "$targetCount", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF008069), modifier = Modifier.size(48.dp))
                }
                Text(text = paymentStatus, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                if (!isPaymentFinished) {
                    TextButton(onClick = onPaymentCancelled) { Text("Cancel") }
                }
            }
        }
    }
}

@Composable
fun BillRow(itemName: String, itemPrice: Int, fontWeight: FontWeight) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(text = itemName, fontWeight = fontWeight)
        Text(text = "Rs. $itemPrice", fontWeight = fontWeight)
    }
}