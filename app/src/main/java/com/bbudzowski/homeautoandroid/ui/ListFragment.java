package com.bbudzowski.homeautoandroid.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;

import java.sql.Timestamp;
import java.util.Timer;

public abstract class ListFragment extends Fragment {
    protected FragmentListBinding binding;
    protected Timer updateTimer;
    protected int updatePeriod = 2000;

    private Timestamp devicesLastUpdate = null;
    private Timestamp sensorsLastUpdate = null;
    private Timestamp automatonsLastUpdate = null;

    @Override
    public void onPause() {
        super.onPause();
        updateTimer.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void handleError(ConstraintLayout root, String text) {
        TextView emptyList = new TextView(root.getContext());
        emptyList.setId(View.generateViewId());
        emptyList.setText(text);
        Typeface typeface = Typeface.create("sans-serif-black", Typeface.BOLD);
        emptyList.setTypeface(typeface);
        emptyList.setTextSize(24f);
        emptyList.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        root.addView(emptyList);
        emptyList.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ConstraintSet cs = new ConstraintSet();
        cs.clone(root);
        cs.center(emptyList.getId(), ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0,
                ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0, .5f);
    }

    protected void replaceFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.nav_host_fragment_content_main, fragment, "DLF");
        fragmentTransaction.addToBackStack("DLF");
        fragmentTransaction.commit();
    }

    protected void constraintViewsToRoot(ConstraintLayout root) {
        ConstraintSet set = new ConstraintSet();
        set.clone(root);
        int childId = root.getChildAt(0).getId();
        set.connect(childId, ConstraintSet.TOP, root.getId(), ConstraintSet.TOP, 50);
        set.center(childId, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0,
                ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0, .5f);
        set.constrainPercentWidth(childId, .8f);
        for(int i = 1; i < root.getChildCount(); ++i) {
            int nextChildId = root.getChildAt(i).getId();
            set.connect(nextChildId, ConstraintSet.TOP, childId, ConstraintSet.BOTTOM, 50);
            set.center(nextChildId, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0,
                    ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0, .5f);
            set.constrainPercentWidth(nextChildId, .8f);
            childId = nextChildId;
        }
        set.applyTo(root);
    }

    protected void addTextView(ConstraintLayout view, String text, Float textSize, int color) {
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
    }

    protected void constraintTextToView(ConstraintLayout view) {
        int childId = view.getChildAt(0).getId();
        ConstraintSet set = new ConstraintSet();
        set.clone(view);
        set.connect(childId, ConstraintSet.TOP, view.getId(), ConstraintSet.TOP);
        for(int i = 1; i < view.getChildCount(); ++i) {
            int nextChildId = view.getChildAt(i).getId();
            set.connect(nextChildId, ConstraintSet.TOP, childId, ConstraintSet.BOTTOM);
            childId = nextChildId;
        }
        set.applyTo(view);
    }
}
