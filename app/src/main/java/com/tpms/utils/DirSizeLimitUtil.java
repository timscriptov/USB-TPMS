package com.tpms.utils;

import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DirSizeLimitUtil {
    static String TAG = "DirSizeLimitUtil";
    private double limitSize = -2.147483648E9d;
    private String mDir = "";

    public DirSizeLimitUtil(String str, double d) {
        this.mDir = str;
        String str2 = TAG;
        Log.w(str2, "Set limitSize:" + d);
        this.limitSize = d;
    }

    public static double calcTotalSize(String str) {
        double d;
        try {
            d = getFileSize(new File(str));
        } catch (Exception unused) {
            d = 0.0d;
        }
        String str2 = TAG;
        Log.i(str2, "mDvrSize files " + d);
        return d;
    }

    public static double getFileSize(File file) throws Exception {
        double d;
        File[] listFiles = file.listFiles();
        double d2 = 0.0d;
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                d = getFileSize(listFiles[i]);
            } else {
                d = (double) (((float) listFiles[i].length()) * 1.0f);
                Double.isNaN(d);
            }
            d2 += d;
        }
        return d2;
    }

    private double calcDvrSize() {
        double d = 0.0d;
        try {
            for (File file : new File(this.mDir).listFiles()) {
                double length = (double) (((float) file.length()) * 1.0f);
                Double.isNaN(length);
                d += length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "mDvrSize files " + d);
        return d;
    }

    public void sizeProc() {
        double d = this.limitSize;
        String str = TAG;
        Log.i(str, "limitSizeOK " + d);
        if (calcDvrSize() > d) {
            String str2 = TAG;
            Log.i(str2, "overy limitSize " + d);
            deleteOldFile();
            sizeProc();
        }
    }

    private void deleteOldFile() {
        File[] listFiles = new File(this.mDir).listFiles();
        if (listFiles != null && listFiles.length > 0 && listFiles[0] != null) {
            List asList = Arrays.asList(listFiles);
            Collections.sort(asList, new Comparator<File>() {
                public int compare(File file, File file2) {
                    if (file.lastModified() < file2.lastModified()) {
                        return -1;
                    }
                    return file.lastModified() > file2.lastModified() ? 1 : 0;
                }
            });
            ((File) asList.get(0)).delete();
        }
    }
}
