package kr.cnttech.webappbase.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.fingerpush.android.FingerNotification;
import com.fingerpush.android.FingerPushListener;
import com.fingerpush.android.FingerPushManager;

import java.util.Iterator;

import kr.cnttech.webappbase.lib.Utils;

/**
 * Created by hansollim on 2016-07-28.
 */
public class IntentService extends FingerPushListener {
    private static final String TAG = "IntentService";
    FingerPushManager manager;

    @Override
    public void onMessage(Context context, Bundle data) {
        Iterator<String> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = data.get(key).toString();
            Utils.Logger(TAG, context, "D", "onMessage ::: key:" + key + ", value:" + value);
        }

        try {
            manager = FingerPushManager.getInstance(IntentService.this);

            setNotification(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNotification(final Bundle data) {

        Intent intent = new Intent(IntentService.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(IntentService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        FingerNotification noti = new FingerNotification(IntentService.this);
        noti.setColor(Color.BLUE);
        noti.setNofiticaionIdentifier((int) System.currentTimeMillis());
        noti.showNotification(data, pi);
    }
}