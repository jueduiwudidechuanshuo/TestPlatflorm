package com.bignerdranch.android.testplatflorm;


import androidx.fragment.app.Fragment;

public class HomePageActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PostListFragment();
    }
}
