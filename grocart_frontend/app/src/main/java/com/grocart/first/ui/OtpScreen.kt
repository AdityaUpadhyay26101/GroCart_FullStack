package com.grocart.first.ui

import android.app.Activity
import android.content.Context
import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


@Composable
fun OtpScreen(otp : String,
              groViewModel: GroViewModel,
              callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
){
    val context = LocalContext.current
    val verificationId by groViewModel.verificationId.collectAsState()
    val ticks by groViewModel.ticks.collectAsState()
    val phoneNumber by groViewModel.phoneNumber.collectAsState()

    OtpBox(otp = otp,
        groViewModel = groViewModel)

    Button(
        onClick = {
            if(otp.isEmpty()){
                Toast.makeText(context, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }else{
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                signInWithPhoneAuthCredential(
                    credential = credential,
                    context = context,
                    groViewModel = groViewModel
                )

            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Verify Otp")
    }
    Text(text = if(ticks == 0L) "Resend OTP" else "Resend OTP(${DateUtils.formatElapsedTime(ticks)})",
        color = Color(63,81,181,255),
        fontWeight = if (ticks == 0L ) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier.clickable{
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+91$phoneNumber") // ✅ Use +91 with entered number
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context as Activity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    )
}

@Composable
fun OtpBox(otp : String,
           groViewModel: GroViewModel){
    BasicTextField(value = otp, onValueChange = {
        groViewModel.setOtp(it)
    },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    {
        Row(
            horizontalArrangement = Arrangement.Center
        ){
            repeat(6){ index ->
                val number = when{
                    index >= otp.length -> ""
                    else -> otp[index].toString()
                }

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ){
                    Text(text = number, fontSize = 32.sp)
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(2.dp)
                            .background(Color.Gray)
                    ){

                    }
                }

            }

        }

    }
}
private fun signInWithPhoneAuthCredential(
    credential: PhoneAuthCredential,
    context : Context,
    groViewModel: GroViewModel
) {
    auth.signInWithCredential(credential)
        .addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Toast.makeText(context, "Verification Successful", Toast.LENGTH_SHORT).show()

                val user = task.result?.user
                groViewModel.setUser(user)

            } else {
                // Sign in failed, display a message and update the UI

                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                    Toast.makeText(context, "The OTP you have entered is invalid. Please try again.", Toast.LENGTH_SHORT).show()
                }
                // Update UI
            }
        }
}