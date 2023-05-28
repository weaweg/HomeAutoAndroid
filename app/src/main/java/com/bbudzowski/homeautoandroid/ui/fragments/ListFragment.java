package com.bbudzowski.homeautoandroid.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;

import java.sql.Timestamp;
import java.util.Timer;

public abstract class ListFragment extends BasicFragment {
    protected FragmentListBinding binding;
    protected Timer updateTimer;
    protected int updatePeriod = 2000;

    @Override
    public void onPause() {
        super.onPause();
        updateTimer.cancel();
    }

    protected void replaceFragment(int navId, Bundle bundle) {
        NavController navController =
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(navId, bundle);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
