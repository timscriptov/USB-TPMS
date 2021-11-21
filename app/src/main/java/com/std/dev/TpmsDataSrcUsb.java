package com.std.dev;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.syt.tmps.TpmsApplication;
import com.syt.tmps.data.UmengConst;
import com.tpms.decode.PackBufferFrame;
import com.tpms.modle.DeviceOpenEvent;
import com.tpms.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;

public class TpmsDataSrcUsb extends TpmsDataSrc {
    private static final String ACTION_USB_PERMISSION = "com.android.cz.USB_PERMISSION";
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final String TAG = "TpmsDataSrcUsb";
    boolean mIsStart = false;
    Handler mMainHander = new Handler();
    private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {
        @Override
        public void onRunError(Exception exc) {
            Log.d(TpmsDataSrcUsb.this.TAG, "Runner stopped. 读取报错，可能是断开了");
            TpmsDataSrcUsb.this.mMainHander.post(new Runnable() {
                public void run() {
                    EventBus.getDefault().post(new DeviceOpenEvent(false));
                }
            });
        }

        public void onNewData(byte[] bArr) {
            Log.e(TpmsDataSrcUsb.this.TAG, "usb read onNewData" + HexDump.dumpHexString(bArr));
            int length = bArr.length;
            byte[] bArr2 = new byte[length];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            TpmsDataSrcUsb.this.BufferFrame.addBuffer(bArr2, length);
        }
    };
    PendingIntent mPermissionIntent;
    UsbSerialPort mPort;
    SerialInputOutputManager mSerialIoManager;
    private List<UsbSerialPort> mEntries = null;
    private UsbManager mUsbManager = null;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (TpmsDataSrcUsb.ACTION_USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra("device");
                    if (!intent.getBooleanExtra("permission", false)) {
                        Log.i("usb", "permission denied for device " + usbDevice);
                    } else if (usbDevice != null) {
                        Log.i("UsbR", "permission granted for device " + usbDevice);
                        Log.i("UsbR", "getInterfaceCount:" + usbDevice.getInterfaceCount());
                        TpmsDataSrcUsb.this.onStartUsbConnent();
                    } else {
                        Log.i("usb", "no permission " + usbDevice);
                    }
                }
            }
        }
    };

    public TpmsDataSrcUsb(TpmsApplication tpmsApplication) {
        super(tpmsApplication);
    }

    @Override
    public void init() {
        this.mUsbManager = (UsbManager) this.theapp.getSystemService(Context.USB_SERVICE);
        this.mEntries = new ArrayList<>();
        this.mPermissionIntent = PendingIntent.getBroadcast(this.theapp, 0, new Intent(ACTION_USB_PERMISSION), 0);
        this.theapp.registerReceiver(this.mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        EventBus.getDefault().register(this);
    }

    @Override
    public void setBufferFrame(PackBufferFrame packBufferFrame) {
        this.BufferFrame = packBufferFrame;
    }

    @Override
    public void writeData(byte[] bArr) {
        SerialInputOutputManager serialInputOutputManager = this.mSerialIoManager;
        if (serialInputOutputManager != null) {
            try {
                serialInputOutputManager.writeAsync(bArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(this.TAG, " usb writeAsync " + HexDump.dumpHexString(bArr));
            return;
        }
        Log.e(this.TAG, " usb writeAsync mSerialIoManager =null ");
    }

    @Override
    public void start() {
        Log.i(this.TAG, "start mIsStart:" + this.mIsStart);
        if (!this.mIsStart) {
            startReadThread();
            this.mIsStart = true;
        }
    }

    @Override
    public void stop() {
        Log.i(this.TAG, "stop mIsStart:" + this.mIsStart);
        if (this.mIsStart) {
            stopIoManager();
            UsbSerialPort usbSerialPort = this.mPort;
            if (usbSerialPort != null) {
                try {
                    usbSerialPort.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.mPort = null;
            }
            this.mIsStart = false;
        }
    }

    private void startReadThread() {
        if (this.mPort == null) {
            onStartUsbConnent();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void onStartUsbConnent() {
        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            public List<UsbSerialPort> doInBackground(Void... voidArr) {
                Log.d(TAG, "Refreshing device list 刷新设备列表 ...");
                TpmsDataSrcUsb.this.sleep(1000);
                List<UsbSerialDriver> findAllDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
                ArrayList<UsbSerialPort> arrayList = new ArrayList<>();
                for (UsbSerialDriver usbSerialDriver : findAllDrivers) {
                    List<UsbSerialPort> ports = usbSerialDriver.getPorts();
                    Object[] objArr = new Object[3];
                    objArr[0] = usbSerialDriver;
                    objArr[1] = Integer.valueOf(ports.size());
                    objArr[2] = ports.size() == 1 ? "" : UmengConst.ap;
                    Log.d(TAG, String.format("+ %s: %s port%s", objArr));
                    arrayList.addAll(ports);
                }
                return arrayList;
            }

            public void onPostExecute(List<UsbSerialPort> list) {
                mEntries.clear();
                mEntries.addAll(list);
                Log.d(TAG, "Done refreshing, " + mEntries.size() + " entries found.");
                if (mEntries.size() == 0) {
                    EventBus.getDefault().post(new DeviceOpenEvent(false));
                }
                for (int i = 0; i < mEntries.size(); i++) {
                    UsbSerialPort usbSerialPort = (UsbSerialPort) mEntries.get(i);
                    if (usbSerialPort != null) {
                        if ("1027_24577".equals(usbSerialPort.getDriver().getDevice().getVendorId() + "_" + usbSerialPort.getDriver().getDevice().getProductId())) {
                            openUsbPort(usbSerialPort);
                            Log.i(TAG, "onPostExecute 1027_24577");
                        } else {
                            if ("1027_24597".equals(usbSerialPort.getDriver().getDevice().getVendorId() + "_" + usbSerialPort.getDriver().getDevice().getProductId())) {
                                openUsbPort(usbSerialPort);
                                Log.i(TAG, "onPostExecute 1027_24597");
                            } else {
                                if ("6790_29987".equals(usbSerialPort.getDriver().getDevice().getVendorId() + "_" + usbSerialPort.getDriver().getDevice().getProductId())) {
                                    openUsbPort(usbSerialPort);
                                    Log.i(TAG, "onPostExecute 6790_29987");
                                }
                            }
                        }
                    }
                }
            }
        }.execute((Void) null);
    }

    private void startIoManager() {
        if (this.mPort != null) {
            Log.i(this.TAG, "Starting io manager ..");
            SerialInputOutputManager serialInputOutputManager = new SerialInputOutputManager(this.mPort, this.mListener);
            this.mSerialIoManager = serialInputOutputManager;
            this.mExecutor.submit(serialInputOutputManager);
        }
    }

    private void stopIoManager() {
        if (this.mSerialIoManager != null) {
            Log.i(this.TAG, "Stopping io manager ..");
            this.mSerialIoManager.stop();
            this.mSerialIoManager = null;
        }
    }

    private void openUsbPort(UsbSerialPort usbSerialPort) {
        this.mPort = usbSerialPort;
        if (usbSerialPort == null) {
            EventBus.getDefault().post(new DeviceOpenEvent(false));
            return;
        }
        Log.i(this.TAG, "interfacecount:" + this.mPort.getDriver().getDevice().getInterfaceCount());
        if ((this.mPort.getDriver().getDevice().getInterfaceCount() > 0 ? this.mPort.getDriver().getDevice().getInterface(0) : null) == null) {
            Log.e(this.TAG, "USB device NO  Interface");
            EventBus.getDefault().post(new DeviceOpenEvent(false));
            return;
        }
        UsbDevice device = this.mPort.getDriver().getDevice();
        sysSetPerMission(device);
        if (this.mUsbManager.hasPermission(device)) {
            UsbDeviceConnection openDevice = this.mUsbManager.openDevice(this.mPort.getDriver().getDevice());
            if (openDevice == null) {
                Log.e(this.TAG, "Error openDevice:  connection " + openDevice);
                EventBus.getDefault().post(new DeviceOpenEvent(false));
                return;
            }
            try {
                this.mPort.open(openDevice);
                try {
                    this.mPort.setParameters(19200, 8, 1, 0);
                    EventBus.getDefault().post(new DeviceOpenEvent(true));
                    Log.i(this.TAG, "port name:" + this.mPort.getClass().getSimpleName());
                    onDeviceStateChange();
                } catch (Exception e) {
                    Log.e(this.TAG, "Error setting up device: " + e.getMessage());
                    EventBus.getDefault().post(new DeviceOpenEvent(false));
                }
            } catch (Exception e2) {
                Log.e(this.TAG, " usb open device: " + e2.getMessage());
                EventBus.getDefault().post(new DeviceOpenEvent(false));
            }
        } else {
            Log.e(this.TAG, "permission denied for device else");
            this.mUsbManager.requestPermission(this.mPort.getDriver().getDevice(), this.mPermissionIntent);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void sleep(long j) {
        try {
            SystemClock.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onEventMainThread(DeviceOpenEvent deviceOpenEvent) {
        if (!deviceOpenEvent.mOpen) {
            this.theapp.stopTpms();
        }
    }

    private boolean sysSetPerMission(UsbDevice usbDevice) {
        sleep(500);
        return true;
    }

    @Override
    public String getDevName() {
        try {
            return this.mPort.getDriver().getDevice().getDeviceName();
        } catch (Exception unused) {
            return "";
        }
    }
}
