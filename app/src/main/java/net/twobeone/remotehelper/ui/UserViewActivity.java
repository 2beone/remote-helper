package net.twobeone.remotehelper.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.databinding.ActivityUserViewBinding;
import net.twobeone.remotehelper.db.UserDao;
import net.twobeone.remotehelper.db.model.User;

public class UserViewActivity extends BaseActivity {

    private ActivityUserViewBinding mBinding;

    private Animator.AnimatorListener mCollapseListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mBinding.contentOptional.setVisibility(View.GONE);
            hideSoftInputFromWindow();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_view);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.etUserMobile.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mBinding.etEmergencyContact.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // 선택정보
        mBinding.tvOptional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.contentOptional.getVisibility() == View.VISIBLE) {
                    collapse(mBinding.contentOptional);
                } else {
                    expand(mBinding.contentOptional);
                }
            }
        });

        // 저장버튼
        mBinding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                hideSoftInputFromWindow();
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

    private void hideSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mBinding.getRoot().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void expand(View view) {
        view.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);
        slideAnimator(view, 0, view.getMeasuredHeight()).start();
    }

    private void collapse(final View view) {
        int finalHeight = view.getHeight();
        ValueAnimator animator = slideAnimator(view, finalHeight, 0);
        animator.addListener(mCollapseListener);
        animator.start();
    }

    private ValueAnimator slideAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
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
