package com.bbudzowski.homeautoandroid.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;

import com.bbudzowski.homeautoandroid.R;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.List;

public abstract class ListFragment<T> extends Fragment {
    public void updateUI(ConstraintLayout root, List<T> units) {
            if(units.size() == 0) {
                TextView emptyList = new TextView(root.getContext());
                emptyList.setText(R.string.no_results);
                emptyList.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                root.addView(emptyList);
                emptyList.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return;
            }
            int guideVertId = root.getChildAt(0).getId();
            int guideHorizId = root.getId();

            Class<?> unitClass = units.get(0).getClass();
            Field[] unitFields = unitClass.getFields();
            for(int i = 0; i < units.size(); ++i) {
                ConstraintLayout view = new ConstraintLayout(root.getContext());
                view.setBackgroundResource(R.drawable.layout_border);
                int prevTextId = view.getId();
                try {
                    for (Field unitField : unitFields) {
                        TextView textView = new TextView(view.getContext());
                        textView.setText((String) unitField.get(units.get(i)));
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        view.addView(textView);

                        ConstraintSet set = new ConstraintSet();
                        set.clone(view);
                        set.connect(textView.getId(), ConstraintSet.TOP, prevTextId, ConstraintSet.BOTTOM);
                        /*ConstraintLayout.LayoutParams textParams = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                        if(i == 0)
                            textParams.topToTop = prevTextId;
                        else
                            textParams.topToBottom = prevTextId;
                        textView.setLayoutParams(textParams);*/
                        prevTextId = textView.getId();
                    }
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                root.addView(view);
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
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
