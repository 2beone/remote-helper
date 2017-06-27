package net.twobeone.remotehelper.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.databinding.ActivityUserViewBinding;
import net.twobeone.remotehelper.db.UserDao;
import net.twobeone.remotehelper.db.model.User;

public class UserViewActivity extends BaseActivity {

    private ActivityUserViewBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_view);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.etUserMobile.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mBinding.etEmergencyContact.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mBinding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mBinding.getRoot().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        // TODO
        mBinding.btnEdit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(UserViewActivity.this, UserInfoActivity.class));
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectData();
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

    private void selectData() {
        User user = UserDao.getInstance().select();
        if (user != null) {
            mBinding.etUserName.setText(user.name);
            mBinding.etUserAge.setText(user.age);
            mBinding.etUserMobile.setText(user.mobile);
            mBinding.etEmergencyContact.setText(user.emergency);
        }
    }

    private void saveData() {
        User user = new User();
        user.name = mBinding.etUserName.getText().toString().trim();
        user.age = mBinding.etUserAge.getText().toString();
        user.mobile = mBinding.etUserMobile.getText().toString();
        user.emergency = mBinding.etEmergencyContact.getText().toString();
        if (UserDao.getInstance().update(user) == 0) {
            UserDao.getInstance().insert(user);
        }
        Toast.makeText(this, "정상적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }
}
