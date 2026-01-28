package com.grocart.first.ui

// Importing required Jetpack Compose and Navigation libraries
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
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
import com.google.firebase.auth.FirebaseAuth
import com.grocart.first.R
import kotlinx.coroutines.flow.MutableStateFlow

/** Enum class to define available screens and their titles */
enum class GroAppScreen(val title: String) {
    Start("GroCart"),
    Item("Choose Items"),
    Cart("Your Cart"),
    Orders("My Orders")
}

// Global variable to track if back navigation is possible
var canNavigateBack = false
val auth = FirebaseAuth.getInstance()

/**
 * Entry-point composable function for the app UI
 * Implements MVVM architecture with Navigation and Scaffold layout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstApp(
    groViewModel: GroViewModel = viewModel(), // ViewModel for managing UI data and state
    navController: NavHostController = rememberNavController(),// Controller for navigating between screens

) {


    val user by groViewModel.user.collectAsState()
    val logoutClicked by groViewModel.logoutClicked.collectAsState()
    auth.currentUser?.let{groViewModel.setUser(it)}
    val isVisible by groViewModel.isVisible.collectAsState()
    // Observe current back stack entry to determine current screen
    val backStackEntry by navController.currentBackStackEntryAsState()

    // Get current screen from route name or default to Start screen
    val currentScreen = GroAppScreen.valueOf(
        backStackEntry?.destination?.route ?: GroAppScreen.Start.name
    )

    // Determine whether back navigation is allowed
    canNavigateBack = navController.previousBackStackEntry != null
    val cartItems by groViewModel.cartItems.collectAsState()
    val isGuest by groViewModel.isGuestSession.collectAsState()


    if(user == null && !isGuest){
        LoginUi(groViewModel = groViewModel)
    }


    else{
        // Scaffold layout for top bar, bottom bar, and content
        Scaffold(
            topBar = {
                TopAppBar(

                    title = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Left side: App title + cart count
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = currentScreen.title,
                                    fontSize = 26.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                                )
                                if (currentScreen == GroAppScreen.Cart) {
                                    Text(
                                        text = "(${cartItems.size})",
                                        fontSize = 26.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                }
                            }

                            // Right side: Logout
                            Row(
                                modifier = Modifier.clickable{
                                    groViewModel.setLogoutClicked(true)
                                }

                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.logout),
                                    contentDescription = "Logout",
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Logout",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                        }
                    },
                    // Show back button only if there is a previous screen in stack
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(onClick = {
                                navController.navigateUp() // Navigate to previous screen
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back Button"
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                FirstAppBar(
                    navController = navController,
                    currentScreen = currentScreen,
                    cartItems = cartItems

                ) // Bottom navigation bar
            }
        ) {
            // Define navigation host with two composable destinations
            NavHost(
                navController = navController,
                startDestination = GroAppScreen.Start.name,
                modifier = Modifier.padding(it)
            ) {
                // Start Screen: Displays categories
                composable(route = GroAppScreen.Start.name) {
                    StartScreen(
                        groViewModel = groViewModel,
                        onCategoryClicked = { category ->
                            groViewModel.updateSelectedCategory(category) // Update ViewModel state
                            navController.navigate(GroAppScreen.Item.name) // Navigate to Item screen
                        }
                    )
                }

                // Item Screen: Displays selected items from category
                composable(route = GroAppScreen.Item.name) {
                    InternetItemScreen(
                        groViewModel = groViewModel,
                        itemUiState = groViewModel.itemUiState
                    )
                }
                composable ( route = GroAppScreen.Cart.name ){
                    CartScreen(groViewModel = groViewModel,
                        onHomeButtonClicked = {
                            navController.navigate(GroAppScreen.Start.name){
                                popUpTo(0)
                            }
                        }

                    )
                }
                composable(route = GroAppScreen.Orders.name){
                    MyOrdersScreen(groViewModel = groViewModel)
                }

            }
            if (logoutClicked){
                AlertCheck(onYesButtonPressed = {
                    groViewModel.setLogoutClicked(false)
                    auth.signOut()
                    groViewModel.clearData()

                }, onNoButtonPressed = {
                    groViewModel.setLogoutClicked(false)
                })
            }
        }
    }


}

/**
 * Bottom navigation bar with Home and Cart options
 */
@Composable
fun FirstAppBar(navController: NavHostController,
                currentScreen: GroAppScreen,
                cartItems: List<InternetItem>
) {
    val groViewModel: GroViewModel =viewModel ()
    val user by groViewModel.user.collectAsState()
    val isGuest by groViewModel.isGuestSession.collectAsState()

    var showLoginPrompt by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 70.dp, vertical = 10.dp)
    ) {
        // Home Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable {
                navController.navigate(GroAppScreen.Start.name) {
                    popUpTo(0) // Clears navigation stack to start
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home Icon")
            Text(text = "Home", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable{
                if(isGuest){
                    showLoginPrompt = true
                }else{
                    navController.navigate(GroAppScreen.Orders.name){
                        popUpTo(0)
                    }
                }
            }
        ){
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.List,
                contentDescription = "My Orders Icon"
            )
            Text(text = "My Orders", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        // Cart Button (Currently navigates to Start as Cart screen isn't implemented)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable {
                if(currentScreen != GroAppScreen.Cart){
                    navController.navigate(GroAppScreen.Cart.name)
                }

            }
        ) {
            Box {
                Icon(imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart Icon"
                )
                if (cartItems.isNotEmpty())
                    Card (
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red
                        ),
                        modifier = Modifier.align(alignment = Alignment.TopEnd)

                    ){
                        Text(text = cartItems.size.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 1.dp))

                    }
                if(showLoginPrompt){
                    AlertDialog(
                        onDismissRequest = {
                            showLoginPrompt = false
                        },
                        title = {
                            Text(text = "Login Required")
                        },
                        text = {Text(text = "You must be logged in to view your cart")},
                        confirmButton = {
                            TextButton(onClick = {
                                showLoginPrompt = false
                                groViewModel.endGuestSession()
                                navController.navigate(GroAppScreen.Start.name){
                                    popUpTo(0) { inclusive = true }
                                }
                            }){
                                Text(text = "Login")
                            }},
                        dismissButton = {
                            TextButton(onClick = {
                                showLoginPrompt = false
                        }){
                            Text(text = "Cancel")
                        }}
                    )
                }
            }
            Text(text = "Cart", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AlertCheck(
    onYesButtonPressed:() -> Unit,
    onNoButtonPressed:() -> Unit
){
    AlertDialog(
        title = {
            Text(text = "Logout?", fontWeight = FontWeight.ExtraBold)
        }, containerColor = Color.White,
        text = {
            Text(text = "Are you sure you want to logout?")
        },
        confirmButton = {
            TextButton(onClick = {onYesButtonPressed()}) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = {onNoButtonPressed()}) {
                Text(text = "No")
            }
        },
        onDismissRequest = {
            onNoButtonPressed()
        }

    )
}