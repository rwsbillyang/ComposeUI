package com.github.rwsbillyang.composeui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInput(label: String, tooltip: String? = null, prompt: String = ": ", input: @Composable ()->Unit){
    Row(FormLineModifier, Arrangement.Start, Alignment.CenterVertically) {
        if(tooltip.isNullOrEmpty()){
            Text("$label$prompt", Modifier.weight(22f))
        }else{
            TooltipBox( //Added in 1.2.0-alpha10
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {   PlainTooltip {   Text(tooltip) }  },
                state = rememberTooltipState()
            ) {
                Text("$label$prompt", Modifier.weight(22f))
            }
        }
        Box(Modifier.weight(78f)){
            input()
        }
    }
}