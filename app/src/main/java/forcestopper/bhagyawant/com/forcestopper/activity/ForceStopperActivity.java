package forcestopper.bhagyawant.com.forcestopper.activity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import forcestopper.bhagyawant.com.forcestopper.R;
import forcestopper.bhagyawant.com.forcestopper.utils.Constants;

public class ForceStopperActivity extends AppCompatActivity {

    static int i = 0;
    ArrayList<String> installedApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force_stopper);

        Constants.isCleanRunning = true;
        wakeLockScreen();
        installedApps();
    }

    public void installedApps() {

        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && (packInfo.applicationInfo.flags & ApplicationInfo.FLAG_STOPPED) == 0) {
                String appName = packInfo.applicationInfo.packageName;
                Log.d("Running ", ": " + appName);
                installedApps.add(appName);
            }
        }
    }

    private void wakeLockScreen() {
        @SuppressWarnings("deprecation") KeyguardManager.KeyguardLock lock = ((KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE)).newKeyguardLock(KEYGUARD_SERVICE);
        PowerManager powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        PowerManager.WakeLock wake = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        lock.disableKeyguard();
        wake.acquire();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        i = 0;
        Constants.isCleanRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cleanApps();
    }

    private void cleanApps() {
        try {
            //To prevent self from getting killed before killing others.
            if (installedApps.size() >= i && (installedApps.get(i).contains("com.bhagyawant.forcestopper"))) {
                i = i + 1;
                onResume();
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + installedApps.get(i)));
                startActivity(intent);
                i = i + 1;
            }

        } catch (Exception e) {
            finish();
            i = 0;
        }
    }

}
