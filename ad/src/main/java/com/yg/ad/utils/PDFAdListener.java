package com.yg.ad.utils;


public interface PDFAdListener {

    default void adLoaded(String adNetworkId) {//广告预加载成功

    }

    default void adShow(Double ecpm, String adNetworkId) {//广告展示成功
    }

    default void adComplete() {//激励视频播放完成
    }

    default void adClose() {//广告关闭
    }

    default void adError(PDFAdError adError) {//广告加载或者播放失败
    }

    default void adError(PDFAdError adError, boolean isLoadFailed) {//广告加载或者播放失败
    }

    default void adClick() {//广告被点击

    }
}
