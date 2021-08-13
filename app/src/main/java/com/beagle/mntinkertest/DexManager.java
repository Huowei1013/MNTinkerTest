package com.beagle.mntinkertest;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * @Author: huowei
 * @CreateDate: 2021/8/3 16:21
 * @Describe: manager加载dex文件
 * @Version: 1.1.2
 */
public class DexManager {
    //获取所有补丁
    private static HashSet<File> loadedDex = new HashSet<>();
    public static void loadDex(Context context) {
        if (context == null){
            return;
        }
        //获取补丁的存储位置的文件夹
        File odex = context.getDir("odex", Context.MODE_PRIVATE);
        //获取所有补丁
        File[] files = odex.listFiles();
        for (File file : files) {
            if (file.getName().startsWith("classes")||file.getName().endsWith(".dex")){
                loadedDex.add(file);
            }
        }
        //创建一个dex文件解压目录
        String optimizeDir = odex.getAbsolutePath() + File.separator + "opt_dex";
        File fpot = new File(optimizeDir);
        if (!fpot.exists()){
            fpot.mkdir();
        }
        //dex合并之前的dex
        doDexInject(context,optimizeDir);

    }

    /**
     * dex合并之前的dex
     * @param context
     * @param optimizeDir
     */
    private static void doDexInject(Context context, String optimizeDir) {

        //合并当前应用的dexElements
        for (File dex : loadedDex) {
            try {
            // 通过反射获取dexElements
            //1.获取类加载器
            PathClassLoader classLoader = (PathClassLoader) context.getClassLoader();
            Class<?> superclass = classLoader.getClass().getSuperclass();
            //获取pathList变量
                Field pathList = superclass.getDeclaredField("pathList");
                pathList.setAccessible(true);
                //获取pathList类加载器中的值
                Object pathListValue = pathList.get(classLoader);
                //获取dexElements
                Field dexElement = pathListValue.getClass().getDeclaredField("dexElements");
                dexElement.setAccessible(true);
                //获取到dexElements在当前应用的值
                Object dexElementValue = dexElement.get(pathListValue);

                //加载补丁APK Library
                DexClassLoader dexClassLoader = new DexClassLoader(dex.getAbsolutePath(),optimizeDir,null,context.getClassLoader());
                //获取dexClassLoader里面的pathList
                Object myPathList = pathList.get(dexClassLoader);
                Object myDexElements = dexElement.get(myPathList);

                //合并新的数据
                int length = Array.getLength(dexElementValue);
                int myLength = Array.getLength(myDexElements);
                int newLength = length +myLength;
                //获取到数组的类型
                Class<?> componentType = dexElementValue.getClass().getComponentType();
                //创建新数组
                Object newElementArray = Array.newInstance(componentType, newLength);
                for (int i = 0; i < newLength; i++) {
                    //注意 要将补丁的数据放到数组前面
                    if (i<myLength){
                        Array.set(newElementArray,i,Array.get(myDexElements,i));
                    }else {
                        Array.set(newElementArray,i,Array.get(dexElementValue,i-myLength));
                    }
                }
                //将合并玩的数组赋值给应用的dexElement
                dexElement.set(pathListValue,newElementArray);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
