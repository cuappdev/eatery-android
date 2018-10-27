package com.cornellappdev.android.eatery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

  private TextView mFeedbackText;
  private TextView mWebsiteText;
  private TextView mInfoText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_info);
    setTitle("About");

    mInfoText = findViewById(R.id.infoText);
    mFeedbackText = findViewById(R.id.feedbackText);
    mWebsiteText = findViewById(R.id.websiteText);

    mFeedbackText.setMovementMethod(LinkMovementMethod.getInstance());
    mWebsiteText.setMovementMethod(LinkMovementMethod.getInstance());

  }
}