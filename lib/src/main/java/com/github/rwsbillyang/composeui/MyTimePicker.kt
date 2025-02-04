package com.github.rwsbillyang.composeui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.util.*

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
fun MyTimePicker(
    hour: String?,
    minute: String?,
    divider: String = ":",
    onChanged: (hour: String, minute: String)-> Unit)
{
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    val now = LocalTime.now()
    val h by rememberUpdatedState(hour?:now.hour.toString())
    val m by rememberUpdatedState(minute?:now.minute.toString())
//    var hour by remember { mutableStateOf((defaultHour?:now.hour).toString()) }
//    var minute by remember { mutableStateOf((defaultMinute?:now.minute).toString()) }

    val w = 25

    Column(Modifier.fillMaxWidth()){
        Row(Modifier.fillMaxWidth(), Arrangement.Start, Alignment.CenterVertically){
            TextField(
                value = h,
                onValueChange = { onChanged(it.trim(), m)},
                Modifier.width((w*2+6).dp),
                textStyle = inputTextStyle,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Text(divider)
            TextField(
                value = m,
                onValueChange = { onChanged(h, it.trim()) },
                Modifier.width((w*2+6).dp),
                textStyle = inputTextStyle,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            IconButton(onClick = { setShowDialog(!showDialog) }, Modifier.size(48.dp)){
                Icon(imageVector = Icons.Rounded.Schedule, contentDescription = "set time")
            }
        }

        if (showDialog) {
            TimePickerDialog(h, m,setShowDialog){ h2, m2 ->
                onChanged(h2.toString(), m2.toString())
            }
        }
    }
}

//https://developer.android.google.cn/reference/kotlin/androidx/compose/material3/package-summary#TimePicker(androidx.compose.material3.TimePickerState,androidx.compose.ui.Modifier,androidx.compose.material3.TimePickerColors,androidx.compose.material3.TimePickerLayoutType)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(hour: String?, minute: String?, setShowDialog: (Boolean) -> Unit,
                     onChanged: (hour: Int, minute: Int)-> Unit){

    val now = LocalTime.now()
    val h = if(hour.isNullOrEmpty()) now.hour else hour.toInt()
    val m = if(minute.isNullOrEmpty()) now.minute else minute.toInt()
    val state = rememberTimePickerState(h, m, true)
    //val formatter = remember { SimpleDateFormat("hh:mm", Locale.getDefault()) }

    val configuration = LocalConfiguration.current

    AlertDialog(
        onDismissRequest = { setShowDialog(false) },
        title = { Text(text = "设置时间") },
        confirmButton = {
            TextButton(onClick = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                cal.isLenient = false

                onChanged(state.hour, state.minute)

                setShowDialog(false)
            }) {   Text("确定")   }
        },
        dismissButton = {  TextButton(onClick = { setShowDialog(false) })  {  Text("取消") }},
        text = {
            if (configuration.screenHeightDp > 400) {
                TimePicker(state = state)
            } else {
                TimeInput(state = state)
            }
        }
    )
}