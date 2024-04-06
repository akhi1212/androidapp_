
package com.theindiecorp.khelodapp.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theindiecorp.khelodapp.Model.Notification;
import com.theindiecorp.khelodapp.Model.Post;
import com.theindiecorp.khelodapp.Model.User;
import com.theindiecorp.khelodapp.Activities.EventViewActivity;
import com.theindiecorp.khelodapp.Activities.HomeActivity;
import com.theindiecorp.khelodapp.Activities.QuizView;
import com.theindiecorp.khelodapp.R;
import com.theindiecorp.khelodapp.Activities.RecentQuizActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainFeedAdapter extends RecyclerView.Adapter<MainFeedAdapter.MyViewHolder> {
    private boolean isBookmarked = false;
    private boolean isLiked = false;
    LinearLayout layout;

    private String name;

    private StorageReference mStorageRef;

    private Context context;
    private ArrayList<Post> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    public void addEvent(Post event) {
        this.dataSet.add(event);
    }

    public int setPosts(ArrayList<Post> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView title, description, date, eventName, peopleCount, likeCount, subTitle,referenceUrl, winnerPointsTv, feesTv;
        private ImageView profileImg, mainImg, bookmarkBtn, menuImg,likeBtn,commentBtn,shareBtn;
        LinearLayout profileBar, feesBox;
        Boolean bookmarked,liked;
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
            this.bookmarkBtn = itemView.findViewById(R.id.main_item_bookmark_btn);
            this.profileBar = itemView.findViewById(R.id.profile_bar);
            this.menuImg = itemView.findViewById(R.id.main_item_menu_img);
            this.likeBtn = itemView.findViewById(R.id.main_item_like_btn);
            this.likeCount = itemView.findViewById(R.id.main_post_likes);
            this.commentBtn = itemView.findViewById(R.id.main_item_comment_btn);
            this.subTitle = itemView.findViewById(R.id.event_view_sub_title);
            this.shareBtn = itemView.findViewById(R.id.main_item_invite_btn);
            this.playQuizBtn = itemView.findViewById(R.id.play_quiz_btn);
            this.referenceUrl = itemView.findViewById(R.id.main_item_link);
            this.winnerPointsBox = itemView.findViewById(R.id.winner_points_box);
            this.winnerPointsTv = itemView.findViewById(R.id.winner_points);
            this.feesTv = itemView.findViewById(R.id.quiz_view_fees_points);
            this.feesBox = itemView.findViewById(R.id.fees_box);
        }
    }

    public MainFeedAdapter(Context context, ArrayList<Post> dataSet){
        this.context = context;
        this.dataSet = dataSet;
    }
    public MainFeedAdapter(ArrayList<Post> data, Context context, boolean isBookmarked, LinearLayout frameLayout, boolean isLiked) {
        this.dataSet = data;
        this.context = context;
        this.isBookmarked = isBookmarked;
        this.layout = frameLayout;
        this.isLiked = isLiked;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_main_recycler_view_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        return myViewHolder;
    }

    private Dialog onCreateDialog(final String eventId) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.report_dialog_layout);

        final EditText messageTxt = dialog.findViewById(R.id.report_message);
        Button submitBtn = dialog.findViewById(R.id.report_dialog_submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("reports").child(eventId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                databaseReference.child("reports").child(eventId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> reports = new ArrayList<>();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            reports.add(snapshot.getKey());
                        }
                        if(reports.size()>=50){
                            databaseReference.child("events").child(eventId).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                dialog.dismiss();
            }
        });
        return dialog;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int itemPosition) {
        final Post post = dataSet.get(itemPosition);
        final String postText;
        final int[] likeCounter = {0};

        holder.description.setText(post.getDescription());
        holder.subTitle.setText(post.getType().toUpperCase());

        if(post.getType().equals("quiz") || post.getType().equals("event"))
            postText = post.getEventName();
        else
            postText = post.getDescription();

        if(post.getDescription()==null || post.getDescription().isEmpty()){
            holder.description.setVisibility(View.GONE);
        }

        if(!post.getType().equals("article")){
            holder.profileBar.setVisibility(View.GONE);
            holder.referenceUrl.setVisibility(View.GONE);
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
        holder.likeCount.setText(post.getLikeCount() + "");

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
                    Log.d(HomeActivity.TAG, exception.getMessage());
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

            if(post.getHostId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            holder.playQuizBtn.setVisibility(View.GONE);

        holder.playQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(post.getHostId().equals(HomeActivity.userId)){
                    context.startActivity(new Intent(context, RecentQuizActivity.class).putExtra("quizId",post.getPostId()));
                }
                else
                    context.startActivity(new Intent(context, QuizView.class)
                            .putExtra("quizId",post.getPostId())
                            .putExtra("hostId",post.getHostId()));
            }
        });

        databaseReference.child("likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post.getPostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.liked = dataSnapshot.exists();
                if(holder.liked)
                    holder.likeBtn.setImageDrawable(holder.likeBtn.getResources().getDrawable(R.drawable.liked));
                else
                    holder.likeBtn.setImageDrawable(holder.likeBtn.getResources().getDrawable(R.drawable.notliked));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("bookmarks").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post.getPostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.bookmarked = dataSnapshot.exists();
                if (holder.bookmarked)
                    holder.bookmarkBtn.setImageDrawable(holder.bookmarkBtn.getResources().getDrawable(R.drawable.ic_checked));
                else
                    holder.bookmarkBtn.setImageDrawable(holder.bookmarkBtn.getResources().getDrawable(R.drawable.ic_bookmark_black));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("posts").child(post.getPostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likeCounter[0] = dataSnapshot.child("likeCount").getValue(Integer.class);
                holder.likeCount.setText(likeCounter[0] + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like(holder.liked,post.getPostId(),likeCounter[0],post.getHostId(), postText);
            }
        });

        holder.bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmark(holder.bookmarked, post.getPostId());
            }
        });

        holder.referenceUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(URLUtil.isValidUrl(holder.referenceUrl.getText().toString())){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getReferenceLink()));
                    context.startActivity(browserIntent);
                }
            }
        });

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse("https://khelod.com"))
                        .setDomainUriPrefix("https://khelodapp.page.link/XktS")
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.theindiecorp.khelodapp").build())
                        .buildDynamicLink();

                Uri dynamicLinkUri = dynamicLink.getUri();

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Check out my post on KheloD App now. \n Download now : \n " + dynamicLinkUri.toString();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Invitation");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        if (isBookmarked) {
            holder.mainImg.setVisibility(View.GONE);
        } else {
            // main image reference
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
                        Log.d(HomeActivity.TAG, exception.getMessage());
                    }
                });
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(post.getType().equals("event")){
                    Intent intent = new Intent(context, EventViewActivity.class);
                    intent.putExtra("eventName", post.getEventName());
                    intent.putExtra("hostId", post.getHostId());
                    intent.putExtra("location", post.getLocation());
                    intent.putExtra("peopleCount", post.getPeopleCount());
                    intent.putExtra("description", post.getDescription());
                    intent.putExtra("mainImgUrl", post.getImgUrl());
                    intent.putExtra("eventId", post.getPostId());
                    intent.putExtra("date", post.getEventDate());
                    context.startActivity(intent);
                }
                else if(post.getType().equals("quiz")){
                    if(post.getHostId().equals(HomeActivity.userId)){
                        context.startActivity(new Intent(context, RecentQuizActivity.class).putExtra("quizId",post.getPostId()));
                    }
                    else{
                        context.startActivity(new Intent(context,QuizView.class).putExtra("quizId",post.getPostId()).putExtra("hostId",post.getHostId()));                    }
                }
            }
        });

        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name = user.getDisplayName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(context, holder.menuImg);
                popup.inflate(R.menu.menu_main_item);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.see_people_who_are_going:
                                break;
                            case R.id.menu_bookmark:
                                break;
                            case R.id.menu_report:
                                Dialog dialog = onCreateDialog(post.getPostId());
                                dialog.show();
                                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                                int width = metrics.widthPixels;
                                int height = metrics.heightPixels;
                                dialog.getWindow().setLayout((6 * width) / 7, height * 2 / 3);
                                break;
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

        databaseReference.child("leaderboards").child(post.getPostId()).child(HomeActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    holder.playQuizBtn.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("posts").child(post.getPostId()).child("winnerPoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    holder.winnerPointsBox.setVisibility(View.VISIBLE);
                    holder.winnerPointsTv.setText("Winning Prize : INR " + dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(post.getReferenceLink()!=null){
            holder.referenceUrl.setText(post.getReferenceLink());
            holder.referenceUrl.setVisibility(View.VISIBLE);
        }
        else
            holder.referenceUrl.setVisibility(View.GONE);

    }
    private void like(Boolean liked,String id,int likeCounter, String hostId, String postText){

        if(liked != null && liked){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id)
                    .removeValue();
            databaseReference.child("posts").child(id).child("likeCount").setValue(likeCounter-1);
        }
        else{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id)
                    .setValue(true);
            databaseReference.child("posts").child(id).child("likeCount").setValue(likeCounter+1);
            if(!HomeActivity.userId.equals(hostId))
                addNotification(hostId,id,"like", postText);
        }
    }

    private void addNotification(String hostId, final String postId, final String type, String postText) {
        Calendar mCurrentTime = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = formatter.format(mCurrentTime.getTime());

        Notification notification = new Notification();
        notification.setDate(date);
        notification.setId(databaseReference.push().getKey());
        notification.setPostId(postId);
        notification.setType(type);
        if(type.equals("like")){
            notification.setText(name + " liked your post '" + postText + "'");
        }
        else if(type.equals("shared post")){
            notification.setText(name + " shared your post '" + postText + "'");
        }

        databaseReference.child("notifications").child(hostId).child(notification.getId()).setValue(notification);

    }

    private void bookmark(Boolean bookmarked, String id) {
        if (bookmarked != null && bookmarked) {
            databaseReference.child("bookmarks").child(HomeActivity.userId).child(id).removeValue();
            //Snackbar.make(layout, "Bookmark removed", Snackbar.LENGTH_SHORT).show();
        } else {
            databaseReference.child("bookmarks").child(HomeActivity.userId).child(id).setValue(true);
            //Snackbar.make(layout, "Bookmark added", Snackbar.LENGTH_SHORT).show();
        }
    }
    @Override
    public int getItemCount() {
        return dataSet.size() > 25 ? 25 : dataSet.size();
    }

}