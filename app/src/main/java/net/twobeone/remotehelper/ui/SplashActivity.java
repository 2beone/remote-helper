package net.twobeone.remotehelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.util.PermissionUtils;

public class SplashActivity extends BaseActivity {

    private static final int REQUEST_CODE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasAllPermissions()) {
            startIntroActivity();
        } else {
            startActivityForResult(new Intent(this, PermissionActivity.class), REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (hasAllPermissions()) {
                startMainActivity();
            } else {
                finish();
            }
        }
    }

    private void startIntroActivity() {
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 1500);
    }

    private void startMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        overridePendingTransition(R.transition.slide_fade_in, R.transition.slide_fade_out);
        finish();
    }

    private boolean hasAllPermissions() {
        return PermissionUtils.getRequiredPermissions(this).length == 0;
    }
}
