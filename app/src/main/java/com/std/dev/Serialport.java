package com.std.dev;

import androidx.annotation.NonNull;

import com.tpms.utils.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Serialport {
    private static final String TAG = "Serialport";
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public static native void close(FileDescriptor fileDescriptor);

    public static native FileDescriptor open(String str, int i, int i2);

    public Serialport(@NonNull File file, int i, int i2) throws SecurityException, IOException {
        if (!file.canRead() || !file.canWrite()) {
            try {
                Process exec = Runtime.getRuntime().exec("/system/bin/su");
                exec.getOutputStream().write(("chmod 666 " + file.getAbsolutePath() + "\nexit\n").getBytes());
                if (exec.waitFor() != 0 || !file.canRead() || !file.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }
        FileDescriptor open = open(file.getAbsolutePath(), i, i2);
        this.mFd = open;
        if (open != null) {
            this.mFileInputStream = new FileInputStream(this.mFd);
            this.mFileOutputStream = new FileOutputStream(this.mFd);
            return;
        }
        Log.e(TAG, "native open returns null");
        throw new IOException();
    }

    public int read(byte[] bArr) throws IOException {
        return this.mFileInputStream.read(bArr);
    }

    public void write(byte[] bArr, int i) throws IOException {
        this.mFileOutputStream.write(bArr, 0, i);
    }

    public InputStream getInputStream() {
        return this.mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return this.mFileOutputStream;
    }

    public void close() {
        try {
            this.mFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mFileOutputStream = null;
        try {
            this.mFileInputStream.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        this.mFileInputStream = null;
        OsWrap.close(this.mFd);
        this.mFd = null;
    }

    static {
        System.loadLibrary("stdSerialport");
    }
}
