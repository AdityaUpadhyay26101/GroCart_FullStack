package com.grocart.first.ui


import android.R.attr.name
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.grocart.first.data.InternetItem
import com.grocart.first.data.Order // ✅ 1. IMPORT THE ORDER CLASS
import com.grocart.first.network.FirstApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database




// ViewModel class to hold and manage UI-related data for the StartScreen
class GroViewModel: ViewModel() {

    // Private mutable state flow to store the internal UI state
    private val _uiState = MutableStateFlow(GroUiState())

    // Public read-only version of the state flow exposed to the UI
    val uiState: StateFlow<GroUiState> = _uiState.asStateFlow()

    private val _paymentCountdown = MutableStateFlow(10)
    val paymentCountdown: StateFlow<Int> = _paymentCountdown.asStateFlow()
    private val _isGuestSession = MutableStateFlow(false)
    val isGuestSession: StateFlow<Boolean> = _isGuestSession.asStateFlow()



    private val _isVisible = MutableStateFlow(true)
    val isVisible = _isVisible
    var itemUiState : ItemUiState by mutableStateOf(ItemUiState.Loading)
        private set

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> get() = _user

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> get() = _phoneNumber

    private val _showPaymentScreen = MutableStateFlow(false)
    val showPaymentScreen: StateFlow<Boolean> get() = _showPaymentScreen


    private val _cartItems = MutableStateFlow<List<InternetItem>>(emptyList())
    val cartItems: StateFlow<List<InternetItem>> get() = _cartItems.asStateFlow()

    // ✅ 2. CORRECTLY DEFINE THE ORDERS STATE
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> get() = _orders.asStateFlow()

    private val  _otp = MutableStateFlow("")
    val otp: MutableStateFlow<String> get() = _otp

    private val _verificationId = MutableStateFlow("")
    val verificationId: MutableStateFlow<String> get() = _verificationId
    private  val _ticks = MutableStateFlow(60L)
    val ticks: MutableStateFlow<Long> get() = _ticks

    private val _loading = MutableStateFlow(false)
    val loading : MutableStateFlow<Boolean> get() = _loading

    private val _logoutClicked = MutableStateFlow(false)
    val logoutClicked: MutableStateFlow<Boolean> get() = _logoutClicked

    private lateinit var  timerJob: Job

    // ✅ 3. MAKE DATABASE REFERENCES LAZY to prevent crashes
    private val auth get() = FirebaseAuth.getInstance()
    private val database = Firebase.database
    private val cartRef get() = database.getReference("users/${auth.currentUser?.uid}/cart")
    private val ordersRef get() = database.getReference("users/${auth.currentUser?.uid}/orders")

    private lateinit var internetJob: Job
    private var screenJob: Job

    sealed interface ItemUiState {
        data class Success(val items: List<InternetItem>) : ItemUiState
        data object Error : ItemUiState
        data object Loading : ItemUiState
    }

    fun startGuestSession(){
        _isGuestSession.value = true
    }

    fun endGuestSession(){
        _isGuestSession.value = false
    }

    fun setPhoneNumber(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    fun setOtp(otp: String){
        _otp.value = otp
    }
    fun setVerificationId(verificationId: String){
        _verificationId.value = verificationId
    }

    fun proceedToPay(){
        _showPaymentScreen.value = true
        resetPaymentCountdown()
    }

    // ✅ 4. FIX THE completePayment() FUNCTION
    fun completePayment(){
        _showPaymentScreen.value = false
        if (_cartItems.value.isNotEmpty()) {
            val aggregateItems = _cartItems.value.groupBy { it.itemName }
                .map { (name, items) ->
                    val firstItem = items.first()
                    val totalQuantity = items.size
                    firstItem.copy()
                }
            val newOrder = Order(items = aggregateItems)
            ordersRef.push().setValue(newOrder)
            cartRef.removeValue()
            _cartItems.value = emptyList()

        }
    }

    // ✅ 5. FIX THE fillOrdersFromDatabase() FUNCTION
    private fun fillOrdersFromDatabase(){
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val firebaseOrders = mutableListOf<Order>()
                for (childSnapshot in dataSnapshot.children) {
                    val order = childSnapshot.getValue(Order::class.java)
                    order?.let{
                        firebaseOrders.add(it)
                    }
                }
                _orders.value = firebaseOrders
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read Orders: ${error.toException()}")
            }
        })
    }

    fun cancelPayment(){
        _showPaymentScreen.value = false
    }


    fun setUser(user: FirebaseUser?)
    {
        _user.value = user
        endGuestSession()
        if(user != null){
            // When a user logs in, fetch their cart items and order history
            fillCartItems()
            fillOrdersFromDatabase()
        }
    }
    fun endGuesstSession(){
        _isGuestSession.value = false
    }
    fun clearData(){
        _user.value = null
        _phoneNumber.value = ""
        _otp.value = ""
        _verificationId.value = ""
        _cartItems.value = emptyList()
        _orders.value = emptyList()
        endGuestSession()
        resetTime()
    }
    fun runTimer(){
        timerJob = viewModelScope.launch {
            while (_ticks.value > 0){
                delay(1000)
                _ticks.value-=1
            }
        }
    }

    fun setLoading(isLoading : Boolean){
        _loading.value = isLoading
    }
    fun setLogoutClicked(clicked: Boolean){
        _logoutClicked.value = clicked
    }

    fun setPaymentCountdown(value: Int){
        _paymentCountdown.value = value
    }


    fun resetPaymentCountdown(){
        _paymentCountdown.value = 10
    }

    fun resetTime(){
        try {
            timerJob.cancel()
        }catch (_: Exception){

        }finally {
            _ticks.value = 60L
        }
    }

    fun addToCart(item: InternetItem){
        _cartItems.value = _cartItems.value + item
    }

    fun addToDatabase(item: InternetItem){
        // Ensure user is logged in before writing
        if (auth.currentUser != null) {
            cartRef.push().setValue(item)
        }
    }

    private fun fillCartItems(){
        // Ensure user is logged in before listening
        if (auth.currentUser != null) {
            cartRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val items = mutableListOf<InternetItem>()
                    for (childSnapshot in dataSnapshot.children) {
                        val item = childSnapshot.getValue(InternetItem::class.java)
                        item?.let {
                            items.add(it)
                        }
                    }
                    _cartItems.value = items
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to read cart items: ${error.toException()}")
                }
            })
        }
    }
    fun removeFromCart(oldItem: InternetItem){
        if (auth.currentUser != null) {
            cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        var itemRemoved = false
                        val item = childSnapshot.getValue(InternetItem::class.java)
                        if (item != null && oldItem.itemName == item.itemName && oldItem.itemCategory == item.itemCategory) {
                            childSnapshot.ref.removeValue()
                            itemRemoved = true
                        }
                        if (itemRemoved) break
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to remove item: ${error.toException()}")
                }
            })
        }
    }
    fun updateClickText(updatedText: String) {
        _uiState.update {
            it.copy(
                clickStatus = updatedText
            )
        }
    }


    fun updateSelectedCategory(updatedCategory: Int){
        _uiState.update{
            it.copy(
                selectedCategory = updatedCategory
            )
        }
    }

    fun decreaseItemCount(itemToRemove: InternetItem) {
        // This logic finds the first matching item in Firebase and removes it.
        cartRef.orderByChild("itemName").equalTo(itemToRemove.itemName).limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        val firstChild = snapshot.children.first()
                        firstChild.ref.removeValue()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    println("Failed to decrease item count: ${error.toException()}")
                }
            })
    }
    private fun toggleVisibility(){
        _isVisible.value = false
    }
    fun getFirstItem() {
        internetJob = viewModelScope.launch {
            // Reset state to loading before trying again
            itemUiState = ItemUiState.Loading
            try {
                val listResult = FirstApi.retrofitService.getItems()
                itemUiState = ItemUiState.Success(listResult)
            }
            catch (e: Exception) {
                // 1. THIS IS THE CRITICAL CHANGE: Log the real error to Logcat
                // Look for "RETR_ERR" in Android Studio Logcat tab
                println("RETR_ERR: ${e.message}")
                e.printStackTrace()

                itemUiState = ItemUiState.Error
                toggleVisibility()
                screenJob.cancel()
            }
        }
    }
    init{
        screenJob = viewModelScope.launch(Dispatchers.Default) {
            delay(3000)
            toggleVisibility()
        }
        getFirstItem()
        // Removed fillCartItems() from init as it should only run after login
    }
}
