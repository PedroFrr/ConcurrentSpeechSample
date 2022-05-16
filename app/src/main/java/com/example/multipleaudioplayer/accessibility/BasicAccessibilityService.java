package com.example.multipleaudioplayer.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.example.multipleaudioplayer.utils.A11yNodeInfo;

/**
 * Created by chrismcmeeking on 3/3/17.
 */

public class BasicAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("BasicAccessibilityService", event.toString());

        switch (event.getEventType()) {
            //On Gesture events print out the entire view heirarchy!
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                Log.d("BasicAccessibilityService", A11yNodeInfo.wrap(getRootInActiveWindow()).toViewHeirarchy());

            default: {
                //If the event has a source, let's print it out separately.
                if (event.getSource() != null) {
                    Log.d("BasicAccessibilityService", A11yNodeInfo.wrap(event.getSource()).toViewHeirarchy());
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.e("BasicAccessibilityService", "Service Interrupted: Have never actually had this happen.");
    }
}