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
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class ListFragment<T> extends Fragment {
    public void updateUI(ConstraintLayout root, List<T> units) {
        if (units == null || units.size() == 0) {
            handleError(root, getString(R.string.no_results));
            return;
        }

        int constraint = ConstraintSet.TOP;
        int guideVertId = R.id.guideline;
        int prevViewId = root.getId();

        Class<?> unitClass = units.get(0).getClass();
        List<Field> displayFields;
        try {
            if (unitClass.equals(DeviceEntity.class))
                displayFields = getDeviceDisplayFields();
            else if(unitClass.equals(SensorEntity.class))
                displayFields = getSensorDisplayFields();
            else
                throw new ClassCastException();
        } catch (NoSuchFieldException e) {
            handleError(root, e.getMessage());
            return;
        }

        for (int i = 0; i < units.size(); ++i) {
            ConstraintLayout view = new ConstraintLayout(root.getContext());
            view.setId(("cl" + i).hashCode());
            view.setBackgroundResource(R.drawable.layout_border);

            int prevTextId = view.getId();
            int nextConstraint = ConstraintSet.TOP;

            for (int j = 0; j < displayFields.size(); ++j) {
                TextView textView = new TextView(view.getContext());
                textView.setId(("tv" + j).hashCode());
                try {
                    textView.setText(String.valueOf(displayFields.get(j).get(units.get(i))));
                } catch (IllegalAccessException e) {
                    handleError(root, e.getMessage());
                    return;
                }
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
            root.addView(view);
            view.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT));
            ConstraintSet set = new ConstraintSet();
            float px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
            set.clone(root);
            set.connect(view.getId(), ConstraintSet.TOP, prevViewId, constraint, (int) px);
            set.constrainPercentWidth(view.getId(), .4f);
            if (i % 2 == 0) {
                set.center(view.getId(), ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0,
                        guideVertId, ConstraintSet.LEFT, 0, 2 / 3f);
            } else {
                set.center(view.getId(), guideVertId, ConstraintSet.RIGHT, 0,
                        ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0, 1 / 3f);
                prevViewId = view.getId();
            }

            set.applyTo(root);
            constraint = ConstraintSet.BOTTOM;
        }
    }

    private void handleError(ConstraintLayout root, String text) {
        TextView emptyList = new TextView(root.getContext());
        emptyList.setText(text);
        emptyList.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        root.addView(emptyList);
        emptyList.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private List<Field> getDeviceDisplayFields() throws NoSuchFieldException {
        List<Field> fields = new ArrayList<>();
        fields.add(DeviceEntity.class.getDeclaredField("name"));
        fields.add(DeviceEntity.class.getDeclaredField("location"));
        return fields;
    }

    private List<Field> getSensorDisplayFields() throws NoSuchFieldException {
        List<Field> fields = new ArrayList<>();
        fields.add(SensorEntity.class.getDeclaredField("device_id"));
        fields.add(SensorEntity.class.getDeclaredField("sensor_id"));
        fields.add(SensorEntity.class.getDeclaredField("current_state"));
        fields.add(SensorEntity.class.getDeclaredField("units"));
        return fields;
    }
}
