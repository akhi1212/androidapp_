package com.theindiecorp.khelodapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.theindiecorp.khelodapp.R;

public class CarouselActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private int[] sampleImages = {R.drawable.carousel_first, R.drawable.carousel_second, R.drawable.carousel_third, R.drawable.carousel_fourth};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carousel);

        final Button continueBtn = findViewById(R.id.continue_btn);
        carouselView = findViewById(R.id.carouselView);

        SharedPreferences sh = getSharedPreferences("SharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();

        boolean firstTime = sh.getBoolean("firstTime", false);

        if(firstTime){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        else{
            editor.putBoolean("firstTime", true);
            editor.apply();
            editor.commit();
        }

        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(sampleImages[position]);
                    if(position == sampleImages.length - 1){
                        continueBtn.setText("Next");
                    }else{
                        continueBtn.setText("Get Started");
                    }
            }
        });

        carouselView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == sampleImages.length - 1){
                    continueBtn.setText("Get Started");
                }else{
                    continueBtn.setText("Next");
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(carouselView.getCurrentItem() == sampleImages.length - 1){
                    startActivity(new Intent(CarouselActivity.this, LoginActivity.class));
                }
                else{
                    carouselView.setCurrentItem(carouselView.getCurrentItem() + 1);
                }
            }
        });
    }
}
