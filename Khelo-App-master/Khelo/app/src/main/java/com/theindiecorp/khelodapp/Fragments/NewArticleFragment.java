package com.theindiecorp.khelodapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theindiecorp.khelodapp.Model.Post;
import com.theindiecorp.khelodapp.Activities.HomeActivity;
import com.theindiecorp.khelodapp.R;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class NewArticleFragment extends Fragment {

    int PLACE_PICKER_REQUEST = 12;
    Uri imgUri;
    private static final int PICK_IMAGE = 100;
    ImageView image;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    EditText articleText, referenceLinkEt;
    TextView imageUploadText;

    public NewArticleFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_article, container, false);

        articleText = view.findViewById(R.id.article_text);
        Button uploadImageBtn = view.findViewById(R.id.uploadImage);
        Button addPostBtn = view.findViewById(R.id.shareBtn);
        image = view.findViewById(R.id.new_article_main_image);
        referenceLinkEt = view.findViewById(R.id.article_url);
        imageUploadText = view.findViewById(R.id.image_text);

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addPost();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, getContext());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
            } else if (requestCode == PICK_IMAGE) {
                image.setImageURI(data.getData());
            }
        }
    }

    private void openGallery() {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);

        imageUploadText.setVisibility(View.GONE);
    }

    public String uploadImage(String eventId) {

        if (image.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();

            String path = "events/" + eventId + "/images/image.jpeg";
            StorageReference storageReference = storage.getReference(path);

            UploadTask uploadTask = storageReference.putBytes(bitmapdata);

            return path;
        }

        return "";
    }

    private void addPost() throws ParseException {
        String articleTxt = articleText.getText().toString();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if (!TextUtils.isEmpty(articleTxt)) {

            String id = databaseReference.push().getKey();
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            String date = day + "/" + month + "/" + year;

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String imgPath = uploadImage(id);

            Post event = new Post();
            event.setType("article");
            event.setPublishDate(date);
            if (!TextUtils.isEmpty(imgPath))
                event.setImgUrl(imgPath);
            event.setHostId(userId);
            event.setEventName("");
            event.setDescription(articleTxt);
            event.setCommentCount(0);
            event.setLikeCount(0);
            event.setPeopleCount(0);
            event.setReferenceLink(referenceLinkEt.getText().toString());
            event.setEventMonth(month);
            event.setEventYear(year);
            event.setNumberOfRepots(0);

            Toast.makeText(getContext(), "Shared", Toast.LENGTH_LONG).show();

            databaseReference.child("posts").child(id).setValue(event);
            startActivity(new Intent(getContext(), HomeActivity.class));

        } else {
            Toast.makeText(getContext(), "You should enter some text first", Toast.LENGTH_LONG).show();
        }
    }
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.home_frame_container, fragment);
        transaction.commit();
    }
}
