package com.bbudzowski.homeautoandroid.ui.automaton.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.lifecycle.Observer;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.AutomatonEntity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.ui.fragments.BasicFragment;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public final class AutomatonListFragment extends BasicFragment {
    private FragmentListBinding binding;
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
        mainActivity.getBinding().appBarMain.fab.show();
        mainActivity.getBinding().appBarMain.fab.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("new", true);
            replaceFragment(R.id.nav_automaton, bundle);
        });
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Timestamp updateTime = mainActivity.getAutomatonsLastUpdate();
                if(updateTime.compareTo(model.getLastUpdateTime()) > 0) {
                    model.getAutomatons().postValue(mainActivity.getAutomatons());
                    model.setLastUpdateTime(updateTime);
                }
            }
        }, 0, updatePeriod);
    }

    @Override
    public void onPause() {
        super.onPause();
        updateTimer.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainActivity.getBinding().appBarMain.fab.hide();
        binding = null;
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

        addTextView(view, automaton.name, 32f, R.color.purple_700);

        SensorEntity sens = automaton.sens;
        String unit = "";
        if(sens.json_desc != null)
            try { unit = sens.json_desc.getString("unit");
            } catch (JSONException e) { unit = ""; }
        ConstraintLayout textLine = new ConstraintLayout(view.getContext());
        textLine.setId(View.generateViewId());
        addTextView(textLine, "Górna: ", 20f);
        addTextView(textLine, automaton.val_top.toString() + unit,  20f);
        addTextView(textLine, " - ", 20f);
        addTextView(textLine, "Dolna: ", 20f);
        addTextView(textLine, automaton.val_bot.toString() + unit,  20f);
        constraintTextInLine(textLine);
        view.addView(textLine);
        textLine.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        textLine = new ConstraintLayout(view.getContext());
        textLine.setId(View.generateViewId());
        String stateDesc = "ON";
        if (automaton.state_up == 0)
            stateDesc = "OFF";
        addTextView(textLine, stateDesc,  20f);
        addTextView(textLine, " - ", 20f);
        stateDesc = "ON";
        if (automaton.state_down == 0)
            stateDesc = "OFF";
        addTextView(textLine, stateDesc, 20f);
        constraintTextInLine(textLine);
        view.addView(textLine);
        textLine.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        constraintTextToView(view, 0);
        view.setOnClickListener(onAutomatonClick(automaton.name));
        return view;
    }

    private View.OnClickListener onAutomatonClick(String name) {
        return view -> {
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putBoolean("new", false);
            replaceFragment(R.id.nav_automaton, bundle);
        };
    }
}