/*
 * Copyright (c) 2014-2015, kymjs 张涛 (kymjs123@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.aframe.bitmap;

import android.graphics.Bitmap;

/**
 * bitmapLibrary的配置器
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class KJBitmapConfig {
    /** 网络连接等待时间 */
    public int timeOut = 5000;
    /** 内存缓存大小 */
    public int memoryCacheSize;

    /** 图片的宽度 */
    public int width = 1000; // 不足1000则显示图片默认大小
    /** 图片的高度 */
    public int height = 1000; // 不足1000则显示图片默认大小
    /** 载入时的图片 */
    public Bitmap loadingBitmap;
    /** 是否开启载入图片时显示环形progressBar效果 */
    public boolean openProgress = false;

    /** 图片载入状态将会回调相应的方法 */
    public BitmapCallBack callBack;

    /** 图片加载器,若认为KJLibrary的加载器不好，也可自定义图片加载器 */
    public I_ImageLoder imgLoader;

    /** 是否开启本地图片缓存功能 */
    public boolean openLocalCache = true;
    /** 本地图片缓存路径 */
    public String cachePath = "/KJLibrary/";

    public KJBitmapConfig() {
        memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
        imgLoader = new Downloader(); // 配置图片加载器
    }
}
