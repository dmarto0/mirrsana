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
package org.kymjs.aframe.ui;

/**
 * UILibrary中的回调方法
 * 
 * @author kymjs(kymjs123@gmail.com)
 * 
 */
public abstract class UICallBack {
    public void onCallBack() {};

    public void onCallBack(int i) {};

    public void onCallBack(String str) {};

    public void onCallBack(int i, String str) {};
}
