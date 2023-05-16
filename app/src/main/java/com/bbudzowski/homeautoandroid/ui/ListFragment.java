package com.bbudzowski.homeautoandroid.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;

import com.bbudzowski.homeautoandroid.R;

import java.lang.reflect.Field;
import java.util.List;

public abstract class ListFragment<T> extends Fragment {
    public void updateUI(@NonNull LayoutInflater inflater, ViewGroup container,
                         ConstraintLayout root, List<T> units) {

            int guideVertId = root.getChildAt(0).getId();
            int guideHorizId = root.getId();
            Class<?> unitClass = units.get(0).getClass();
            for(int i = 0; i < units.size(); ++i) {
                ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.box_layout, container, false);
                TextView tmp;
                Field[] unitFields = unitClass.getFields();
                for(int j = 0; j < unitFields.length; ++j) {
                    tmp = new TextView(view.getContext());
                    tmp.setText(unitFields[i].toString());
                    view.addView(tmp);
                }
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
                layoutParams.matchConstraintPercentWidth = (float) 0.5;
                layoutParams.topToBottom = guideHorizId;
                if(i%2 == 0)
                    layoutParams.rightToLeft = guideVertId;
                else
                    layoutParams.leftToRight = guideVertId;
                view.setLayoutParams(layoutParams);

                Guideline guideHoriz = new Guideline(root.getContext());
                root.addView(guideHoriz);

                ConstraintLayout.LayoutParams guidelineParams =
                        new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                guidelineParams.orientation = ConstraintSet.HORIZONTAL;
                guidelineParams.topToBottom = view.getId();

                guideHoriz.setLayoutParams(guidelineParams);
                guideHorizId = guideHoriz.getId();
            }
    }
}
