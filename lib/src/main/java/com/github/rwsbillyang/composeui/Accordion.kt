package com.github.rwsbillyang.composeui


import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput


@Composable
fun Accordion(title: @Composable () -> Unit,
              iconTint: Color = LocalContentColor.current,
              initialExpand: Boolean = false,
              horizontalAlignment: Alignment.Horizontal = Alignment.Start,
              content: @Composable () -> Unit){
    var expanded by remember { mutableStateOf(initialExpand) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(Modifier.fillMaxWidth(), Arrangement.Top, horizontalAlignment){
        Row(Modifier.pointerInput(Unit) {  detectTapGestures(onTap = { expanded = !expanded })}){
            title()
            Icon(icon,"expand dropdown", tint = iconTint)
        }
        if(expanded){
            content()
        }
    }
}