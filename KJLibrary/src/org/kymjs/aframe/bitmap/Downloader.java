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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.kymjs.aframe.utils.FileUtils;
import org.kymjs.aframe.utils.StringUtils;

/**
 * 图片下载器：可以从网络或本地加载一张Bitmap并返回
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class Downloader implements I_ImageLoder {

    /**
     * 图片加载器协议的接口方法
     */
    @Override
    public byte[] loadImage(String imagePath) {
        byte[] img = null;
        if (!StringUtils.isEmpty(imagePath)) {
            if (imagePath.trim().toLowerCase().startsWith("http")) {
                img = loadImgFromNet(imagePath);
            } else {
                img = loadImgFromFile(imagePath);
            }

        }
        return img;
    }

    /**
     * 从网络载入一张图片
     * 
     * @param imagePath
     *            图片的地址
     */
    private byte[] loadImgFromNet(String imagePath) {
        byte[] data = null;
        HttpURLConnection con = null;
        try {
            URL url = new URL(imagePath);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(KJBitmap.config.timeOut);
            con.setReadTimeout(KJBitmap.config.timeOut * 2);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            data = FileUtils.input2byte(con.getInputStream());
        } catch (Exception e) {
            if (KJBitmap.config.callBack != null) {
                KJBitmap.config.callBack.imgLoadFailure(imagePath,
                        e.getMessage());
            }
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return data;
    }

    /**
     * 从本地载入一张图片
     * 
     * @param imagePath
     *            图片的地址
     */
    private byte[] loadImgFromFile(String imagePath) {
        byte[] data = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagePath);
            if (fis != null)
                data = FileUtils.input2byte(fis);
        } catch (FileNotFoundException e) {
            if (KJBitmap.config.callBack != null) {
                KJBitmap.config.callBack.imgLoadFailure(imagePath,
                        e.getMessage());
            }
            e.printStackTrace();
        } finally {
            FileUtils.closeIO(fis);
        }
        return data;
    }
}
