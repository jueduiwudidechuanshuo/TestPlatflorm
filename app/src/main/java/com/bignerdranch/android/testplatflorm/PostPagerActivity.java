package com.bignerdranch.android.testplatflorm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class PostPagerActivity extends AppCompatActivity {
    private static final String EXTRA_POST_ID =
            "com.bignerdranch.android.testplatflorm.post_id";

    private ViewPager mViewPager;
    private List<Post> mPosts;

    public static Intent NewIntent(Context packageContext, UUID postId) {
        Intent intent = new Intent(packageContext, PostPagerActivity.class);
        intent.putExtra(EXTRA_POST_ID, postId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_pager);

        UUID postId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_POST_ID);

        mViewPager = (ViewPager) findViewById(R.id.post_view_pager);

        mPosts = PostLab.get(this).getPosts();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Post post = mPosts.get(position);
                return PostFragment.newInstance(post.getId());
            }

            @Override
            public int getCount() {
                return mPosts.size();
            }
        });

        for (int i = 0; i < mPosts.size(); i++) {
            if (mPosts.get(i).getId().equals(postId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
