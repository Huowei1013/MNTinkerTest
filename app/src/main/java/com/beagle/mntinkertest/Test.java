package com.beagle.mntinkertest;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * @Author: huowei
 * @CreateDate: 2021/8/3 16:24
 * @Describe:
 * @Version: 1.1.2
 */
public class Test {

    public void testFix(Context context) {
        int x = 0;
        int y = 1;
        Toast.makeText(context, y/x+"", Toast.LENGTH_SHORT).show();
    }


}
