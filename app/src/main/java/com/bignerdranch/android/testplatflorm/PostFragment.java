package com.bignerdranch.android.testplatflorm;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

public class PostFragment extends Fragment {

    private static final String TAG = "PostFragment";
    private static final String ARG_POST_ID = "post_id";
    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int REQUST_DATE = 0;
    private static final int REQUST_CONTACT = 1;
    private static final int REQUST_PHOTO = 2;
    private static final int REQUST_GALLERY = 3;

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
                showBottomSheetDialog();

            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.post_photo);
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.testplatflorm.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            mPost.setUri(mPhotoFile.toString());

            Log.i(TAG, "The picture's file path you took is: " + uri.toString());

            updatePhotoView();
        }

        if (requestCode == REQUST_GALLERY) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                mPost.setUri(picturePath);
                Log.i(TAG, "The picture's file path you selected is: " + mPost.getUri());
//                if (EasyPermissions.hasPermissions(getContext(), galleryPermissions)) {
                    updatePhotoView();
//                    Log.i(TAG, "You get the permission to access this picture.");
//                } else {
//                    EasyPermissions.requestPermissions(this, "Access for storage",
//                            101, galleryPermissions);
//                    updatePhotoView();
//                    Log.i(TAG, "You don't have permission to get this picture.");
//                }

            } else {
                Log.i(TAG, "You don't select any picture.");
            }
        }
    }

    private void updatePhotoView() {
//        if ((mPhotoFile == null && mPost.getUri() == null)|| !mPhotoFile.exists()) {
//            mPhotoView.setImageDrawable(null);
//        } else {
//            Bitmap bitmap = PictureUtils.getScaledBitmap(
//                    mPost.getUri(), getActivity());
//            mPhotoView.setImageBitmap(bitmap);
//        }



        if (mPost.getUri() == null) {
            mPhotoView.setImageDrawable(null);
        } else {
//            BitmapFactory.Options options=new BitmapFactory.Options();
//            options.inJustDecodeBounds=true;
            Bitmap bitmap = BitmapFactory.decodeFile(mPost.getUri());
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

    public void showBottomSheetDialog() {
        BottomSheetDialog bottomsheet = new BottomSheetDialog(getContext());
        bottomsheet.setCancelable(true);
        bottomsheet.setContentView(R.layout.photo_buttom_select);
        BottomSheetFunction(bottomsheet);
        bottomsheet.show();
    }

    public void BottomSheetFunction(BottomSheetDialog bottomSheetDialog) {
        TextView Open_Camera = bottomSheetDialog.findViewById(R.id.open_camera);
        TextView Open_Gallery = bottomSheetDialog.findViewById(R.id.open_gallery);
        TextView Sheet_Cancel = bottomSheetDialog.findViewById(R.id.cancel_photo);

        Open_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

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
                bottomSheetDialog.cancel();
            }
        });

        Open_Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Open the local Gallery.", Toast.LENGTH_SHORT).show();
                openGallery();
                bottomSheetDialog.cancel();
            }
        });

        Sheet_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                openGallery();
            } else {
                Toast.makeText(getContext(),"你被拒绝了",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUST_GALLERY);
    }

    @RequiresApi (api = Build.VERSION_CODES.KITKAT)
    private String handImage(Intent data) {
        String path = null;
        Uri uri = data.getData();
        Log.i(TAG, "The uri you get is : " + uri);

        if (DocumentsContract.isDocumentUri(getContext(), uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            Log.i(TAG, "The docId is : " + docId);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                Log.i(TAG, "The uri's authority is : " + uri.getAuthority());
                String id = docId.split(":")[1];
//                String selection = MediaStore.Images.Media._ID+" = ? "+id;
                String selection = id;
                Log.i(TAG, "The selection is : " + selection);
                path = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                path = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            path = getImagePath(uri,null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }

    private String getImagePath(Uri uri, String selection) {
        final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
        };
        String path = null;
        Cursor cursor = getContext().getContentResolver().query(uri, IMAGE_PROJECTION, null,null,null);
        Log.i(TAG, "The cursor is: " + cursor);
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID)) == selection) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }

            if (cursor == null) {
                cursor.close();
            }
        }
        Log.i(TAG, "The path is in function getImagePath is : " + path);
        return path;
    }
}
