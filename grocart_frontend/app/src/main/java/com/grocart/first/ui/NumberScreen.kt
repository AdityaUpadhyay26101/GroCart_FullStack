package com.grocart.first.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Composable
fun NumberScree(
    groViewModel: GroViewModel,
    callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
) {
    val phoneNumber by groViewModel.phoneNumber.collectAsState()
    val context = LocalContext.current

    // ✅ FirebaseAuth instance
    val auth = FirebaseAuth.getInstance()

    Text(
        text = "LOGIN",
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        fontStyle = FontStyle.Italic,
    )
    Text(
        text = "Enter Your Phone Number",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center

    )
    Text(
        text = "We will send you an OTP to verify next",
        fontSize = 12.sp,
        color = Color(105, 103, 100)
    )
    TextField(
        value = phoneNumber,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        onValueChange = {
            groViewModel.setPhoneNumber(it)
        },
        label = {
            Text(text = "Your Phone Number")
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Button(
        onClick = {
            if (phoneNumber.isNotEmpty() && phoneNumber.length == 10) {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber("+91$phoneNumber") // ✅ Use +91 with entered number
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(context as Activity)
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
                groViewModel.setLoading(true)
            } else {
                Toast.makeText(context, "Enter valid phone number", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Send OTP",
            fontSize = 16.sp
        )
    }
}