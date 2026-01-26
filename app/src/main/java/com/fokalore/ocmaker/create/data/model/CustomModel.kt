package com.fokalore.ocmaker.create.data.model

data class CustomModel(
    var avt: String,
    var bodyPart: ArrayList<BodyPartModel>,
    var checkDataOnline : Boolean = false
)

data class BodyPartModel(
    var icon: String,
    var listPath: ArrayList<ColorModel>
)

data class ColorModel(
    var color: String,
    var listPath: ArrayList<String>
)