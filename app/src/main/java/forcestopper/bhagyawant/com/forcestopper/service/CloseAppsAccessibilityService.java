package forcestopper.bhagyawant.com.forcestopper.service;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import forcestopper.bhagyawant.com.forcestopper.utils.Constants;

public class CloseAppsAccessibilityService extends AccessibilityService {
    AccessibilityServiceInfo info = new AccessibilityServiceInfo();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo nodeInfo = accessibilityEvent.getSource();
        if (nodeInfo == null) {
            return;
        }

        List<AccessibilityNodeInfo> list = nodeInfo
                .findAccessibilityNodeInfosByText(Constants.FORCE_STOP);


        if (Constants.isCleanRunning) {
            if (list.isEmpty() || list.size() == 0) {
                performGlobalAction(GLOBAL_ACTION_BACK);
            } else {
                for (final AccessibilityNodeInfo node : list) {
                    if (node.isEnabled()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }, 1000);

                    } else {
                        performGlobalAction(GLOBAL_ACTION_BACK);
                    }
                }
                list = nodeInfo
                        .findAccessibilityNodeInfosByText(getString(android.R.string.ok));
                for (final AccessibilityNodeInfo node : list) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        info.notificationTimeout = 100;
        this.setServiceInfo(info);
    }

}
