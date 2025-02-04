package com.github.rwsbillyang.composeui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

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
fun MyDatePicker(
    year: String?,
    month: String?, //[1,12]
    day: String?, //[1, 31]
    divider: String = "-",
    onChanged: (year: String, month: String, day: String)-> Unit)
{
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    val now = LocalDate.now()
    val y by rememberUpdatedState(year?:now.year.toString())
    val m by rememberUpdatedState(month?:now.monthValue.toString())
    val d by rememberUpdatedState(day?:now.dayOfMonth.toString())

//    var year by remember { mutableStateOf((defaultYear?:now.year).toString()) }
//    var month by remember { mutableStateOf((defaultMonth?:now.monthValue).toString()) }
//    var day by remember { mutableStateOf((defaultDay?:now.dayOfMonth).toString()) }

    val w = 25

    Column(Modifier.fillMaxWidth()){
        Row(Modifier.fillMaxWidth(), Arrangement.Start, Alignment.CenterVertically){
            TextField(
                value = y,
                onValueChange = { onChanged(it.trim(), m, d) },
                Modifier.width((w*4).dp),
                textStyle = inputTextStyle,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Text(divider)
            TextField(
                value = m,
                onValueChange = { onChanged(y, it.trim(), d) },
                Modifier.width((w*2+6).dp),
                textStyle = inputTextStyle,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Text(divider)
            TextField(
                value = d,
                onValueChange = { onChanged(y, m, it.trim()) },
                Modifier.width((w*2+6).dp),
                textStyle = inputTextStyle,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            IconButton({ setShowDialog(!showDialog)  }, Modifier.size(48.dp)
                //,colors = IconButtonColors(Color.Transparent, MaterialTheme.colorScheme.primary, Color.Transparent, MaterialTheme.colorScheme.secondary)
            ){
                Icon(imageVector = Icons.Rounded.CalendarMonth, contentDescription = "set date")
            }
        }

        if (showDialog) {
            DatePickerDialog(y, m, d, setShowDialog){y2, m2, d2 ->
                onChanged(y2.toString(),m2.toString(),d2.toString())
            }
        }
    }
}

//TODO: support ChineseCalendar https://developer.android.google.cn/reference/android/icu/util/ChineseCalendar?hl=en
//https://developer.android.google.cn/reference/kotlin/androidx/compose/material3/package-summary#DatePicker(androidx.compose.material3.DatePickerState,androidx.compose.ui.Modifier,androidx.compose.material3.DatePickerFormatter,kotlin.Function0,kotlin.Function0,kotlin.Boolean,androidx.compose.material3.DatePickerColors)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    year: String?, month: String?, day: String?,
    setShowDialog: (Boolean) -> Unit,
    onChanged: (year: Int, month: Int, day: Int)-> Unit)
{
    val now = if(!year.isNullOrEmpty()){
        val m = if(month.isNullOrEmpty()) 1 else month.toInt()
        val d = if(day.isNullOrEmpty()) 1 else day.toInt()
        LocalDateTime.of(year.toInt(), m, d, 0,0,0)
            .toInstant(ZoneOffset.UTC).toEpochMilli()
            //.toEpochSecond(LocalTime.now(), ZoneOffset.ofHours(8))
    }else System.currentTimeMillis()

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = now)
    //val formatter = remember { SimpleDateFormat("hh:mm", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = { setShowDialog(false) },
        title = { Text(text = "设置日期") },
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC)
                }?.also {
                    onChanged(it.year, it.monthValue, it.dayOfMonth)
                }
                //val cal: Calendar = Calendar.getInstance(ULocale("zh_CN@calendar=chinese"))
                setShowDialog(false)
            }) {   Text("确定")   }
        },
        dismissButton = {  TextButton(onClick = { setShowDialog(false) })  {  Text("取消") }},
        text = {
            DatePicker(state = datePickerState )
        }
    )
}