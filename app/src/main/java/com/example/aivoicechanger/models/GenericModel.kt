package com.example.aivoicechanger.models

class GenericModel(
    val img: Int,
    val title: String,
    var select: Boolean = false,
    val audioEffect: Pair<Float, Float>
)