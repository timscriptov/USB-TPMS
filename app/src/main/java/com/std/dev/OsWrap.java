package com.std.dev;

import java.io.FileDescriptor;

public class OsWrap extends BaseWrap {
    private static final String mPackName = "android.system.Os";

    public static void close(FileDescriptor fileDescriptor) {
        try {
            runRelMethod(mPackName, null, new Class[]{FileDescriptor.class}, fileDescriptor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
