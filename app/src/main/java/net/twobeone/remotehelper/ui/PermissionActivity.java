package net.twobeone.remotehelper.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.util.PermissionUtils;

public class PermissionActivity extends BaseActivity {

    private interface RequestCode {
        int NORMAL = 1;
        int NEVER_ASK_AGAIN = 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        findViewById(R.id.btn_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = PermissionUtils.getRequiredPermissions(PermissionActivity.this);
                boolean isNeverAskAgainAll = PermissionUtils.isNeverAskAgainAll(PermissionActivity.this, permissions);
                ActivityCompat.requestPermissions(PermissionActivity.this, permissions, isNeverAskAgainAll ? RequestCode.NEVER_ASK_AGAIN : RequestCode.NORMAL);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionUtils.getRequiredPermissions(this).length == 0) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCode.NEVER_ASK_AGAIN) {
            // PermissionUtils.requestPermission(this, "권한필요", 1);
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName())));
        }
    }
}
