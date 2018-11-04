package com.cornellappdev.android.eatery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.util.StringUtil;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

  private TextView mFeedbackText;
  private TextView mWebsiteText;
  private BottomNavigationView mBottomNavigationBar;
  public  ArrayList<EateryBaseModel> cafeList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_info);
    setTitle("About");

    cafeList = (ArrayList<EateryBaseModel>) getIntent().getSerializableExtra("cafeData");

    mFeedbackText = findViewById(R.id.feedbackText);
    mWebsiteText = findViewById(R.id.websiteText);
    mBottomNavigationBar = findViewById(R.id.bottom_navigation);
    mBottomNavigationBar.setSelectedItemId(R.id.action_brb);

    Spannable mWebsiteSpannable = Spannable.Factory.getInstance().newSpannable(mWebsiteText.getText());
    if (mWebsiteSpannable != null) {
      StringUtil.removeUnderlines(mWebsiteSpannable);
    }

    Spannable mFeedbackSpannable = Spannable.Factory.getInstance().newSpannable(mFeedbackText.getText());
    if (mFeedbackSpannable != null) {
      StringUtil.removeUnderlines(mFeedbackSpannable);
    }

    mBottomNavigationBar.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
              case R.id.action_home:
                finish();
                break;
              case R.id.action_week:
                finish();
                intent = new Intent(getApplicationContext(), WeeklyMenuActivity.class);
                intent.putExtra("cafeData", cafeList);
                startActivity(intent);
                break;
              case R.id.action_brb:
                ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
                sv.smoothScrollTo(0, 0);
                break;
            }
            return true;
          }
        });

    mFeedbackText.setMovementMethod(LinkMovementMethod.getInstance());
    mWebsiteText.setMovementMethod(LinkMovementMethod.getInstance());

    mWebsiteText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Uri uri = Uri.parse("https://www.cornellappdev.com/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
      }
    });

    mFeedbackText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Uri uri = Uri.parse("https://goo.gl/forms/g8v6CbN2lrQtY4tH2");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
      }
    });
  }
}