package com.github.rwsbillyang.composeui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * 受控组件
 *
 * 给定初始值后，有可能再加载重新赋值，需设计为受控组件：除了自身用户交互更新值，还可能来自父组件的更新,
 * 父组件有责任更新子组件，哪怕是子组件自身的修改
 *
 * 若不需再重新赋值，只是交互控件自身改变值，可设计为非受控组件，
 * 自己负责自己的更新，无需父组件介入，最终把更新后的值传递给父组件即可
 *
 * */
@Composable
fun <T> RadioGroup(radioOptions: Array<T>, optionLabel: (T) -> String, selected:T, onOptionSelected: (T) -> Unit){
    //https://developer.android.google.cn/reference/kotlin/androidx/compose/material/package-summary#RadioButton(kotlin.Boolean,kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.foundation.interaction.MutableInteractionSource,androidx.compose.material.RadioButtonColors)
    Row(Modifier.selectableGroup().padding(5.dp), Arrangement.Start,Alignment.CenterVertically) {//Column(Modifier.selectableGroup()) {}
        radioOptions.forEach {
            RadioItem(it, selected == it, optionLabel){
                onOptionSelected(it)}
        }
    }
}


@Composable
fun <T> RadioItem(it: T, isSelected: Boolean, label: (T) -> String, onOptionSelected: (T) -> Unit) {
    Row(
        Modifier
            .selectable(
                selected = (isSelected),
                onClick = { onOptionSelected(it) },
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null // null recommended for accessibility with screenreaders
        )
        Text(
            text = label(it),
            style = MaterialTheme.typography.bodyMedium.merge(),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}