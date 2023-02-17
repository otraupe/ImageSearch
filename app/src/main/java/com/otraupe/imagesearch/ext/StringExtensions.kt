package com.otraupe.imagesearch.ext

fun String.spaceToPlus(): String {
    return this.replace(' ', '+')
}