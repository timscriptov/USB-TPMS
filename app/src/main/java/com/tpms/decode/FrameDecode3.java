package com.tpms.decode;

import android.content.Context;

import com.syt.tmps.data.UmengConst;
import com.tpms.modle.AlarmAgrs;
import com.tpms.modle.HeartbeatEvent;
import com.tpms.modle.PaireIDOkEvent;
import com.tpms.modle.QueryIDOkEvent;
import com.tpms.modle.TimeSeedEvent;
import com.tpms.modle.TiresExchangeEvent;
import com.tpms.modle.TiresState;
import com.tpms.modle.TiresStateEvent;
import com.tpms.utils.Log;
import com.tpms.utils.SLOG;

import java.nio.ByteBuffer;

import de.greenrobot.event.EventBus;

public class FrameDecode3 extends FrameDecode {
    private static final String TAG = "FrameDecode3";

    @Override
    public void init(Context context) {
        super.init(context);
        this.mPackBufferFrame = new PackBufferFrame3();
    }

    @Override
    public void onEventMainThread(ByteBuffer byteBuffer) {
        byte[] array = byteBuffer.array();
        byte b = array[2];
        SLOG.LogByteArr(TAG + "完整V", array, array.length);
        Log.i(TAG, "cmd:" + ((int) b));
        String str = "右后";
        if (b == 8) {
            Log.i(TAG, "轮胎状态,4秒上报一次");
            TiresStateEvent tiresStateEvent = new TiresStateEvent();
            if (array[3] == 0) {
                Log.i(TAG, "左前");
                tiresStateEvent.tires = 1;
            } else if (array[3] == 1) {
                Log.i(TAG, "右前");
                tiresStateEvent.tires = 2;
            } else if (array[3] == 16) {
                Log.i(TAG, "左后");
                tiresStateEvent.tires = 0;
            } else if (array[3] == 17) {
                Log.i(TAG, str);
                tiresStateEvent.tires = 3;
            } else if (array[3] == 5) {
                tiresStateEvent.tires = 5;
            } else {
                return;
            }
            TiresState tiresState = tiresStateEvent.mState;
            double d = (double) (array[4] & 255);
            Double.isNaN(d);
            tiresState.AirPressure = (int) (d * 3.44d);
            if (tiresStateEvent.mState.AirPressure > 50) {
                TiresState tiresState2 = tiresStateEvent.mState;
                tiresState2.AirPressure -= 20;
            }
            tiresStateEvent.mState.Temperature = (array[5] & 255) - 50;
            Log.i(TAG, "气压参数:" + tiresStateEvent.mState.AirPressure + ";温度:" + tiresStateEvent.mState.Temperature);
            if ((array[6] & 32) != 0) {
                Log.i(TAG, "信号丢失了");
                tiresStateEvent.mState.NoSignal = true;
            }
            int i = tiresStateEvent.tires;
            if ((array[6] & 8) != 0) {
                tiresStateEvent.mState.Leakage = true;
                Log.i(TAG, "漏气");
            }
            if ((array[6] & UmengConst.n) != 0) {
                tiresStateEvent.mState.LowPower = true;
                Log.i(TAG, "低电");
            }
            EventBus.getDefault().post(tiresStateEvent);
        } else if (b == 6) {
            if (array[3] == 24) {
                PaireIDOkEvent paireIDOkEvent = new PaireIDOkEvent();
                if (array[4] == 0) {
                    Log.i(TAG, "配对学习成功 左前");
                    paireIDOkEvent.tires = 1;
                } else if (array[4] == 1) {
                    Log.i(TAG, "配对学习成功 右前");
                    paireIDOkEvent.tires = 2;
                } else if (array[4] == 16) {
                    Log.i(TAG, "配对学习成功 左后");
                    paireIDOkEvent.tires = 0;
                } else if (array[4] == 17) {
                    Log.i(TAG, "配对学习成功 右后");
                    paireIDOkEvent.tires = 3;
                } else if (array[4] == 5) {
                    paireIDOkEvent.tires = 5;
                } else {
                    return;
                }
                EventBus.getDefault().post(paireIDOkEvent);
            } else if (array[3] != 22) {
                if (array[3] == 0 && array[4] == -120) {
                    Log.i(TAG, "是握手了,也叫心跳,最新版本 不支持了");
                    EventBus.getDefault().post(new HeartbeatEvent(0));
                } else if (array[3] == -75) {
                    EventBus.getDefault().post(new TimeSeedEvent(array[4]));
                }
            }
        } else if (b == 9) {
            SLOG.LogByteArr("find", array, array.length);
            QueryIDOkEvent queryIDOkEvent = new QueryIDOkEvent();
            if (array[3] == 1) {
                queryIDOkEvent.tires = 1;
                str = "左前";
            } else if (array[3] == 2) {
                queryIDOkEvent.tires = 2;
                str = "右前";
            } else if (array[3] == 3) {
                queryIDOkEvent.tires = 0;
                str = "左后";
            } else if (array[3] == 4) {
                queryIDOkEvent.tires = 3;
            } else {
                queryIDOkEvent.tires = 5;
                str = "备胎";
            }
            queryIDOkEvent.mID = Util.byteToUpperString(array[4]) + Util.byteToUpperString(array[5]) + Util.byteToUpperString(array[6]) + Util.byteToUpperString(array[7]);
            Log.i(TAG, "查寻反回的id " + str + ";id:" + queryIDOkEvent.mID);
            EventBus.getDefault().post(queryIDOkEvent);
        } else if (b != 10 && b == 7 && array[3] == 48) {
            TiresExchangeEvent tiresExchangeEvent = new TiresExchangeEvent("左前右前");
            if (array[4] == 0 && array[5] == 1) {
                Log.i(TAG, "左前右前 调换");
                tiresExchangeEvent = new TiresExchangeEvent("左前右前");
            } else if (array[4] == 0 && array[5] == 16) {
                Log.i(TAG, "左前左后 调换");
                tiresExchangeEvent = new TiresExchangeEvent("左前左后");
            } else if (array[4] == 0 && array[5] == 17) {
                Log.i(TAG, "左前右后 调换");
                tiresExchangeEvent = new TiresExchangeEvent("左前右后");
            } else if (array[4] == 1 && array[5] == 16) {
                Log.i(TAG, "右前左后 调换");
                tiresExchangeEvent = new TiresExchangeEvent("右前左后");
            } else if (array[4] == 1 && array[5] == 17) {
                Log.i(TAG, "右前右后 调换");
                tiresExchangeEvent = new TiresExchangeEvent("右前右后");
            } else if (array[4] == 16 && array[5] == 17) {
                Log.i(TAG, "左后右后 调换");
                tiresExchangeEvent = new TiresExchangeEvent("左后右后");
            } else if (array[4] == 16 || array[5] == 5) {
                tiresExchangeEvent = new TiresExchangeEvent("备胎交换");
            }
            EventBus.getDefault().post(tiresExchangeEvent);
        }
    }

    @Override
    public void decodeAlarmAgrsProc(byte b, byte b2, byte b3) {
        AlarmAgrs alarmAgrs = new AlarmAgrs();
        alarmAgrs.AirPressureHi = (b & 255) * 10;
        alarmAgrs.AirPressureLo = (b2 & 255) * 10;
        alarmAgrs.Temperature = b3 & 255;
        EventBus.getDefault().post(alarmAgrs);
    }
}
