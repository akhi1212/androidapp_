package com.theindiecorp.khelodadmin.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theindiecorp.khelodadmin.Data.Post;
import com.theindiecorp.khelodadmin.EditArticleActivity;
import com.theindiecorp.khelodadmin.EditEventActivity;
import com.theindiecorp.khelodadmin.EditQuizActivity;
import com.theindiecorp.khelodadmin.MainActivity;
import com.theindiecorp.khelodadmin.QuizViewActivity;
import com.theindiecorp.khelodadmin.R;

import java.io.File;
import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {

    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Post> dataSet;
    private PostListener listener;

    public interface PostListener{
        public void deletePost(Post post, int position);
    }

    public int setPosts(ArrayList<Post> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView title, description, date, eventName, peopleCount, subTitle,referenceUrl, winnerPointsTv, feesTv;
        private ImageView profileImg, mainImg, menuImg;
        LinearLayout profileBar, feesBox;
        private Button playQuizBtn;
        private FrameLayout winnerPointsBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.main_item_title);
            this.description = itemView.findViewById(R.id.main_item_description);
            this.profileImg = itemView.findViewById(R.id.main_item_profile_pic);
            this.mainImg = itemView.findViewById(R.id.main_item_main_image);
            this.date = itemView.findViewById(R.id.main_item_date);
            this.eventName = itemView.findViewById(R.id.main_item_event_name);
            this.peopleCount = itemView.findViewById(R.id.main_item_people_count);
            this.profileBar = itemView.findViewById(R.id.profile_bar);
            this.menuImg = itemView.findViewById(R.id.main_item_menu_img);
            this.subTitle = itemView.findViewById(R.id.event_view_sub_title);
            this.playQuizBtn = itemView.findViewById(R.id.play_quiz_btn);
            this.referenceUrl = itemView.findViewById(R.id.main_item_link);
            this.winnerPointsBox = itemView.findViewById(R.id.winner_points_box);
            this.winnerPointsTv = itemView.findViewById(R.id.winner_points);
            this.feesTv = itemView.findViewById(R.id.quiz_view_fees_points);
            this.feesBox = itemView.findViewById(R.id.fees_box);
        }
    }

    public PostsAdapter(Context context, ArrayList<Post> dataSet, PostListener listener){
        this.context = context;
        this.dataSet = dataSet;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_main_recycler_view_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Post post = dataSet.get(position);

        holder.description.setText(post.getDescription());
        holder.subTitle.setText(post.getType().toUpperCase());

        if(post.getDescription()==null || post.getDescription().isEmpty()){
            holder.description.setVisibility(View.GONE);
        }

        if(!post.getType().equals("article")){
            holder.referenceUrl.setVisibility(View.GONE);
        }
        else{
            holder.referenceUrl.setVisibility(View.VISIBLE);
        }

        databaseReference.child("users").child(post.getHostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.title.setText(dataSnapshot.child("displayName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.date.setText(post.getEventDate());
        holder.date.setTextColor(context.getResources().getColor(R.color.colorAccent));

        holder.eventName.setText(post.getEventName());
        holder.peopleCount.setText(post.getPeopleCount() + "");
        final Uri file=Uri.fromFile(new File(""));
        final StorageReference profileImageReference = storage.getReference().child("users/" + post.getHostId() + "/images/profile_pic/profile_pic.jpeg");
        profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profileImageReference.putFile(file);
                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .apply(new RequestOptions()
                                .override(50,50))
                        .load(uri)
                        .into(holder.profileImg);
                profileImageReference.putFile(file);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

        if(post.getReferenceLink() == null||post.getReferenceLink().isEmpty()){
            holder.referenceUrl.setVisibility(View.GONE);
        }

        if(post.getDescription() == null||post.getDescription().isEmpty()){
            holder.description.setVisibility(View.GONE);
        }

        if(post.getImgUrl() != null) {
            StorageReference imageReference = storage.getReference().child(post.getImgUrl());
            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context.getApplicationContext())
                            .load(uri)
                            .into(holder.mainImg);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(MainActivity.TAG, exception.getMessage());
                }
            });
        }
        else{
            holder.mainImg.setVisibility(View.GONE);
        }

        if(post.getType().equals("article")){
            holder.peopleCount.setVisibility(View.GONE);
            holder.eventName.setVisibility(View.GONE);
            holder.date.setVisibility(View.GONE);
        }
        else{
            holder.eventName.setVisibility(View.VISIBLE);
            holder.date.setVisibility(View.VISIBLE);
        }

        if(post.getFees()!=null)
            if(!post.getFees().isEmpty()){
                holder.feesBox.setVisibility(View.VISIBLE);
                holder.feesTv.setText("Rs. " + post.getFees());
            }
        else{
                holder.feesBox.setVisibility(View.GONE);
            }

        if(post.getType().equals("quiz")){
            holder.playQuizBtn.setVisibility(View.GONE);
            holder.date.setVisibility(View.GONE);
            holder.description.setVisibility(View.GONE);
            holder.winnerPointsBox.setVisibility(View.VISIBLE);
            if(!post.getFees().isEmpty()){
                holder.feesTv.setText("Rs. " + post.getFees());
            }
            else
                holder.feesTv.setText("Free");
        }
        else{
            holder.playQuizBtn.setVisibility(View.GONE);
            holder.winnerPointsBox.setVisibility(View.GONE);
        }

        if(post.getReferenceLink()!=null){
            holder.referenceUrl.setText(post.getReferenceLink());
            holder.referenceUrl.setVisibility(View.VISIBLE);
        }
        else
            holder.referenceUrl.setVisibility(View.GONE);

        holder.menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(context, holder.menuImg);
                popup.inflate(R.menu.post_options);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.post_delete:
                                listener.deletePost(dataSet.get(position), position);
                                break;
                            case R.id.edit_post:
                                if(post.getType().equals("article"))
                                    context.startActivity(new Intent(context, EditArticleActivity.class)
                                            .putExtra("postId", post.getPostId()));
                                else if(post.getType().equals("event"))
                                    context.startActivity(new Intent(context, EditEventActivity.class)
                                            .putExtra("postId", post.getPostId())
                                            .putExtra("hostId",post.getHostId()));
                                else context.startActivity(new Intent(context, EditQuizActivity.class)
                                            .putExtra("postId", post.getPostId())
                                            .putExtra("hostId",post.getHostId()));
                            default:
                                return false;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });

        final Post p = post;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(p.getType().equals("quiz")){
                    Intent intent = new Intent(context, QuizViewActivity.class);
                    intent.putExtra("quizId", p.getPostId());
                    context.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
