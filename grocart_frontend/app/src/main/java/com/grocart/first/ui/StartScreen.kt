//@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.grocart.first.ui

import android.content.Context
import android.widget.Toast
import java.text.Normalizer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester

import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.grocart.first.data.DataSource
import kotlinx.coroutines.delay

private fun String.norm(): String {
    val lower = lowercase()
    val nfd = Normalizer.normalize(lower, Normalizer.Form.NFD)
    return nfd.replace("\\p{M}+".toRegex(), "")
}

@Composable
fun StartScreen(
    groViewModel: GroViewModel,
    onCategoryClicked: (Int) -> Unit
) {
    val context = LocalContext.current

    // Search state (no expansion)
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var inSearchMode by remember { mutableStateOf(false) }

    val all = remember { DataSource.loadCategories() }

    // Live filtering with startsWith first, then contains
    val filtered = remember(query.text, all) {
        val q = query.text.trim().norm()
        if (q.isEmpty()) all
        else {
            val triples = all.map { item ->
                val name = context.getString(item.stringResourceId)
                Triple(item, name, name.norm())
            }
            val starts = triples.filter { it.third.startsWith(q) }.map { it.first }
            val contains = triples.filter { it.third.contains(q) && !it.third.startsWith(q) }.map { it.first }
            starts + contains
        }
    }

    // Keyboard/focus
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    // Auto-focus when entering search mode
    LaunchedEffect(inSearchMode) {
        if (inSearchMode) {
            delay(50)
            focusRequester.requestFocus()
            keyboard?.show()
        } else {
            focusManager.clearFocus(force = true)
            keyboard?.hide()
        }
    }

    // Back: exit search mode first
    BackHandler(enabled = inSearchMode) {
        query = TextFieldValue("")
        inSearchMode = false
    }

    Column(Modifier.fillMaxSize()) {
        // Fixed search row (no expanding content)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 0.dp,
            color = Color(0x99D8D8D8)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { state -> if (state.isFocused) inSearchMode = true },
                value = query,
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                onValueChange = { query = it },
                placeholder = { Text("Search categories") },
                singleLine = true,

                leadingIcon = {
                    if (inSearchMode) {
                        IconButton(onClick = {
                            query = TextFieldValue("")
                            inSearchMode = false
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close search"
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                trailingIcon = {
                    if (query.text.isNotEmpty()) {
                        IconButton(onClick = { query = TextFieldValue("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear text")
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent

                )
            )
        }

        // Grid: hide header only while inSearchMode; categories always visible and filtered
        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (!inSearchMode) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,

                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(215, 208, 208, 205)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
//                                .padding(vertical = 1.dp),
                            shape = RoundedCornerShape(12.dp),
//                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Shop By Category",
                                    fontWeight = FontWeight.Normal,


                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.Black,
                                    fontSize = 22.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }

            if (filtered.isEmpty() && query.text.isNotBlank()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "No categories match \"${query.text}\"",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(filtered) { item ->
                    CategoryCard(
                        context = context,
                        stringResourceId = item.stringResourceId,
                        imageResourceId = item.imageResourceId,
                        groViewModel = groViewModel,
                        onCategoryClicked = { id ->
                            if (inSearchMode) {
                                // Optional: exit search on navigation
                                query = TextFieldValue("")
                                inSearchMode = false
                            }
                            onCategoryClicked(id)
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    context: Context,
    stringResourceId: Int,
    imageResourceId: Int,
    groViewModel: GroViewModel,
    onCategoryClicked: (Int) -> Unit
) {
    val categoryName = stringResource(id = stringResourceId)

    Card(
        onClick = {
            groViewModel.updateClickText(categoryName)
            Toast.makeText(context, "$categoryName selected", Toast.LENGTH_SHORT).show()
            onCategoryClicked(stringResourceId)
        },
        colors = CardDefaults.cardColors(
            containerColor = Color(217, 213, 213, 157)
        ),
        modifier = Modifier,

    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .width(350.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = categoryName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(150.dp),
                textAlign = TextAlign.Center
            )
            Image(
                painter = painterResource(imageResourceId),
                contentDescription = categoryName,
                modifier = Modifier.size(150.dp)
            )
        }
    }
}
