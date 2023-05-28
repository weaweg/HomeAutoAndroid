package com.bbudzowski.homeautoandroid.ui.fragments;

import android.graphics.Typeface;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.bbudzowski.homeautoandroid.R;
import com.google.android.material.textfield.TextInputEditText;

public abstract class BasicFragment extends Fragment {

    protected void handleError(ConstraintLayout root, String text) {
        TextView emptyList = new TextView(root.getContext());
        emptyList.setId(View.generateViewId());
        emptyList.setText(text);
        Typeface typeface = Typeface.create("sans-serif-black", Typeface.BOLD);
        emptyList.setTypeface(typeface);
        emptyList.setTextSize(32f);
        emptyList.setTextColor(getResources().getColor(R.color.purple_500));
        emptyList.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        root.addView(emptyList);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 100;
        layoutParams.topToTop = LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        layoutParams.leftToLeft = LayoutParams.PARENT_ID;
        layoutParams.rightToRight = LayoutParams.PARENT_ID;
        emptyList.setLayoutParams(layoutParams);
    }

    protected int addTextView(ConstraintLayout view, String text, Float textSize, int color) {
        TextView textView = new TextView(view.getContext());
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText(text);
        Typeface typeface = Typeface.create("sans-serif-black", Typeface.BOLD);
        textView.setTypeface(typeface);
        textView.setTextSize(textSize);
        textView.setTextColor(view.getContext().getResources().getColor(color, null));
        textView.setId(View.generateViewId());
        view.addView(textView);
        textView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        return textView.getId();
    }

    protected int addEditTextView(ConstraintLayout view, String text, Float textSize, int color) {
        TextInputEditText editText = new TextInputEditText(new ContextThemeWrapper(view.getContext(),
                com.google.android.material.R.style.Widget_MaterialComponents_TextInputEditText_FilledBox));
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setText(text);
        Typeface typeface = Typeface.create("sans-serif-black", Typeface.BOLD);
        editText.setTypeface(typeface);
        editText.setTextSize(textSize);
        editText.setTextColor(view.getContext().getResources().getColor(color, null));
        editText.setId(View.generateViewId());
        view.addView(editText);
        editText.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        return editText.getId();
    }

    protected void addListView(ConstraintLayout view, String text, Float textSize, int color) {

    }

    protected void constraintTextToView(ConstraintLayout view, int margin) {
        ConstraintSet set = new ConstraintSet();
        set.clone(view);
        int childId = view.getChildAt(0).getId();
        set.connect(childId, ConstraintSet.TOP, view.getId(), ConstraintSet.TOP, 0);
        for(int i = 1; i < view.getChildCount(); ++i) {
            int nextChildId = view.getChildAt(i).getId();
            set.connect(nextChildId, ConstraintSet.TOP, childId, ConstraintSet.BOTTOM, margin);
            childId = nextChildId;
        }
        set.connect(childId, ConstraintSet.BOTTOM, view.getId(), ConstraintSet.BOTTOM, 30);
        set.applyTo(view);
    }
}
