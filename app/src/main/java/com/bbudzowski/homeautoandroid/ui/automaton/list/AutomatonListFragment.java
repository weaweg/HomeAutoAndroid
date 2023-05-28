package com.bbudzowski.homeautoandroid.ui.automaton.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.SensorApi;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.AutomatonEntity;
import com.bbudzowski.homeautoandroid.ui.device.list.DeviceListViewModel;
import com.bbudzowski.homeautoandroid.ui.fragments.ListFragment;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutomatonListFragment extends ListFragment {
    private MainActivity mainActivity;
    private AutomatonListViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainActivity = ((MainActivity) getActivity());
        binding = FragmentListBinding.inflate(inflater, container, false);
        ScrollView root = binding.getRoot();
        model = new AutomatonListViewModel(mainActivity);
        getViewModelStore().put("automatonList", model);
        final Observer<List<AutomatonEntity>> automatonObserver = automatons -> {
            createAutomatonsUi(root.findViewById(R.id.units_list), automatons);
        };
        model.getAutomatons().observe(getViewLifecycleOwner(), automatonObserver);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Timestamp updateTime = mainActivity.getLastUpdateTime();
                if(updateTime.compareTo(model.getLastUpdateTime()) > 0) {
                    model.getAutomatons().postValue(mainActivity.getAutomatons());
                    model.setLastUpdateTime(updateTime);
                }
            }
        }, 0, updatePeriod);
    }

    private void createAutomatonsUi(ConstraintLayout root, List<AutomatonEntity> automatons) {
        root.removeAllViews();
        if (automatons == null || automatons.size() == 0) {
            handleError(root, getString(R.string.no_results));
            return;
        }
        for (int i = 0; i < automatons.size(); ++i) {
            ConstraintLayout view = createAutomatonView(root, automatons.get(i));
            view.setId(View.generateViewId());
            root.addView(view);
            view.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT));
        }
        constraintViewsToRoot(root);
    }

    private ConstraintLayout createAutomatonView(ConstraintLayout root, AutomatonEntity automaton) {
        ConstraintLayout view = new ConstraintLayout(root.getContext());
        view.setBackgroundResource(R.drawable.layout_border);

        addEditTextView(view, automaton.name, 24f, R.color.purple_500);
        addTextView(view, automaton.sens.name + " - " + automaton.sens.device.location,
                20f, R.color.purple_500);

        ConstraintLayout line = new ConstraintLayout(root.getContext());
        line.setId(View.generateViewId());
        int valTopId = addEditTextView(line, automaton.val_top.toString(),  20f, R.color.purple_500);
        int ValBotId = addEditTextView(line, automaton.val_bot.toString(),  20f, R.color.purple_500);
        ConstraintSet set = new ConstraintSet();
        set.clone(line);
        set.connect(ValBotId, ConstraintSet.LEFT, line.getId(), ConstraintSet.LEFT);
        set.connect(ValBotId, ConstraintSet.RIGHT, valTopId, ConstraintSet.LEFT);
        set.connect(valTopId, ConstraintSet.RIGHT, line.getId(), ConstraintSet.RIGHT);
        set.connect(valTopId, ConstraintSet.LEFT, ValBotId, ConstraintSet.RIGHT);
        set.applyTo(line);
        view.addView(line);
        line.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


        constraintTextToView(view, 0);
        view.setOnClickListener(onAutomatonClick(automaton.name));
        return view;
    }

    private View.OnClickListener onAutomatonClick(String name) {
        return view -> {
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            //replaceFragment(new AutomatonUnitFragment(), bundle);
        };
    }
}