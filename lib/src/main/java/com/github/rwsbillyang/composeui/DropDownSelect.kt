package com.github.rwsbillyang.composeui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize


/**
 * 受控组件
 *
 * 给定初始值后，有可能再加载重新赋值，需设计为受控组件：除了自身用户交互更新值，还可能来自父组件的更新,
 * 父组件有责任更新子组件，哪怕是子组件自身的修改
 *
 * 若不需再重新赋值，只是交互控件自身改变值，可设计为非受控组件，
 * 自己负责自己的更新，无需父组件介入，最终把更新后的值传递给父组件即可
 *
 * @param defaultOptions 指定选项列表
 * @param value 外部父控件设置选中的值，isControlled设置为true才有意义
 * @param isControlled 是否为受控组件，当为true时，value才有意义，将使用value设置选中的值，否则value不被使用，指定也无效
 * */
@Composable
fun <T> DropDownSelect(
    item2Label: (T) -> String,
    onChanged: (value: T?, index: Int?) -> Unit,
    modifier: Modifier = Modifier,
    defaultOptions: Collection<T>,
    placeholder: String? = null,
    value: T? = null,
    isControlled: Boolean = false,
    clearButton: Boolean = true,
    useTextFieldEdit: Boolean = false,
    readOnly: Boolean = false, //useTextFieldEdit 为true时适用
    optionRender: (@Composable (T) -> Unit) ? = null
){
    DropDownSelect(item2Label, onChanged, modifier, placeholder,defaultOptions, value, isControlled,
        clearButton,useTextFieldEdit, readOnly,  optionRender, false, null,null)
}

/**
 * 受控组件与非受控
 *
 * 给定初始值后，有可能再加载重新赋值，需设计为受控组件：除了自身用户交互更新值，还可能来自父组件的更新,
 * 父组件有责任更新子组件，哪怕是子组件自身的修改
 *
 * 若不需再重新赋值，只是交互控件自身改变值，可设计为非受控组件，
 * 自己负责自己的更新，无需父组件介入，最终把更新后的值传递给父组件即可
 *
 * @param value 外部父控件设置选中的值，isControlled设置为true才有意义
 * @param isControlled 是否为受控组件，当为true时，value才有意义，将使用value设置选中的值，否则value不被使用，指定也无效
 * @param loading 外部父组件指定是否loading状态
 * @param params 请求参数
 * @param loadOptions 获取options列表的请求
 *
 * */
@Composable
fun <T, Q> DropDownSelect(
    item2Label: (T) -> String,
    onChanged: (value: T?, index: Int?) -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean,
    placeholder: String? = null,
    params: Q? = null,
    value: T? = null,
    isControlled: Boolean = false,
    clearButton: Boolean = true,
    useTextFieldEdit: Boolean = false,
    readOnly: Boolean = false, //useTextFieldEdit 为true时适用
    optionRender: (@Composable (T) -> Unit) ? = null,
    loadOptions: (suspend (params: Q?) -> List<T>?)
){
    DropDownSelect(item2Label, onChanged, modifier, placeholder,null,  value, isControlled,
        clearButton,useTextFieldEdit, readOnly,  optionRender,loading, params,loadOptions)
}
@Composable
fun <T, Q> DropDownSelect(
    item2Label: (T) -> String,
    onChanged: (value: T?, index: Int?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    defaultOptions: Collection<T>? = null,
    value: T? = null,
    isControlled: Boolean = false,
    clearButton: Boolean = true,
    useTextFieldEdit: Boolean = false,
    readOnly: Boolean = false, //useTextFieldEdit 为true时适用
    optionRender: (@Composable (T) -> Unit) ? = null,
    loading: Boolean = false,
    params: Q? = null,
    loadOptions: (suspend (params: Q?) -> List<T>?)? = null
){

    // Declaring a boolean value to store the expanded state of the Text Field
    var mExpanded by remember { mutableStateOf(false) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    var isLoading by remember { mutableStateOf(false) }
    val currentLoading by rememberUpdatedState(loading)
    var options by remember { mutableStateOf(defaultOptions) }

    val currentValue by rememberUpdatedState(value)
    var selected by remember { mutableStateOf<T?>(null) }

    LaunchedEffect(params){
        if(loadOptions != null) {
            isLoading = true
            options = loadOptions(params)
            isLoading = false
        }
    }

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val iconSizeModifier = Modifier.size(16.dp)

    Column(modifier) {
        val v = if(isControlled) currentValue else selected
        val text = v?.let { item2Label(it) }
        //Log.d(AppConstants.TAG, "render value=$text")
        if(useTextFieldEdit){
            TextField(
                value = text?:placeholder?:"", //若指定的尺寸过小，则文字不能显示出来
                onValueChange = {if(it == ""){
                    selected = null
                    onChanged(null, null)
                } },
                readOnly = readOnly,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        mTextFieldSize = coordinates.size.toSize()
                    },
                textStyle = if(text.isNullOrEmpty()){
                    if(placeholder.isNullOrEmpty()){
                        inputTextStyle
                    }else{
                        inputTextStyle.copy(color = MaterialTheme.colorScheme.secondary)
                    }
                }else{
                    inputTextStyle
                },
                trailingIcon = {
                    if(currentLoading || isLoading){
                        CircularProgressIndicator(Modifier.fillMaxHeight())
                    }else
                        if(clearButton){
                            if(text.isNullOrEmpty()){
                                Icon(icon,"expand dropdown",
                                    iconSizeModifier.clickable { mExpanded = !mExpanded })
                            }else{
                                Icon(Icons.Filled.Close,"clear content",
                                    iconSizeModifier.clickable { selected = null; onChanged(null, null) })
                            }
                        }else{
                            Icon(icon,"expand dropdown",
                                iconSizeModifier.clickable { mExpanded = !mExpanded })
                        }
                }
            )
        }else{
            AssistChip(
                onClick = { mExpanded = !mExpanded },
                label = {
                    if(text.isNullOrEmpty()){
                        if(placeholder.isNullOrEmpty()){
                            Text("", softWrap= false, overflow = TextOverflow.Ellipsis, style = inputTextStyle)
                        }else{
                            Text(placeholder, softWrap= false, overflow = TextOverflow.Ellipsis, style = inputTextStyle.copy(color = MaterialTheme.colorScheme.secondary))
                        }
                    }else{
                        Text(text, softWrap= false, overflow = TextOverflow.Ellipsis, style = inputTextStyle)
                    }},
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        mTextFieldSize = coordinates.size.toSize()
                    },
                trailingIcon = {
                    if(currentLoading || isLoading){
                        CircularProgressIndicator(Modifier.fillMaxHeight())
                    }else
                        if(clearButton){
                            if(text.isNullOrEmpty()){
                                Icon(icon,"expand dropdown",
                                    iconSizeModifier.clickable { mExpanded = !mExpanded })
                            }else{
                                Icon(Icons.Filled.Close,"clear content",
                                    iconSizeModifier.clickable { selected = null; onChanged(null, null) })
                            }
                        }else{
                            Icon(icon,"expand dropdown",
                                iconSizeModifier.clickable { mExpanded = !mExpanded })
                        }
                }
            )
        }

        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier.width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
        ) {
            options?.forEachIndexed { index, it ->
                DropdownMenuItem(
                    text = if (optionRender != null) {
                        { optionRender(it) }
                    } else {
                        {
                            if(it == v){
                                Text(text = item2Label(it), color = MaterialTheme.colorScheme.primary)
                            }else{
                                Text(text = item2Label(it), color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    },
                    onClick = {
                        mExpanded = false
                        selected = it
                        onChanged(it, index)
                    })
            }
        }
    }
}

