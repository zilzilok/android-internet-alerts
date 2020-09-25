package ru.zilzilok.ict.utils.layouts;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import ru.zilzilok.ict.R;

/**
 * Class for progress button.
 */
public class ProgressButton {
    private View view;                          // Current view

    // Button components
    private CardView cardView;
    private ConstraintLayout constraintLayout;
    private LinearLayout linearLayout;
    private ImageView imageView;
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
        linearLayout = view.findViewById(R.id.progressButtonLinearLayout);
        imageView = view.findViewById(R.id.progressButtonImageView);
        progressBar = view.findViewById(R.id.progressButtonProgressBar);
        textView = view.findViewById(R.id.progressButtonTextView);
        fade_in = AnimationUtils.loadAnimation(context, R.anim.progress_button_fade_in);
    }

    public ProgressButton(Context context, View view, String firstText) {
        this(context, view);
        this.firstText = firstText;
        textView.setText(firstText);
    }

    public ProgressButton(Context context, View view, String firstText, int imageId) {
        this(context, view);
        this.firstText = firstText;
        textView.setText(firstText);
        imageView.setImageResource(imageId);
    }

    /**
     * Use to block progress button.
     */
    public void blockButton() {
        view.setEnabled(false);
    }

    /**
     * Use to unblock progress button.
     */
    public void unblockButton() {
        view.setEnabled(true);
    }

    /**
     * Use when progress button activated.
     */
    public void buttonActivated() {
        progressBar.setAnimation(fade_in);
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setAnimation(fade_in);
        textView.setText(context.getResources().getString(R.string.wait_loading));
    }

    /**
     * Use when progress button restored.
     */
    public void buttonRestored() {
        constraintLayout.setBackground(cardView.getContext().getDrawable(R.drawable.button_background));
        progressBar.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        textView.setText(firstText);
    }

    /**
     * Use when progress button failed.
     */
    public void buttonFailed() {
        constraintLayout.setBackgroundColor(cardView.getContext().getColor(R.color.red));
        progressBar.setVisibility(View.GONE);
        textView.setText("Error");
    }

    /**
     * Use when progress button finished.
     */
    public void buttonFinished() {
        constraintLayout.setBackgroundColor(cardView.getContext().getColor(R.color.green));
        progressBar.setVisibility(View.GONE);
        textView.setText("Finished");
    }
}