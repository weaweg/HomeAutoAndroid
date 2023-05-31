package com.bbudzowski.homeautoandroid.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.ui.MainActivity;

import java.util.Timer;

public abstract class BasicFragment extends Fragment {
    protected MainActivity mainActivity;
    protected Timer updateTimer;
    protected int updatePeriod = 2000;

    protected void replaceFragment(int navId, Bundle bundle) {
        NavController navController =
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(navId, bundle);
    }

    protected void previousFragment() {
        NavController navController =
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigateUp();
    }

    protected void handleError(ConstraintLayout root, String text) {
        TextView emptyList = new TextView(root.getContext());
        emptyList.setId(View.generateViewId());
        emptyList.setText(text);
        Typeface typeface = Typeface.create("sans-serif-black", Typeface.BOLD);
        emptyList.setTypeface(typeface);
        emptyList.setTextSize(32f);
        emptyList.setTextColor(getResources().getColor(R.color.purple_500, null));
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
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        return textView.getId();
    }

    protected int addTextView(ConstraintLayout view, String text, Float textSize) {
        return addTextView(view, text, textSize, R.color.purple_500);
    }

    protected int addEditTextView(ConstraintLayout view, String text, Float textSize, int color, boolean isNumeric) {
        EditText editText = new EditText(view.getContext());
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if (isNumeric)
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText(text);
        Typeface typeface = Typeface.create("sans-serif-black", Typeface.BOLD);
        editText.setTypeface(typeface);
        editText.setTextSize(textSize);
        editText.setTextColor(view.getContext().getResources().getColor(color, null));
        editText.setId(View.generateViewId());
        editText.setBackground(null);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        view.addView(editText);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(layoutParams);
        return editText.getId();
    }

    protected int addEditTextView(ConstraintLayout view, String text, Float textSize, boolean isNumeric) {
        return addEditTextView(view, text, textSize, R.color.teal_700, isNumeric);
    }

    protected void constraintTextInLine(ConstraintLayout view) {
        ConstraintSet set = new ConstraintSet();
        set.clone(view);
        int childId = view.getChildAt(0).getId();
        set.connect(childId, ConstraintSet.LEFT, view.getId(), ConstraintSet.LEFT);
        set.centerVertically(childId, ConstraintSet.PARENT_ID);
        for (int i = 1; i < view.getChildCount(); ++i) {
            int nextChildId = view.getChildAt(i).getId();
            set.connect(nextChildId, ConstraintSet.LEFT, childId, ConstraintSet.RIGHT);
            set.centerVertically(nextChildId, ConstraintSet.PARENT_ID);
            childId = nextChildId;
        }
        set.connect(childId, ConstraintSet.RIGHT, view.getId(), ConstraintSet.RIGHT);
        set.applyTo(view);
    }

    protected void constraintTextToView(ConstraintLayout view, int margin) {
        ConstraintSet set = new ConstraintSet();
        set.clone(view);
        int childId = view.getChildAt(0).getId();
        set.connect(childId, ConstraintSet.TOP, view.getId(), ConstraintSet.TOP);
        set.centerHorizontally(childId, ConstraintSet.PARENT_ID);
        for (int i = 1; i < view.getChildCount(); ++i) {
            int nextChildId = view.getChildAt(i).getId();
            set.connect(nextChildId, ConstraintSet.TOP, childId, ConstraintSet.BOTTOM, margin);
            set.centerHorizontally(nextChildId, ConstraintSet.PARENT_ID);
            childId = nextChildId;
        }
        set.connect(childId, ConstraintSet.BOTTOM, view.getId(), ConstraintSet.BOTTOM);
        set.applyTo(view);
    }

    protected void constraintViewsToRoot(ConstraintLayout root) {
        ConstraintSet set = new ConstraintSet();
        set.clone(root);
        int childId = root.getChildAt(0).getId();
        set.connect(childId, ConstraintSet.TOP, root.getId(), ConstraintSet.TOP, 50);
        set.centerHorizontally(childId, ConstraintSet.PARENT_ID);
        set.constrainPercentWidth(childId, .8f);
        for (int i = 1; i < root.getChildCount(); ++i) {
            int nextChildId = root.getChildAt(i).getId();
            set.connect(nextChildId, ConstraintSet.TOP, childId, ConstraintSet.BOTTOM, 50);
            set.centerHorizontally(nextChildId, ConstraintSet.PARENT_ID);
            set.constrainPercentWidth(nextChildId, .8f);
            childId = nextChildId;
        }
        set.applyTo(root);
    }
}
