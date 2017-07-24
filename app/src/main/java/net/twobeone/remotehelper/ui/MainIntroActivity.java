package net.twobeone.remotehelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.twobeone.remotehelper.R;

public class MainIntroActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_intro);
        setOnClickListener(R.id.btn_call, R.id.btn_file_box);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_call:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btn_file_box:
                startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.REDIRECT, MainActivity.Redirect.FILE_BOX));
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    private void setOnClickListener(int... ids) {
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
            findViewById(id).setOnLongClickListener(this);
        }
    }
}
