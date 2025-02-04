package com.github.rwsbillyang.composeui


//import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 普通的wheel picker
 * TODO: onSelect尚不能准确上报数据
 * 1. 支持placeholder提示
 * 2. 支持clearButton 一键清空
 * 3. 不支持无限循环滚动
 * 4. 不支持动态加载选项列表
 * */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPickerTODO(
    list: List<T>,
    onSelect: (index: Int?, item: T?) -> Unit,
    selectIndex: Int? = null,
    visibleCount: Int = 1,
    clearButton: Boolean = true,
    placeholder: String = "滑动选择",
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(32.dp)
        .border(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            RoundedCornerShape(6.dp)
        ),
    useTransition: Boolean = false,
    key: ((index:Int, item: T) -> Any)? = null,
    itemContent: @Composable LazyItemScope.(index:Int, item: T) -> Unit
){
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }

    //Log.d(AppConstants.TAG,"selectIndex=$selectIndex, firstVisibleItemIndex=${listState.firstVisibleItemIndex}")

    LaunchedEffect(Unit){
        listState.scrollToItem(0)
    }

    BoxWithConstraints(modifier = modifier, propagateMinConstraints = true) {
        val density = LocalDensity.current
        val pickerHeightPx = density.run { maxHeight.toPx() }
        val pickerCenterLinePx = pickerHeightPx / 2
        val itemHeight = maxHeight / visibleCount

        Row(Modifier.fillMaxSize(), Arrangement.SpaceBetween, Alignment.CenterVertically){
            Spacer(Modifier.width(1.dp).weight(1f))
            LazyColumn(Modifier.fillMaxHeight().weight(80f), state = listState,flingBehavior = rememberSnapFlingBehavior(listState)) {
                item {
                    //Log.d(AppConstants.TAG, "placeholder=$placeholder")
                    Text(placeholder,  Modifier.fillMaxWidth(), fontSize = mediumFont, color = Color.Gray,textAlign = TextAlign.Center)
                }

                itemsIndexed(list, key, contentType = {_,_ -> null }){index, item ->
                    //Log.d(AppConstants.TAG, "index=$index, item=$item, visibleItems=${layoutInfo.visibleItemsInfo.joinToString(",") { "${it.key}" }}")
                    val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == index }
                    var percent = 1f
                    if (itemInfo != null) {
                        val itemCenterY = itemInfo.offset + itemInfo.size / 2
                        percent = if (itemCenterY < pickerCenterLinePx) {
                            itemCenterY / pickerCenterLinePx
                        } else {
                            1 - (itemCenterY - pickerCenterLinePx) / pickerCenterLinePx
                        }

                        //非滑动状态，且item正好跨越wheel picker的中间线状态
                        if (!listState.isScrollInProgress
                            && itemInfo.offset < pickerCenterLinePx
                            && itemInfo.offset + itemInfo.size > pickerCenterLinePx
                        ) {
                            //Log.d(AppConstants.TAG, "notify: index=$index")
                            onSelect(index, item)
                        }
                    }

                    if(useTransition){
                        Box(Modifier.graphicsLayer {
                                    alpha = 0.75f + 0.25f * percent
                                    scaleX = 0.75f + 0.25f * percent
                                    scaleY = 0.75f + 0.25f * percent
                                    rotationX = (1 + (0.75f + 0.25f * percent)) * 180
                                }.fillMaxWidth().height(itemHeight),contentAlignment = Alignment.Center,
                        ) {
                            itemContent(index, item)
                        }
                    }else
                        Box(Modifier.fillMaxWidth().height(itemHeight),contentAlignment = Alignment.Center){
                            itemContent(index, item)
                        }
                }
            }

            if(clearButton){
                Icon(Icons.Filled.Close,"clear",
                    Modifier
                        .weight(19f)
                        .size(16.dp)
                        .clickable {
                            scope.launch {
                                listState.scrollToItem(0)
                            }
                            onSelect(null, null)
                        }, Color.Gray)
            }
        }

    }
}


/**
 *
 * 无限循环滚动 wheel picker
 * 1. 支持无限循环滚动
 * 2. 不支持placeholder提示
 * 3. 不支持clearButton一键清空
 * 4. 不支持动态加载选项列表
 * https://developer.android.google.cn/jetpack/compose/lists?hl=zh_cn
 * https://juejin.cn/post/7266702105829277754
 * */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPickerInfinite(
    data: List<T>,
    onSelect: (index: Int, item: T) -> Unit,
    selectIndex: Int? = null,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(32.dp)
        .border(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            RoundedCornerShape(6.dp)
        ),
    visibleCount: Int = 1,
    useTransition: Boolean = false,
    content: @Composable (item: T) -> Unit
) {
    val size = data.size
    BoxWithConstraints(modifier = modifier, propagateMinConstraints = true) {
        val density = LocalDensity.current
        val count = size * 10000
        //val pickerHeight = maxHeight
        val pickerHeightPx = density.run { maxHeight.toPx() }
        val pickerCenterLinePx = pickerHeightPx / 2
        val itemHeight = maxHeight / visibleCount
        val itemHeightPx = density.run { itemHeight.toPx() }
        val startIndex = count / 2
       // Log.d(AppConstants.TAG, "itemHeightPx=$itemHeightPx, pickerHeightPx=$pickerHeightPx")
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = startIndex - startIndex.floorMod(size) + (selectIndex?:0),
            initialFirstVisibleItemScrollOffset = ((itemHeightPx - pickerHeightPx) / 2).roundToInt(),
        )
        val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }
        LazyColumn(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState)
        ) {
            items(count) { index ->
                val currIndex = (index - startIndex).floorMod(size)
                val item = layoutInfo.visibleItemsInfo.find { it.index == index }
                var percent = 1f
                if (item != null) {
                    val itemCenterY = item.offset + item.size / 2
                    percent = if (itemCenterY < pickerCenterLinePx) {
                        itemCenterY / pickerCenterLinePx
                    } else {
                        1 - (itemCenterY - pickerCenterLinePx) / pickerCenterLinePx
                    }

                    //非滑动状态，且item正好跨越wheel picker的中间线状态
                    if (!listState.isScrollInProgress
                        && item.offset < pickerCenterLinePx
                        && item.offset + item.size > pickerCenterLinePx
                    ) {
                        onSelect(currIndex, data[currIndex])
                    }
                }
                if(useTransition){
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = 0.75f + 0.25f * percent
                                scaleX = 0.75f + 0.25f * percent
                                scaleY = 0.75f + 0.25f * percent
                                rotationX = (1 + (0.75f + 0.25f * percent)) * 180
                            }
                            .fillMaxWidth()
                            .height(itemHeight),
                        contentAlignment = Alignment.Center,
                    ) {
                        content(data[currIndex])
                    }
                }else
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight),
                        contentAlignment = Alignment.Center,
                    ){
                        content(data[currIndex])
                    }

            }
        }
    }
}

private fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}

