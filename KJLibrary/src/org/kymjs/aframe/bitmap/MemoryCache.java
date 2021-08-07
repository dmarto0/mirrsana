/*
 * Copyright (c) 2012-2013, kymjs 张涛 (kymjs123@gmail.com).
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

import org.kymjs.aframe.utils.SystemTool;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

/**
 * 使用lru算法的内存缓存池
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class MemoryCache {

    private LruCache<String, Bitmap> cache;

    @SuppressLint("NewApi")
    public MemoryCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        cache = new LruCache<String, Bitmap>(maxMemory / 8) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                super.sizeOf(key, value);
                if (SystemTool.getSDKVersion() >= 12) {
                    return value.getByteCount() / 1024;
                } else {
                    return value.getRowBytes() * value.getHeight();
                }
            }
        };
    }

    @SuppressLint("NewApi")
    public MemoryCache(int maxSize) {
        cache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                super.sizeOf(key, value);
                if (SystemTool.getSDKVersion() >= 12) {
                    return value.getByteCount() / 1024;
                } else {
                    return value.getRowBytes() * value.getHeight();
                }
            }
        };
    }

    public void put(String key, Bitmap bitmap) {
        if (this.get(key) == null) {
            cache.put(key, bitmap);
        }
    }

    public Bitmap get(String key) {
        return cache.get(key);
    }
}
