package com.bignerdranch.android.testplatflorm;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostFragment extends Fragment {

    private static final String TAG = "PostFragment";
    private static final String ARG_POST_ID = "post_id";

    private static final int REQUST_DATE = 0;
    private static final int REQUST_CONTACT = 1;
    private static final int REQUST_PHOTO = 2;

    private Post mPost;
    private File mPhotoFile;
    private List<EditText> editTexts = new ArrayList<EditText>();

    private int Click_Count;

    private EditText mTitleField;
    private ImageButton mPhotoButton;
    private Button mLabelButton;
    private ImageView mPhotoView;
    private LinearLayout mLabelLayout;

    public static PostFragment newInstance(UUID postId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST_ID, postId);

        PostFragment fragment = new PostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID postId = (UUID) getArguments().getSerializable(ARG_POST_ID);
        mPost = PostLab.get(getActivity()).getPost(postId);
        mPhotoFile = PostLab.get(getActivity()).getPhotoFile(mPost);
    }

    @Override
    public void onPause() {
        super.onPause();

        mPost.setLabel(Record_Label(editTexts));
        PostLab.get(getActivity())
                .updatePost(mPost);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);

        Click_Count = 0;

        mPhotoButton = (ImageButton) v.findViewById(R.id.post_camera);
        mLabelButton = (Button) v.findViewById(R.id.label_add);
        mLabelLayout = (LinearLayout) v.findViewById(R.id.label_table);

        Load_Label(mPost.getLabel());

        mTitleField = (EditText) v.findViewById(R.id.post_title);
        mTitleField.setText(mPost.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPost.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLabelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Click_Count += 1;
                EditText label_edittext = new EditText(getActivity());
                label_edittext.setHint("input label for this post");
                label_edittext.setId(Click_Count);
                label_edittext.setSingleLine(true);
                editTexts.add(label_edittext);
                mLabelLayout.addView(label_edittext);
                Long_Click(label_edittext);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.testplatflorm.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUST_PHOTO);
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.post_photo);
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return ;
        }

        if (resultCode == REQUST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.testplatflorm.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }

    }

    private String Record_Label (List<EditText> editTexts) {
        String Labels = null;

        for (int i = 0; i < editTexts.size(); i++) {
            if (Labels == null) {
                Labels = editTexts.get(i).getText().toString();
            } else {
                Labels = Labels + ";" + editTexts.get(i).getText().toString();
            }
        }
        Log.i(TAG, "labels: " + Labels);
        return Labels;
    }

    private void Load_Label (String labels) {
        String[] label_list = null;

        if (labels != null) {
            label_list = labels.split(";");
            Click_Count = label_list.length;

            for (int i = 0; i < label_list.length; i++) {
                EditText et = new EditText(getActivity());
                et.setText(label_list[i]);
                et.setId(i);
                et.setSingleLine(true);
                editTexts.add(et);
                mLabelLayout.addView(et);
                Long_Click(et);
            }
        }
    }

    private void Long_Click(EditText editText) {
        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editTexts.remove(editText);
                mLabelLayout.removeView(editText);
                Log.i(TAG, "The current label list: " + editTexts.toString());
                Toast.makeText(getActivity(), "The label: " + editText.getText().toString() + " has been deleted.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
