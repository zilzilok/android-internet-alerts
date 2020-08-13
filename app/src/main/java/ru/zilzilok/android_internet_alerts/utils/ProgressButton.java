package ru.zilzilok.android_internet_alerts.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import ru.zilzilok.android_internet_alerts.R;

public class ProgressButton {
    private View view;
    private CardView cardView;
    private ConstraintLayout constraintLayout;
    private ProgressBar progressBar;
    private TextView textView;
    private Animation fade_in;
    private String firstText;
    private Context context;

    public ProgressButton(Context context, View view) {
        this.context = context;
        this.view = view;
        cardView = view.findViewById(R.id.progressButtonCardView);
        constraintLayout = view.findViewById(R.id.progressButtonConstraintLayout);
        progressBar = view.findViewById(R.id.progressButtonProgressBar);
        textView = view.findViewById(R.id.progressButtonTextView);
        fade_in = AnimationUtils.loadAnimation(context, R.anim.progress_button_fade_in);
    }

    public ProgressButton(Context context, View view, String firstText) {
        this(context, view);
        this.firstText = firstText;
        textView.setText(firstText);
    }

    public void blockButton() {
        view.setEnabled(false);
    }

    public void unblockButton() {
        view.setEnabled(true);
    }

    public void buttonActivated() {
        progressBar.setAnimation(fade_in);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setAnimation(fade_in);
        textView.setText(context.getResources().getString(R.string.wait_loading));
    }

    public void buttonRestored() {
        constraintLayout.setBackground(cardView.getContext().getDrawable(R.drawable.button_background));
        progressBar.setVisibility(View.GONE);
        textView.setText(firstText);
    }

    public void buttonFailed() {
        constraintLayout.setBackgroundColor(cardView.getContext().getColor(R.color.red));
        progressBar.setVisibility(View.GONE);
        textView.setText("Error");
    }

    public void buttonFinished() {
        constraintLayout.setBackgroundColor(cardView.getContext().getColor(R.color.green));
        progressBar.setVisibility(View.GONE);
        textView.setText("Finished");
    }
}