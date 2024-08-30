package com.example.ecojourney

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Button
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextAlign
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import com.google.firebase.firestore.FirebaseFirestore

fun loginUser(
    email: String,
    password: String,
    navController: NavHostController,
    onLoginResult: (Boolean, String?) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val userId = auth.currentUser?.uid ?: ""
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        onLoginResult(true, null)
                        // Navigate to MainScreen if login is successful
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        onLoginResult(false, "User data not found")
                    }
                }
                .addOnFailureListener { e ->
                    onLoginResult(false, "Failed to fetch user data: ${e.message}")
                }
        } else {
            onLoginResult(false, task.exception?.message ?: "Login failed")
        }
    }
}

@Composable
fun LoginScreen(
    navController: NavHostController,
    onLoginResult: (Boolean, String?) -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Check if email and password fields are not empty
    val isLoginButtonEnabled = email.isNotEmpty() && password.isNotEmpty()

    Surface(
        color = Color(0xFF3F6B1B),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(650.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ecowhite),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "EcoJourney",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PJakartaFontFamily,
                            color = Color.White
                        ),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Text(
                    "Masuk",
                    style = TextStyle(
                        fontSize = 26.sp,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F6B1B)
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 50.dp, bottom = 20.dp)
                )

                CTextField(title = "Email", hint = "Masukkan Email", value = email, onValueChange = { email = it })

                CTextFieldPass(title = "Password", hint = "Masukkan Kata Sandi", value = password, onValueChange = { password = it }, isPassword = true)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Lupa kata Sandi?",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("resetPassword")
                        }
                        .padding(vertical = 8.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF3F6B1B),
                        fontFamily = PJakartaSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoginButtonEnabled) {
                    CButton(
                        text = "Masuk",
                        onClick = {
                            coroutineScope.launch {
                                loginUser(email, password, navController, onLoginResult)
                                navController.navigate("main")
                            }
                        }
                    )
                } else {
                    CButton2(text = "Masuk")
                }

                Spacer(modifier = Modifier.height(20.dp))

                OrDivider()

                Spacer(modifier = Modifier.height(20.dp))

                CButtonOutlinedG(
                    text = "Masuk dengan Google",
                    onClick = {
                        onGoogleSignInClick()
                    },
                    iconResId = R.drawable.google_ic
                )

                Spacer(modifier = Modifier.height(20.dp))

                CButtonOutlinedF(
                    text = "Masuk dengan Facebook",
                    onClick = {
                        // Handle login with Facebook
                    },
                    iconResId = R.drawable.fcb_ic
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                DontHaveAccountRow(
                    onSignupTap = {
                        navController.navigate("signup")
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        navController = rememberNavController(),
        onLoginResult = { isSuccess, message ->
            // Handle result here, e.g., show a Toast in a real app
        },
        onGoogleSignInClick = { /* Handle Google Sign-In click */ }
    )
}

