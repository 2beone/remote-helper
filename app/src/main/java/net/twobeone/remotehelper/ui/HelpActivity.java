package net.twobeone.remotehelper.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.widget.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(HelpFragment.newInstance(R.drawable.bg_guide1));
        adapter.addFragment(HelpFragment.newInstance(R.drawable.bg_guide2));

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        ViewPagerIndicator indicator = (ViewPagerIndicator) findViewById(R.id.indicator);
        indicator.initViewPager(viewPager);
    }

    public static class HelpFragment extends Fragment {

        public static Fragment newInstance(int background) {
            HelpFragment fragment = new HelpFragment();
            Bundle args = new Bundle();
            args.putInt("BACKGROUND", background);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ImageView view = new ImageView(getActivity());
            view.setBackgroundResource(getArguments().getInt("BACKGROUND"));
            return view;
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}