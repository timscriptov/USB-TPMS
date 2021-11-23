package com.tpms.decode;

import android.content.Context;

import com.mcal.tmps.data.UmengConst;
import com.tpms.modle.AlarmAgrs;
import com.tpms.modle.PaireIDOkEvent;
import com.tpms.modle.QueryIDOkEvent;
import com.tpms.modle.ShakeHands;
import com.tpms.modle.TiresState;
import com.tpms.modle.TiresStateEvent;
import com.tpms.utils.Log;
import com.tpms.utils.SLOG;

import java.nio.ByteBuffer;

import de.greenrobot.event.EventBus;

public class FrameDecode {
    private static final String TAG = "FrameDecode";
    protected PackBufferFrame mPackBufferFrame;
    long downtime = 0;
    Context mctx;

    public void init(Context context) {
        this.mctx = context;
        EventBus.getDefault().register(this);
        this.mPackBufferFrame = new PackBufferFrame();
    }

    public PackBufferFrame getPackBufferFrame() {
        return this.mPackBufferFrame;
    }

    public void onEventMainThread(ByteBuffer byteBuffer) {
        byte[] array = byteBuffer.array();
        byte b = array[4];
        SLOG.LogByteArr(TAG, array, array.length);
        if (b == 33) {
            Log.i("find", "查寻反回基本参数");
            SLOG.LogByteArr("find", array, array.length);
            decodeAlarmAgrsProc(array[6], array[7], array[8]);
        } else if (b == 17) {
            Log.i(TAG, "是握手了");
            EventBus.getDefault().post(new ShakeHands(0));
        } else if (b == 65) {
            Log.i("find", "查寻反回的id");
            SLOG.LogByteArr("find", array, array.length);
            QueryIDOkEvent queryIDOkEvent = new QueryIDOkEvent();
            queryIDOkEvent.tires = array[5];
            queryIDOkEvent.mID = Util.byteToUpperString(array[7]) + Util.byteToUpperString(array[6]);
            EventBus.getDefault().post(queryIDOkEvent);
        } else if (b == 97) {
            Log.i("find", "配对反回的id");
            SLOG.LogByteArr("find", array, array.length);
            PaireIDOkEvent paireIDOkEvent = new PaireIDOkEvent();
            paireIDOkEvent.tires = array[5];
            paireIDOkEvent.mID = Util.byteToUpperString(array[7]) + Util.byteToUpperString(array[6]);
            EventBus.getDefault().post(paireIDOkEvent);
        } else if (b == 113) {
            Log.i(TAG, "轮胎状态");
            TiresStateEvent tiresStateEvent = new TiresStateEvent();
            tiresStateEvent.tires = array[5];
            tiresStateEvent.mState.AirPressure = (array[6] & 255) + ((array[7] & 255) << 8);
            Log.i(TAG, "气压参数:low:" + (array[6] & 255) + ";hi:" + ((array[7] & 255) << 8));
            tiresStateEvent.mState.Temperature = array[8] & 255;
            if ((array[9] & Byte.MIN_VALUE) != 0) {
                Log.i(TAG, "无效数据");
                EventBus.getDefault().post(tiresStateEvent);
                return;
            } else if ((array[9] & 32) == 0) {
                Log.i(TAG, "无效报警");
                EventBus.getDefault().post(tiresStateEvent);
                return;
            } else {
                if ((array[9] & 1) != 0) {
                    TiresState tiresState = tiresStateEvent.mState;
                    tiresState.error = tiresState.error +
                            " 低压";
                    Log.i(TAG, "低压");
                }
                if ((array[9] & 2) != 0) {
                    TiresState tiresState2 = tiresStateEvent.mState;
                    tiresState2.error = tiresState2.error +
                            " 高压";
                    Log.i(TAG, "高压");
                }
                if ((4 & array[9]) != 0) {
                    TiresState tiresState3 = tiresStateEvent.mState;
                    tiresState3.error = tiresState3.error +
                            " 高温";
                    Log.i(TAG, "高温");
                }
                if ((array[9] & 8) != 0) {
                    TiresState tiresState4 = tiresStateEvent.mState;
                    tiresState4.error = tiresState4.error +
                            " 漏气";
                    Log.i(TAG, "漏气");
                }
                if ((array[9] & UmengConst.n) != 0) {
                    TiresState tiresState5 = tiresStateEvent.mState;
                    tiresState5.error = tiresState5.error +
                            " 低电";
                    Log.i(TAG, "低电");
                }
                EventBus.getDefault().post(tiresStateEvent);
            }
        } else if (b == -127) {
            Log.i(TAG, "协议版本号,日月,年,版本号");
        } else if (b == -1) {
            Log.i(TAG, "错误");
            if (array[5] == 1) {
                Log.i(TAG, "通信错误");
            } else if (array[5] == 2) {
                Log.i(TAG, "不支持该功能号");
            } else if (array[5] == 3) {
                Log.i(TAG, "不支持该子功能号");
            } else if (array[5] == 4) {
                Log.i(TAG, "写rom失败");
            } else if (array[5] == 5) {
                Log.i(TAG, "配对超时");
            } else if (array[5] == 7) {
                Log.i(TAG, "接收机RF错误");
            } else if (array[5] == 8) {
                Log.i(TAG, "压力传感器错误");
            } else if (array[5] == 9) {
                Log.i(TAG, "温度传感器错误");
            }
            EventBus.getDefault().post(new ShakeHands(array[5]));
        }
        Log.i(TAG, "cmd:" + ((int) b));
    }

    public void decodeAlarmAgrsProc(byte b, byte b2, byte b3) {
        AlarmAgrs alarmAgrs = new AlarmAgrs();
        alarmAgrs.AirPressureHi = (b & 255) * 10;
        alarmAgrs.AirPressureLo = (b2 & 255) * 10;
        alarmAgrs.Temperature = b3 & 255;
        EventBus.getDefault().post(alarmAgrs);
    }
}
