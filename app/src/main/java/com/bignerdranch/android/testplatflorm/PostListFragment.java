package com.bignerdranch.android.testplatflorm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    private RecyclerView mPostRecyclerView;
    private PostAdapter mAdapter;
    private FlexboxLayout mLabelFlexboxLayout;

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
            if (labels != null) {
                for (int i = 0; i < labels.length; i++) {
                    TextView tv = new TextView(getActivity());
                    tv.setText(labels[i]);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(15);
                    tv.setPadding(5,0,5,0);
                    tv.setBackground(getActivity().getDrawable(R.drawable.label_bg));
                    mLabelFlexboxLayout.addView(tv);
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
            Post post = mPosts.get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            return mPosts.size();
        }

        public void setPosts(List<Post> posts) {
            mPosts = posts;
        }
    }
}
