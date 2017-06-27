package net.twobeone.remotehelper.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.databinding.ActivityUserViewBinding;

public class UserViewActivity extends BaseActivity {

    private ActivityUserViewBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_view);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserViewActivity.this, UserInfoActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectItem();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void selectItem() {

    }
}
