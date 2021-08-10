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

import java.util.HashSet;
import java.util.Set;

import org.kymjs.aframe.bitmap.utils.BitmapCreate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

/**
 * The BitmapLibrary's core classes
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class KJBitmap {
    /**
     * 图片加载的配置器：以static修饰的配置器，保证一次设置可一直使用
     */
    public static KJBitmapConfig config;
    /** 记录所有正在下载或等待下载的任务 */
    private Set<BitmapWorkerTask> taskCollection;
    private MemoryCache mMemoryCache;

    public KJBitmap(Context context) {
        if (config == null) {
            config = new KJBitmapConfig();
        }
        mMemoryCache = new MemoryCache(config.memoryCacheSize);
        taskCollection = new HashSet<BitmapWorkerTask>();
    }

    /**
     * 加载网络图片
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     */
    public void display(View imageView, String imageUrl) {
        if (config.openProgress) {
            loadImageWithProgress(imageView, imageUrl);
        } else {
            loadImage(imageView, imageUrl);
        }
    }

    /**
     * 加载网络图片
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     * @param imgW
     *            图片显示宽度。若大于图片本身大小，则只显示图片大小
     * @param imgH
     *            图片显示高度。若大于图片本身大小，则只显示图片大小
     */
    public void display(View imageView, String imageUrl, int imgW, int imgH) {
        config.width = imgW;
        config.height = imgH;
        display(imageView, imageUrl);
    }

    /**
     * 加载网络图片
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     * @param loadingBitmap
     *            图片载入过程中显示的图片
     */
    public void display(View imageView, String imageUrl, Bitmap loadingBitmap) {
        config.loadingBitmap = loadingBitmap;
        display(imageView, imageUrl);
    }

    /**
     * 显示加载中的环形等待条
     */
    private void loadImageWithProgress(View imageView, String imageUrl) {
        loadImage(imageView, imageUrl);
    }

    /**
     * 加载图片（核心方法）
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     */
    private void loadImage(View imageView, String imageUrl) {
        if (config.callBack != null)
            config.callBack.imgLoading(imageView);
        final Bitmap bitmap = mMemoryCache.get(imageUrl);
        if (bitmap != null) {
            if (imageView instanceof ImageView) {
                ((ImageView) imageView).setImageBitmap(bitmap);
            } else {
                imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
            if (config.callBack != null)
                config.callBack.imgLoadSuccess(imageView);
        } else {
            if (imageView instanceof ImageView) {
                ((ImageView) imageView).setImageBitmap(config.loadingBitmap);
            } else {
                imageView.setBackgroundDrawable(new BitmapDrawable(
                        config.loadingBitmap));
            }
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            taskCollection.add(task);
            task.execute(imageUrl);
        }
    }

    /********************* 异步获取Bitmap并设置image的任务类 *********************/
    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private View imageView;

        public BitmapWorkerTask(View imageview) {
            this.imageView = imageview;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            // 从指定链接调取image
            byte[] res = config.imgLoader.loadImage(params[0]);
            if (res != null) {
                bitmap = BitmapCreate.bitmapFromByteArray(res, 0, res.length,
                        config.width, config.height);
            }
            if (bitmap != null) {
                mMemoryCache.put(params[0], bitmap); // 图片载入完成后缓存到LrcCache中
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageView instanceof ImageView) {
                if (bitmap != null) {
                    ((ImageView) imageView).setImageBitmap(bitmap);
                }
            } else {
                imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
            if (config.callBack != null)
                config.callBack.imgLoadSuccess(imageView);
            taskCollection.remove(this);
        }
    }
}
