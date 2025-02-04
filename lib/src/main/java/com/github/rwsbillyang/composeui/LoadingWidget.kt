package com.github.rwsbillyang.composeui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun LoadingWidget(isLoading: Boolean, content: @Composable ()-> Unit) {
    if(isLoading){
        Row(Modifier.wrapContentWidth(), Arrangement.Center, Alignment.CenterVertically){
            content()
            CircularProgressIndicator(Modifier.size(18.dp))
        }
    }else{
        content()
    }
}