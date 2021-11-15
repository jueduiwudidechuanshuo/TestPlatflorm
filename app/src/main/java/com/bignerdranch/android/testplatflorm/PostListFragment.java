package com.bignerdranch.android.testplatflorm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    private RecyclerView mPostRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_item_post, container, false);

        mPostRecyclerView = (RecyclerView) view.findViewById(R.id.post_recycler_view);
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    private class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Post mPost;
        private TextView mTitleTextView;
        private ImageView mHeadImageView;

        public PostHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_post, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mHeadImageView = (ImageView) itemView.findViewById(R.id.head_image);
        }

        public void bind(Post post) {
            mPost = post;
            mTitleTextView.setText(mPost.getTitle());

        }

        @Override
        public void onClick(View v) {

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
