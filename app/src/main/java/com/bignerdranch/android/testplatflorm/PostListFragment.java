package com.bignerdranch.android.testplatflorm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;

import java.io.File;
import java.util.List;

public class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    private RecyclerView mPostRecyclerView;
    private PostAdapter mAdapter;
    private FlexboxLayout mLabelFlexboxLayout;

    private boolean is_onStop = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        mPostRecyclerView = (RecyclerView) view.findViewById(R.id.post_recycler_view);
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI() {
        PostLab postLab = PostLab.get(getActivity());
        List<Post> posts = postLab.getPosts();

        if (mAdapter == null) {
            mAdapter = new PostAdapter(posts);
            mPostRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setPosts(posts);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_post_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_post:
                Post post = new Post();
                PostLab.get(getActivity()).addPost(post);
                Intent intent = PostPagerActivity
                        .NewIntent(getActivity(), post.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public String[] getLabelList(String labels) {

        if (labels != null) {

            String[] label_list = labels.split(";");
            return label_list;
        } else {
            return null;
        }
    }

    private class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Post mPost;
        private TextView mTitleTextView;
        private ImageView mHeadImageView;
        private File mPhotoFile;

        public PostHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_post, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.label_post_title);
            mHeadImageView = (ImageView) itemView.findViewById(R.id.head_image);
            mLabelFlexboxLayout = (FlexboxLayout) itemView.findViewById(R.id.flex_label);
        }

        public void bind(Post post) {
            mPost = post;
            if (mPost.getTitle() != null) {
                mTitleTextView.setText(mPost.getTitle());
            }
            mPhotoFile = PostLab.get(getActivity()).getPhotoFile(mPost);
            if (mPhotoFile == null || !mPhotoFile.exists()) {
                mHeadImageView.setImageDrawable(null);
            } else {
                Bitmap bitmap = PictureUtils.getScaledBitmap(
                        mPhotoFile.getPath(), getActivity());
                mHeadImageView.setImageBitmap(bitmap);
            }

            Log.i(TAG, "post label: " + mPost.getLabel());
            String[] labels = getLabelList(mPost.getLabel());
            Log.i(TAG, "labels: " + labels);
            Log.i(TAG, "current activity: " + getActivity().toString());
            if (labels != null) {
                for (int i = 0; i < labels.length; i++) {
                    TextView tv = new TextView(getActivity());
                    tv.setText(labels[i]);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(20);
                    tv.setPadding(15,0,15,0);
                    tv.setBackground(getActivity().getDrawable(R.drawable.label_bg));
                    mLabelFlexboxLayout.addView(tv);
                    ViewGroup.LayoutParams params = tv.getLayoutParams();
                    if (params instanceof FlexboxLayout.LayoutParams) {
                        FlexboxLayout.LayoutParams layoutParams = (FlexboxLayout.LayoutParams) params;
                        layoutParams.setMargins(5, 5, 5, 5);
//                        layoutParams.setFlexBasisPercent(0.5f);
                    }
                }
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = PostPagerActivity.NewIntent(getActivity(), mPost.getId());
            startActivity(intent);
        }
    }

    private class PostAdapter extends RecyclerView.Adapter<PostHolder> {

        private List<Post> mPosts;

        public PostAdapter(List<Post> posts) {
            mPosts = posts;
        }

        @NonNull
        @Override
        public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PostHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PostHolder holder, int position) {
            if (!is_onStop) {
                Post post = mPosts.get(position);
                Log.i(TAG, "label position: " + position);
                holder.bind(post);
                Log.i(TAG, "ooBindViewHolder");
            }
        }

        @Override
        public int getItemCount() {
            return mPosts.size();
        }

        public void setPosts(List<Post> posts) {
            mPosts = posts;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "OnStart");
    }

    @Override
    public void onStop() {
        is_onStop = true;
        super.onStop();
        Log.i(TAG, "OnStop");
    }
}
