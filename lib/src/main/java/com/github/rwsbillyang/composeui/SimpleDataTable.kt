package com.github.rwsbillyang.composeui



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 表格的列配置， 可进一步参考antd中的Column配置
 * @param header 表头文字
 * @param row2CellValue 从行记录值中提取一个字段
 * @param weight 列宽weight
 * @param hide 是否隐藏
 * @param cellRender 自定义cellRender
 * */
class ProColumn<T>(
    val header: String,
    val row2CellValue: ((entity: T) -> String?)? = null,
    val weight: Int = 1,
    val alignment: Alignment = Alignment.Center,
    val hide: Boolean = false,
    val headerRender: (@Composable (text: String, columnIndex: Int) -> Unit)? = null,
    val cellRender: (@Composable (row: T, rowIndex: Int, columnIndex: Int) -> Unit)? = null
)


/**
 * @param columns 列配置
 * @param rows 行记录列表
 * @param width 0f为fillMaxWidth, 大于0表示指定的宽度，小于0表示当前父组件宽度的倍数，-1f表示不缩放，-1.5f表示1.5倍的当前父组件宽，-2f表示2倍的父组件宽
 * @param verticalLazyListState
 * @param cellRender 单元格绘制函数，优先级低于ProColumn中的cellRender，都不指定则使用SimpleCell
 * 其中row为表格的行数据，rowIndex为数据的第一行数据（不包括header这一行），索引为0；columnIndex为列索引，0开始
 *
 * 注意：下面的用法错误，SimpleDataTable里使用了LazyColumn，外层不可直接使用verticalScroll
 * Column(Modifier.verticalScroll(rememberScrollState())) {
 *     SimpleDataTable(...)
 * }
 * */
@Composable
fun <T> SimpleDataTable(
    columns: List<ProColumn<T>>,
    rows: List<T>,
    width: Float = 0f,
    padding: Int = 3,
    rowHorizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    rowVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    verticalLazyListState: LazyListState = rememberLazyListState(),
    cellRender: @Composable ((row: T, rowIndex: Int, columnIndex: Int) -> Unit)? = null
) {
    val columnNotHide = columns.filter { !it.hide }

    //val configuration = LocalConfiguration.current
    BoxWithConstraints(modifier = Modifier.fillMaxWidth(), propagateMinConstraints = true) {
        val currentAvailableWidth = maxWidth.value.toInt() // LocalConfiguration.current.screenWidthDp

        val cellHorizontalPadding = 3
        var tableWidth: Int
        val modifier = if(width == 0f) {
            tableWidth = currentAvailableWidth  - 2 * padding - cellHorizontalPadding * 2 * columnNotHide.size//.dp
            Modifier.fillMaxWidth()
        }else{
            tableWidth = if(width < 0) {
                (currentAvailableWidth * (-width)).toInt() - 2 * padding - cellHorizontalPadding * 2 * columnNotHide.size//.dp
            }else {
                width.toInt() - (2 * padding) - cellHorizontalPadding * 2 * columnNotHide.size
            }
            Modifier.width(tableWidth.dp)
        }

        //val weights = remember { mutableStateOf(calcWeights(headers, rows.map{entity2Cells(it)})) }
        val weightSum = columnNotHide.sumOf { it.weight }
        val weights = remember { mutableStateOf(columnNotHide.map { it.weight * tableWidth / weightSum }) }

        val textColor = MaterialTheme.colorScheme.primary
        val cellBoxModifier = Modifier //.background(if(rowIndex % 2 == 0) Color.Red else Color.Blue)
            .fillMaxWidth()
            //.border(0.dp, textColor.copy(alpha = 0.3f))
            .padding(horizontal = cellHorizontalPadding.dp, vertical = 2.dp)

        Box(modifier.padding(padding.dp).border(0.dp, textColor.copy(alpha = 0.5f)))
        {
            Column(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()))
            {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .border(0.dp, textColor.copy(alpha = 0.3f))
                    .background(Color.Gray), rowHorizontalArrangement, rowVerticalAlignment) {
                    columnNotHide.forEachIndexed { columnIndex, c ->
                        val w = weights.value[columnIndex]
                        Box(cellBoxModifier.width(w.dp), c.alignment){
                            if(c.headerRender != null) c.headerRender.invoke(c.header, columnIndex)
                            else SimpleCell(text = c.header, 0, columnIndex)
                        }
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxWidth(), state = verticalLazyListState) {
                    itemsIndexed(rows) { rowIndex, row ->
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .border(0.dp, textColor.copy(alpha = 0.3f)), rowHorizontalArrangement, rowVerticalAlignment) {
                            columnNotHide.forEachIndexed { columnIndex, column ->
                                val render = column.cellRender?:cellRender
                                val w = weights.value[columnIndex]
                                Box(cellBoxModifier.width(w.dp), column.alignment){
                                    if(render != null) render(row, rowIndex, columnIndex)
                                    else {
                                        SimpleCell(column.row2CellValue?.invoke(row)?:"",rowIndex+1,columnIndex)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleCell(text: String, rowIndex: Int, columnIndex: Int){
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 5,
        softWrap = true,
        overflow = TextOverflow.Ellipsis
    )
}
//https://stackoverflow.com/questions/68143308/how-do-i-create-a-table-in-jetpack-compose/71665355#71665355
private fun calcWeights(columns: List<String>, rows: List<List<String>>): List<Float> {
    val weights = MutableList(columns.size) { 0 }
    val fullList = rows.toMutableList()
    fullList.add(columns)
    fullList.forEach { list ->
        list.forEachIndexed { columnIndex, value ->
            weights[columnIndex] = weights[columnIndex].coerceAtLeast(value.length)
        }
    }
    return weights.map { it.toFloat() }
}

