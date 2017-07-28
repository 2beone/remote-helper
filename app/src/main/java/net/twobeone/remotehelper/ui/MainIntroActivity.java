package net.twobeone.remotehelper.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.util.AppUtils;
import net.twobeone.remotehelper.util.PermissionUtils;
import net.twobeone.remotehelper.util.StringUtils;

public class MainIntroActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final int REQUEST_CODE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_intro);
        ((TextView) findViewById(R.id.tv_version)).setText("현재버전: v" + AppUtils.getPackageInfo(this).versionName);
        setOnClickListener(R.id.btn_call, R.id.btn_file_box, R.id.btn_help, R.id.btn_setting, R.id.btn_myinfo, R.id.btn_safety);

        if (!hasAllPermissions()) {
            startActivityForResult(new Intent(this, PermissionActivity.class), REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_call:
                if (StringUtils.isNullOrEmpty(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_USER_NAME, ""))) {
                    new AlertDialog.Builder(this).setIcon(R.drawable.ic_assignment_black_24dp).setTitle(R.string.confirm_need_myinfo_title).setMessage(R.string.confirm_need_myinfo).setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MainIntroActivity.this, UserInfoActivity.class));
                        }
                    }).show();
                } else {
                    new AlertDialog.Builder(this).setIcon(R.drawable.ic_video_call_black_24dp).setTitle(R.string.confirm_call_title).setMessage(R.string.confirm_call).setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MainIntroActivity.this, MainActivity.class).putExtra(MainActivity.REDIRECT, MainActivity.Redirect.CALL));
                        }
                    }).show();
                }
                break;
            case R.id.btn_file_box:
                startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.REDIRECT, MainActivity.Redirect.FILE_BOX));
                break;
            case R.id.btn_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.btn_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.btn_myinfo:
                startActivity(new Intent(this, UserInfoActivity.class));
                break;
            case R.id.btn_safety:
                startActivity(new Intent(this, MapActivity.class));
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_call:
                startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.REDIRECT, MainActivity.Redirect.MUTECALL));
                break;
        }
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("알림").setMessage("어플리케이션을 종료하시겠습니까?")
                .setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();
    }
}
