package com.grocart.first.ui


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.grocart.first.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

@Composable
fun LoginUi(groViewModel: GroViewModel) {
    val context = LocalContext.current
    val otp by groViewModel.otp.collectAsState()
    val verificationId by groViewModel.verificationId.collectAsState()
    val loading by groViewModel.loading.collectAsState()

    // Callbacks for OTP verification
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            groViewModel.setLoading(false)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This function is called when the verification fails, e.g., invalid phone number.
            Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
            groViewModel.setLoading(false)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number,
            // we now need to ask the user to enter the code and then construct a
            // credential by combining the code with a verification ID.
            groViewModel.setVerificationId(verificationId)
            Toast.makeText(context, "OTP sent", Toast.LENGTH_SHORT).show()
            groViewModel.resetTime()
            groViewModel.runTimer()
            groViewModel.setLoading(false)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            // Use spacedBy to ensure consistent spacing between all elements in the column
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.otp),
                contentDescription = "App Icon",
                modifier = Modifier
                    .padding(
                        top = 50.dp,
                        bottom = 10.dp
                    )
                    .size(250.dp)
            )

            // Conditionally display Number or OTP screen
            if (verificationId.isEmpty()) {
                NumberScree(groViewModel = groViewModel, callbacks = callbacks)
            } else {
                OtpScreen(otp = otp, groViewModel = groViewModel, callbacks = callbacks)
            }

            // ✅ MOVED GUEST LOGIN LOGIC HERE, INSIDE THE COLUMN
            Text(
                text = "or",
                modifier = Modifier.padding(vertical = 8.dp),
            )
            OutlinedButton(
                onClick = { groViewModel.startGuestSession() },
                modifier = Modifier.fillMaxWidth(0.8f) // Use a fraction for better padding
            ) {
                Text("Continue as Guest")
            }
        }

        // The back button is kept in the Box to overlay on top of the Column
        if (verificationId.isNotEmpty()) {
            IconButton(
                onClick = {
                    groViewModel.setVerificationId("")
                    groViewModel.setOtp("")
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        // The loading indicator is also kept in the Box to overlay on everything
        if (loading) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(255, 255, 255, 210)) // Slightly more opaque
            ) {
                LinearProgressIndicator()
                Text(text = "Loading...")
            }
        }
    }
}
