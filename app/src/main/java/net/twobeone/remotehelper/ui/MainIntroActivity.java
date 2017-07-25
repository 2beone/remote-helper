package net.twobeone.remotehelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.util.AppUtils;
import net.twobeone.remotehelper.util.PermissionUtils;

public class MainIntroActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final int REQUEST_CODE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_intro);
        ((TextView) findViewById(R.id.tv_version)).setText("현재버전: v" + AppUtils.getPackageInfo(this).versionName);
        setOnClickListener(R.id.btn_call, R.id.btn_file_box, R.id.btn_help, R.id.btn_setting, R.id.btn_safety);

        if (!hasAllPermissions()) {
            startActivityForResult(new Intent(this, PermissionActivity.class), REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_call:
                startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.REDIRECT, MainActivity.Redirect.CALL));
                break;
            case R.id.btn_file_box:
                startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.REDIRECT, MainActivity.Redirect.FILE_BOX));
                break;
            case R.id.btn_help:
                startActivity(new Intent(this, HelpActivity.class));
                // startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.REDIRECT, MainActivity.Redirect.HELP));
                break;
            case R.id.btn_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                // startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.REDIRECT, MainActivity.Redirect.SETTINGS));
                break;
            case R.id.btn_safety:
                startActivity(new Intent(this, MapActivity.class));
                // startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.REDIRECT, MainActivity.Redirect.SAFETY));
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!hasAllPermissions()) {
                finish();
            }
        }
    }

    private void setOnClickListener(int... ids) {
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
            findViewById(id).setOnLongClickListener(this);
        }
    }

    private boolean hasAllPermissions() {
        return PermissionUtils.getRequiredPermissions(this).length == 0;
    }
}
