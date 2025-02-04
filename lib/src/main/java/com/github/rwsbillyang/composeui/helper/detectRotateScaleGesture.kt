/*
 * Copyright © 2023 rwsbillyang@qq.com.  All Rights Reserved.
 *
 * Written by rwsbillyang@qq.com at Beijing Time: 2023-12-07 16:00
 *
 * NOTICE:
 * This software is protected by China and U.S. Copyright Law and International Treaties.
 * Unauthorized use, duplication, reverse engineering, any form of redistribution,
 * or use in part or in whole other than by prior, express, printed and signed license
 * for use is subject to civil and criminal prosecution. If you have received this file in error,
 * please notify copyright holder and destroy this and any other copies as instructed.
 */

package com.github.rwsbillyang.composeui.helper

import android.util.Log
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope

import kotlin.math.asin
import kotlin.math.sqrt


/**
 * 监测单指旋转，双指缩放手势
 * */
suspend fun PointerInputScope.detectRotateScaleGesture(onRotate: (Float)-> Unit, onScale: (Float)-> Unit, enableLog: Boolean = false){
    // 循环监听每一组事件序列
    awaitEachGesture {
        awaitFirstDown()
        do {
            val event = awaitPointerEvent()
            when (event.changes.size) {
                1 -> {
                    val it = event.changes[0]
                    val rotate = calculateRotation(
                        Offset(size.width / 2f, size.height / 2f),
                        it.previousPosition,
                        it.position
                    )
                    //angle += rotate
                    onRotate(rotate)
                    //if(enableLog) Log.d(TAG,"rotateScaleGesture:rotate=$rotate, previousPosition=${it.previousPosition}  position=${it.position}, size=$size")
                }
                2 -> {
                    val zoom = event.calculateZoom()
                    //scale *= zoom
                    onScale(zoom)
                    if(enableLog){
                        val str = event.changes.joinToString(" | ") { "(${it.position.x}, ${it.position.y})" }
                        Log.d("ComposeUI","rotateScaleGesture: zoom=$zoom. type=${event.type}, $str")
                    }
                }
                else -> {
                    //Log.w(TAG, "changes size=$size, ignore")
                }
            }
        } while (event.changes.any { it.pressed })

        //            val downEvent = awaitFirstDown()//等待第一根手指按下事件时恢复执行，并将手指按下事件返回
        //drag 需要主动传入一个 PointerId 用以表示要具体获取到哪根手指的拖动事件
        //            drag(downEvent.id) {
        //                val rotate = calculateRotation(Offset(size.width/2f, size.height/2f), it.previousPosition, it.position)
        //                angle += rotate
        //                Log.d("RotationCompass", "after drag: rotate=$rotate, previousPosition=${it.previousPosition}  position=${it.position}, size=$size")
        //            }
    }
}


/**
 * 以center为圆心，根据初始和结束位置，计算旋转角度
 * */
fun calculateRotation(center: Offset, previousPosition: Offset, position: Offset): Float{
    val start = previousPosition - center
    val end = position - center
    // x1 = x0 + r*sin(θ)  y1 = y0 - r* cos(θ)
    //sin(A-B) = sinAcosB-cosAsinB
    val endR = sqrt(end.x * end.x + end.y * end.y)
    val startR = sqrt(start.x * start.x + start.y * start.y)

    val sinEnd = end.x / endR
    val cosEnd = -end.y / endR

    val sinStart = start.x / startR
    val cosStart = -start.y / startR

    val sinTheta = sinEnd * cosStart - cosEnd * sinStart

    return asin(sinTheta).toDegree()
}
/**
 * rad to degree
 * */
fun Float.toDegree() = Math.toDegrees(this.toDouble()).toFloat() //180.0 * this / Math.PI).toFloat()
