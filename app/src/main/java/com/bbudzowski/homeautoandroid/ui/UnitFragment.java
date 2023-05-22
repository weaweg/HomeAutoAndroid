package com.bbudzowski.homeautoandroid.ui;

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

public abstract class UnitFragment<T> extends Fragment {
    public void updateUI(ConstraintLayout root, T unit) {
        if (unit == null) {
            handleError(root, getString(R.string.no_results));
            return;
        }

        Class<?> unitClass = unit.getClass();
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

        int nextConstraint = ConstraintSet.TOP;
        int prevTextId = root.getId();

        for (int j = 0; j < displayFields.size(); ++j) {
            TextView textView = new TextView(root.getContext());
            textView.setId(("tv" + j).hashCode());
            try {
                textView.setText(String.valueOf(displayFields.get(j).get(unit)));
            } catch (IllegalAccessException e) {
                handleError(root, e.getMessage());
                return;
            }
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            root.addView(textView);
            textView.setLayoutParams(new ConstraintLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            ConstraintSet set = new ConstraintSet();
            set.clone(root);
            set.connect(textView.getId(), ConstraintSet.TOP, prevTextId, nextConstraint);
            set.applyTo(root);
            nextConstraint = ConstraintSet.BOTTOM;
            prevTextId = textView.getId();
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
        fields.add(DeviceEntity.class.getDeclaredField("device_id"));
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
