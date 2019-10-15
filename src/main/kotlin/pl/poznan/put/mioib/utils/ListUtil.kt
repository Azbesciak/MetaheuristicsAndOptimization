package pl.poznan.put.mioib.utils

import java.util.*

fun shuffle (list: List<Any>): List<Any> {
    val random = Random()
    for (i in list.size downTo 0){
        Collections.swap(list, i, random.nextInt(i + 1))
    }

    return list
}