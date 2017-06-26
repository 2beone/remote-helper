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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        findViewById(R.id.btn_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = PermissionUtils.getRequiredPermissions(PermissionActivity.this);
                ActivityCompat.requestPermissions(PermissionActivity.this, permissions, 0);
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
        if (PermissionUtils.isNeverAskAgainAll(this, PermissionUtils.getRequiredPermissions(PermissionActivity.this))) {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName())));
        }
    }
}
