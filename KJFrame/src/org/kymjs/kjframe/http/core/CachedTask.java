/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
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
package org.kymjs.kjframe.http.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.kymjs.kjframe.utils.CipherUtils;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.StringUtils;

/**
 * 本类主要用于获取网络数据，并将结果缓存至文件，文件名为key，缓存有效时间为value <br>
 * <b>注：</b> 参数Result需要序列化，否则不能或者不能完整的读取缓存。<br>
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public abstract class CachedTask<Params, Progress, Result extends Serializable>
        extends SafeTask<Params, Progress, Result> {
    private static String cachePath = "folderName"; // 缓存路径
    private String cacheName = "MD5_effectiveTime"; // 缓存文件名格式
    private long expiredTime = 0; // 缓存有效时间
    private String key; // 缓存以键值对形式存在
    private static ConcurrentHashMap<String, Long> cacheMap = new ConcurrentHashMap<String, Long>(); // (k:缓存md5(url),v:缓存时的时间)

    protected boolean resFromCache = false; // 如果本次请求的结果是从缓存读取，本变量会被赋值为true

    private long delayCacheTime = 0;

    /**
     * 读取缓存以后sleep的时间
     */
    public void setDelayCacheTime(long delayCacheTime) {
        this.delayCacheTime = delayCacheTime;
    }

    /**
     * 读取缓存以后sleep的时间
     * 
     * @param delayCacheTime
     */
    public long getDelayCacheTime() {
        return delayCacheTime;
    }

    /**
     * 构造方法
     * 
     * @param cachePath
     *            缓存路径
     * @param key
     *            存储的key值，若重复将覆盖
     * @param cacheTime
     *            缓存有效期，单位：分
     */
    public CachedTask(String cachePath, String key, long cacheTime) {
        if (StringUtils.isEmpty(cachePath) || StringUtils.isEmpty(key)) {
            throw new RuntimeException("cachePath or key is empty");
        } else if (cacheTime == 0) {
            CachedTask.cachePath = cachePath;
            this.expiredTime = 0;
            this.cacheName = this.key + "_" + expiredTime;
        } else {
            CachedTask.cachePath = cachePath;
            // 对外url，对内url的md5值（不仅可以防止由于url过长造成文件名错误，还能防止恶意修改缓存内容）
            this.key = CipherUtils.md5(key);
            // 对外单位：分，对内单位：毫秒
            this.expiredTime = TimeUnit.MILLISECONDS.convert(cacheTime,
                    TimeUnit.MINUTES);
            this.cacheName = this.key + "_" + expiredTime;
            initCacheMap();
        }
    }

    private void initCacheMap() {
        File folder = FileUtils.getSaveFolder(cachePath);
        // 如果文件的数量与cache大小的相同则认为是已经初始化。虽然会有可能有误差,但是在很大程度上降低了文件遍历,提高了效率
        if (folder.listFiles().length != cacheMap.size()) {
            for (File file : folder.listFiles()) {
                String name = file.getName();
                if (!StringUtils.isEmpty(name)) {
                    String[] nameFormat = name.split("_");
                    // 若满足命名格式则认为是一个合格的cache
                    if (nameFormat.length == 2
                            && (nameFormat[0].length() == 32
                                    || nameFormat[0].length() == 64 || nameFormat[0]
                                    .length() == 128)) {
                        cacheMap.put(nameFormat[0], file.lastModified());
                    }
                }
            }
        }
    }

    @Override
    protected void onPreExecuteSafely() throws Exception {
        resFromCache = false;
        super.onPreExecuteSafely();
    }

    /**
     * 做联网操作,本方法运行在线程中
     */
    protected abstract Result doConnectNetwork(Params... params)
            throws Exception;

    /**
     * 做耗时操作
     */
    @Override
    protected final Result doInBackgroundSafely(Params... params)
            throws Exception {
        Result res = null;
        if (expiredTime != 0) { // 为0就不读写cache，提升效率
            Long temp = cacheMap.get(key);
            long saveTime = (temp == null) ? 0 : temp; // 获取缓存时的时间
            long currentTime = System.currentTimeMillis(); // 获取当前时间

            if (currentTime >= saveTime + expiredTime) { // 若缓存无效，联网下载
                res = doConnectNetwork(params);
                if (res != null)
                    saveCache(res);
            } else { // 缓存有效，使用缓存
                res = getResultFromCache(cacheName);
                if (res == null) { // 若缓存数据意外丢失，重新下载
                    res = doConnectNetwork(params);
                    saveCache(res);
                } else {
                    resFromCache = true;
                    Thread.sleep(delayCacheTime);
                }
            }
        } else {
            res = doConnectNetwork(params);
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private Result getResultFromCache(String cacheName) {
        Result res = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(
                    FileUtils.getSaveFile(cachePath, cacheName)));
            res = (Result) ois.readObject();
        } catch (Exception e) {
        } finally {
            FileUtils.closeIO(ois);
        }
        return res;
    }

    /**
     * 保存数据，并返回是否成功
     */
    private boolean saveResultToCache(Result res) {
        boolean saveSuccess = false;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(
                    FileUtils.getSaveFile(cachePath, cacheName)));
            oos.writeObject(res);
            saveSuccess = true;
        } catch (Exception e) {
        } finally {
            FileUtils.closeIO(oos);
        }
        return saveSuccess;
    }

    /**
     * 清空缓存文件（异步）
     */
    public static void cleanCacheFiles() {
        cleanCacheFiles(cachePath);
    }

    /**
     * 清空缓存文件（异步）
     */
    public static void cleanCacheFiles(String cachePath) {
        cacheMap.clear();
        File file = FileUtils.getSaveFolder(cachePath);
        final File[] fileList = file.listFiles();
        if (fileList != null) {
            // 异步删除全部文件
            TaskExecutor.start(new Runnable() {
                @Override
                public void run() {
                    for (File f : fileList) {
                        if (f.isFile()) {
                            f.delete();
                        }
                    }
                }// end run()
            });
        }// end if
    }

    /**
     * 移除一个缓存
     * 
     * @param key
     */
    public static void remove(String key) {
        // 对内是url的MD5
        String realKey = CipherUtils.md5(key);
        for (Map.Entry<String, Long> entry : cacheMap.entrySet()) {
            if (entry.getKey().equals(realKey)) {
                cacheMap.remove(realKey);
                break;
            }
        }
        // 删除文件缓存
        File file = FileUtils.getSaveFolder(cachePath);
        final File[] fileList = file.listFiles();
        for (File cacheFile : fileList) {
            String cacheFilekey = cacheFile.getName().split("_")[0];
            if (cacheFilekey.equalsIgnoreCase(realKey)) {
                cacheFile.delete();
                break;
            }
        }
    }

    /**
     * 获取url对应的缓存数据(不论是否过期)，若不存在，则返回null
     * 
     * @param url
     *            链接地址
     */
    public static String getCache(String cachePath, String url) {
        // 对内是url的MD5
        String realKey = CipherUtils.md5(url);
        String res = null;

        // 删除文件缓存
        File file = FileUtils.getSaveFolder(cachePath);
        final File[] fileList = file.listFiles();
        for (File cacheFile : fileList) {
            String cacheFilekey = cacheFile.getName().split("_")[0];
            if (cacheFilekey.equalsIgnoreCase(realKey)) {
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(new FileInputStream(cacheFile));
                    res = ois.readObject().toString();
                } catch (Exception e) {
                } finally {
                    FileUtils.closeIO(ois);
                }
                break;
            }
        }
        return res;
    }

    /**
     * 如果缓存是有效的，就保存
     * 
     * @param res
     *            将要缓存的数据
     */
    private void saveCache(Result res) {
        if (res != null) {
            saveResultToCache(res);
            cacheMap.put(cacheName, System.currentTimeMillis());
        }
    }
}
