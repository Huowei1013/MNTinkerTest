package com.beagle.mntinkertest;

import android.app.Application;
import android.content.Context;

/**
 * @Author: huowei
 * @CreateDate: 2021/8/4 11:39
 * @Describe:
 * @Version: 1.1.2
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    protected void attachBaseContext(Context base) {
        // TODO Auto-generated method stub
        //修复默认路径下的dex文件（待修复的文件都需要移入该目录中）  data/data/包名/files/odex
//        DexFixUtils.loadFixedDex(base);
        DexManager.loadDex(base);
        //直接在拿sd卡中的dex文件进行修复，但是应用在每次重启后（杀进程）都需要重写修复一次，所以最好是将修复的dex文件放在包名下的文件目录中
//    DexFixUtils.loadFixedDex(base,Environment.getExternalStorageDirectory());
        super.attachBaseContext(base);

    }
}