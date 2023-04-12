package com.yg.pdf.bean

data class RemoteConfig(
    var inside: Inside = Inside()
) {
    data class Inside(
        var isGP: Int = 1,
        var maxShowAdCount: Int = 5,
        var showAd: Int = 1,
        var spacedPage: Int = 2,
        var isShowBackAd: Int = 2
    )
}