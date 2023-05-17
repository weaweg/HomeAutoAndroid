package com.bbudzowski.homeautoandroid.ui;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.bbudzowski.homeautoandroid.R;

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
            int guideVertId = R.id.guideline;
            int prevViewId = root.getId();

            Class<?> unitClass = units.get(0).getClass();
            Field[] unitFields = unitClass.getFields();
            int constraint = ConstraintSet.TOP;
            for(int i = 0; i < units.size(); ++i) {
                ConstraintLayout view = new ConstraintLayout(root.getContext());
                view.setId(("cl" + i).hashCode());
                view.setBackgroundResource(R.drawable.layout_border);
                int prevTextId = view.getId();
                int nextConstraint = ConstraintSet.TOP;
                try {
                    for (int j = 0; j < unitFields.length; ++j) {
                        TextView textView = new TextView(view.getContext());
                        textView.setId(("tv" + j).hashCode());
                        textView.setText((CharSequence) unitFields[j].get(units.get(i)));
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        view.addView(textView);

                        textView.setLayoutParams(new LayoutParams(
                                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        ConstraintSet set = new ConstraintSet();
                        set.clone(view);
                        set.connect(textView.getId(), ConstraintSet.TOP, prevTextId, nextConstraint);
                        set.applyTo(view);
                        nextConstraint = ConstraintSet.BOTTOM;
                        prevTextId = textView.getId();
                    }
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                root.addView(view);

                view.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT));
                ConstraintSet set = new ConstraintSet();
                float px = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

                set.clone(root);
                set.connect(view.getId(), ConstraintSet.TOP, prevViewId, constraint, (int) px);
                set.constrainPercentWidth(view.getId(), (float) 0.4);
                if(i%2 == 0) {
                    set.center(view.getId(), ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0,
                            guideVertId, ConstraintSet.LEFT, 0, 2.f/3.f);
                }
                else {
                    set.center(view.getId(), guideVertId, ConstraintSet.RIGHT, 0,
                            ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0, 1.f/3.f);
                    prevViewId = view.getId();
                }

                set.applyTo(root);
                constraint = ConstraintSet.BOTTOM;
            }

    }
}
