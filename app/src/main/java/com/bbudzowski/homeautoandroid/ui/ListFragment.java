package com.bbudzowski.homeautoandroid.ui;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bbudzowski.homeautoandroid.R;

public abstract class ListFragment extends Fragment {

    protected void handleError(ConstraintLayout root, String text) {
        TextView emptyList = new TextView(root.getContext());
        emptyList.setText(text);
        emptyList.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        root.addView(emptyList);
        emptyList.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        set.clone(root);
        int guideVertId = R.id.guideline;
        int nextConstraint = ConstraintSet.TOP;
        int childId = root.getId();
        for(int i = 0; i < root.getChildCount(); ++i) {
            int nextChildId = root.getChildAt(i).getId();
            set.connect(nextChildId, ConstraintSet.TOP, childId, nextConstraint, (int) px);
            set.constrainPercentWidth(nextChildId, .4f);
            if (i % 2 != 0) {
                set.center(nextChildId, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0,
                        guideVertId, ConstraintSet.LEFT, 0, 2 / 3f);
            } else {
                set.center(nextChildId, guideVertId, ConstraintSet.RIGHT, 0,
                        ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0, 1 / 3f);
                childId = nextChildId;
                nextConstraint = ConstraintSet.BOTTOM;
            }
        }
        set.applyTo(root);
    }

    protected void addTextView(ConstraintLayout view, String text, String id) {
        TextView textView = new TextView(view.getContext());
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText(text);
        textView.setId(id.hashCode());
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
