package forcestopper.bhagyawant.com.forcestopper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import forcestopper.bhagyawant.com.forcestopper.R;
import forcestopper.bhagyawant.com.forcestopper.utils.Constants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inti();
    }

    private void inti() {
       getString(this, "force_stop", "com.android.settings");
    }

    /**
     * Function to get localization for String "Force Stop" from system
     * @param context
     * @param str
     * @param str2
     */
    public void getString(Context context, String str, String str2) {
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(str2);
            Constants.FORCE_STOP = resourcesForApplication.getString(resourcesForApplication.getIdentifier(str, "string", str2));
        } catch (Exception ignored) {
        }

    }

    public static boolean isAccessibilityEnabled(Context activity) {
        int accessibilityEnabled = 0;
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(activity.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Exception ignored) {
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {


            String settingValue = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.contains("CloseAppsAccessibilityService")) {
                        return true;
                    }
                }
            }

        }
        return accessibilityFound;
    }

    public void onClick(View view) {
        if(view.getId()==R.id.btnStart){

            if(isAccessibilityEnabled(this)){
                startForceStopper();
            }else{
             showNeedAccessibilityPermissionDilog();
            }
        }
    }

    private void showNeedAccessibilityPermissionDilog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = View.inflate(this, R.layout.dialog_accessibility_permission_request, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        final Button btnOK = view.findViewById(R.id.btn_ok);
        final Button btnCancel = view.findViewById(R.id.btn_cancel);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void startForceStopper() {
        finish();
        startActivity(new Intent(this,ForceStopperActivity.class));
    }
}
