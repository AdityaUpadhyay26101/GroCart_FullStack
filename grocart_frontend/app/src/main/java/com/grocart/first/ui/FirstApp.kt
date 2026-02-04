package com.grocart.first.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.grocart.first.data.InternetItem
import com.grocart.first.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


/** Enum class to define available screens and their titles */
enum class GroAppScreen(val title: String) {
    Start("GroCart"),
    Item("Choose Items"),
    Cart("Your Cart"),
    Orders("My Orders")
}

// Global variable for back navigation
var canNavigateBack = false



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstApp(
    groViewModel: GroViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
) {
    // Collect states from MySQL-based ViewModel
    val user by groViewModel.user.collectAsState()
    val logoutClicked by groViewModel.logoutClicked.collectAsState()
    val cartItems by groViewModel.cartItems.collectAsState()
    val isGuest by groViewModel.isGuestSession.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = GroAppScreen.valueOf(
        backStackEntry?.destination?.route ?: GroAppScreen.Start.name
    )

    canNavigateBack = navController.previousBackStackEntry != null

    // Login logic updated to check MySQL user
    if (user == null && !isGuest) {
        LoginUi(groViewModel = groViewModel)
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentScreen.title,
                                    fontSize = 26.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                                )
                                if (currentScreen == GroAppScreen.Cart) {
                                    Text(
                                        text = " (${cartItems.size})",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Logout trigger
                            Row(modifier = Modifier.clickable { groViewModel.setLogoutClicked(true) }) {
                                Icon(painter = painterResource(R.drawable.logout), contentDescription = "Logout", modifier = Modifier.size(24.dp))
                                Text(text = "Logout", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 2.dp))
                            }
                        }
                    },
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                FirstAppBar(navController = navController, currentScreen = currentScreen, cartItems = cartItems, groViewModel = groViewModel)
            }
        ) { padding ->
            NavHost(navController = navController, startDestination = GroAppScreen.Start.name, modifier = Modifier.padding(padding)) {
                composable(route = GroAppScreen.Start.name) {
                    StartScreen(groViewModel = groViewModel, onCategoryClicked = { cat ->
                        groViewModel.updateSelectedCategory(cat)
                        navController.navigate(GroAppScreen.Item.name)
                    })
                }
                composable(route = GroAppScreen.Item.name) {
                    InternetItemScreen(groViewModel = groViewModel, itemUiState = groViewModel.itemUiState)
                }
                composable(route = GroAppScreen.Cart.name) {
                    CartScreen(groViewModel = groViewModel, onHomeButtonClicked = {
                        navController.navigate(GroAppScreen.Start.name) { popUpTo(0) }
                    })
                }
                composable(route = GroAppScreen.Orders.name) {
                    MyOrdersScreen(groViewModel = groViewModel)
                }
            }

            if (logoutClicked) {
                AlertCheck(
                    onYesButtonPressed = {
                        groViewModel.setLogoutClicked(false)
                        // ✅ MySQL LOGOUT: Just clear local user data
                        groViewModel.clearData()
                    },
                    onNoButtonPressed = { groViewModel.setLogoutClicked(false) }
                )
            }
        }
    }
}

@Composable
fun FirstAppBar(
    navController: NavHostController,
    currentScreen: GroAppScreen,
    cartItems: List<InternetItem>,
    groViewModel: GroViewModel
) {
    val isGuest by groViewModel.isGuestSession.collectAsState()
    var showLoginPrompt by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 10.dp)
    ) {
        // Home
        AppNavItem(Icons.Outlined.Home, "Home") {
            navController.navigate(GroAppScreen.Start.name) { popUpTo(0) }
        }

        // Orders
        AppNavItem(Icons.AutoMirrored.Outlined.List, "Orders") {
            if (isGuest) showLoginPrompt = true
            else navController.navigate(GroAppScreen.Orders.name) { popUpTo(0) }
        }

        // Cart
        Box(modifier = Modifier.clickable { navController.navigate(GroAppScreen.Cart.name) }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.ShoppingCart, "Cart")
                Text("Cart", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            if (cartItems.isNotEmpty()) {
                Badge(containerColor = Color.Red, modifier = Modifier.align(Alignment.TopEnd)) {
                    Text(cartItems.size.toString(), color = Color.White)
                }
            }
        }
    }

    if (showLoginPrompt) {
        AlertDialog(
            onDismissRequest = { showLoginPrompt = false },
            title = { Text("Login Required") },
            text = { Text("Please login to see your orders.") },
            confirmButton = { TextButton(onClick = { groViewModel.endGuestSession(); showLoginPrompt = false }) { Text("Login") } },
            dismissButton = { TextButton(onClick = { showLoginPrompt = false }) { Text("Cancel") } }
        )
    }
}
// Navigation
@Composable
fun AppNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Icon(icon, label)
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
// ✅ ALERT DIALOG REMOVED
@Composable
fun AlertCheck(onYesButtonPressed: () -> Unit, onNoButtonPressed: () -> Unit) {
    AlertDialog(
        onDismissRequest = onNoButtonPressed,
        title = { Text("Logout?", fontWeight = FontWeight.ExtraBold) },
        text = { Text("Are you sure you want to logout?") },
        confirmButton = { TextButton(onClick = onYesButtonPressed) { Text("Yes") } },
        dismissButton = { TextButton(onClick = onNoButtonPressed) { Text("No") } }
    )
}