package com.grocart.first.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grocart.first.data.InternetItem
import com.grocart.first.data.Order
import com.grocart.first.data.SessionManager
import com.grocart.first.network.FirstApi
import com.grocart.first.network.LoginRequest
import com.grocart.first.network.UserResponse
import com.grocart.first.network.UserSignupRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> = _user.asStateFlow()

    private val _cartItems = MutableStateFlow<List<InternetItem>>(emptyList())
    val cartItems: StateFlow<List<InternetItem>> = _cartItems.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _isGuestSession = MutableStateFlow(false)
    val isGuestSession: StateFlow<Boolean> = _isGuestSession.asStateFlow()

    private val _logoutClicked = MutableStateFlow(false)
    val logoutClicked: StateFlow<Boolean> = _logoutClicked.asStateFlow()

    private val _showPaymentScreen = MutableStateFlow(false)
    val showPaymentScreen: StateFlow<Boolean> = _showPaymentScreen.asStateFlow()

    private val _paymentCountdown = MutableStateFlow(10)
    val paymentCountdown: StateFlow<Int> = _paymentCountdown.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _uiState = MutableStateFlow(GroUiState())
    val uiState: StateFlow<GroUiState> = _uiState.asStateFlow()

    var itemUiState: ItemUiState by mutableStateOf(ItemUiState.Loading)
        private set

    sealed interface ItemUiState {
        data class Success(val items: List<InternetItem>) : ItemUiState
        object Error : ItemUiState
        object Loading : ItemUiState
    }

    // Staggered init to stop the Davey lag
    init {
        checkExistingSession()
        viewModelScope.launch {
            delay(2500) // Allow UI to finish first frame
            launch { loadUserCart() }
            launch { getFirstItem() }
        }
    }

    private fun checkExistingSession() {
        val savedId = sessionManager.getUserId()
        val savedName = sessionManager.getUsername()
        if (savedId != -1L && savedName != null) {
            _user.value = UserResponse(id = savedId, username = savedName, email = "")
            _isGuestSession.value = false
        }
    }

    fun loadUserCart() {
        val userId = sessionManager.getUserId()
        if (userId == -1L) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = FirstApi.retrofitService.getUserCart(userId)
                if (response.isSuccessful) {
                    val items = response.body() ?: emptyList()
                    _cartItems.update { items } // Safe update
                    Log.d("GROCART_DEBUG", "Cart Synced from MySQL: ${items.size}")
                }
            } catch (e: Exception) {
                Log.e("GROCART_DEBUG", "Cart sync failed: ${e.message}")
            }
        }
    }

    fun addToCart(item: InternetItem) {
        val userId = _user.value?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = FirstApi.retrofitService.addCartItem(userId, item)
                if (response.isSuccessful) {
                    _cartItems.update { it + item }
                }
            } catch (e: Exception) {
                Log.e("GROCART_DEBUG", "Add Cart error: ${e.message}")
            }
        }
    }

    fun getFirstItem() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { itemUiState = ItemUiState.Loading }
            try {
                val items = FirstApi.retrofitService.getItems()
                withContext(Dispatchers.Main) { itemUiState = ItemUiState.Success(items) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { itemUiState = ItemUiState.Error }
            }
        }
    }

    // Helper functions
    fun setLogoutClicked(v: Boolean) { _logoutClicked.value = v }
    fun endGuestSession() { _isGuestSession.value = false }
    fun startGuestSession() { _isGuestSession.value = true }
    fun proceedToPay() { _showPaymentScreen.value = true }
    fun cancelPayment() { _showPaymentScreen.value = false }
    fun setPaymentCountdown(v: Int) { _paymentCountdown.value = v }
    fun updateSelectedCategory(cat: Int) { _uiState.update { it.copy(selectedCategory = cat) } }
    fun updateClickText(t: String) { _uiState.update { it.copy(clickStatus = t) } }

    fun login(u: String, p: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = FirstApi.retrofitService.loginUser(LoginRequest(u, p))
                if (res.isSuccessful) {
                    res.body()?.let {
                        _user.value = it
                        sessionManager.saveUserSession(it.id, it.username)
                        loadUserCart()
                    }
                }
            } catch (e: Exception) { Log.e("LOGIN", e.message ?: "") }
            finally { _loading.value = false }
        }
    }

    fun logout() {
        sessionManager.logout()
        _user.value = null
        _cartItems.value = emptyList()
        _isGuestSession.value = false
        _logoutClicked.value = false
    }

    fun clearData() { logout() }

    fun register(u: String, e: String, p: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = FirstApi.retrofitService.registerUser(UserSignupRequest(u, e, p))
                if (res.isSuccessful) login(u, p)
            } catch (err: Exception) { Log.e("REG", err.message ?: "") }
            finally { _loading.value = false }
        }
    }

    fun completePayment() {
        if (_cartItems.value.isNotEmpty()) {
            val order = Order(items = _cartItems.value, timestamp = System.currentTimeMillis())
            _orders.update { it + order }
            _cartItems.value = emptyList()
        }
        _showPaymentScreen.value = false
    }

    fun decreaseItemCount(item: InternetItem) {
        _cartItems.update { current ->
            val list = current.toMutableList()
            val idx = list.indexOfFirst { it.itemName == item.itemName }
            if (idx != -1) list.removeAt(idx)
            list
        }
    }
}