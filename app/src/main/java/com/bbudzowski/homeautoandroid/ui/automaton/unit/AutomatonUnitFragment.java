package com.bbudzowski.homeautoandroid.ui.automaton.unit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.AutomatonApi;
import com.bbudzowski.homeautoandroid.databinding.FragmentUnitBinding;
import com.bbudzowski.homeautoandroid.tables.AutomatonEntity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.ui.fragments.BasicFragment;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public final class AutomatonUnitFragment extends BasicFragment {
    private final int[] valsId = new int[4];
    private final int[] linesId = new int[4];
    private FragmentUnitBinding binding;
    private AutomatonUnitViewModel model;
    private AutomatonEntity updatedAuto;
    private int editNameId;
    private boolean isNew;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        isNew = args.getBoolean("new");
        String name = args.getString("name");
        mainActivity = ((MainActivity) getActivity());
        binding = FragmentUnitBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        model = new AutomatonUnitViewModel(mainActivity, name, isNew);
        getViewModelStore().put("automatonUnit", model);
        final Observer<AutomatonEntity> automatonObserver = automaton ->
                createAutomatonView((ConstraintLayout) root.getViewById(R.id.unit_layout), automaton);
        model.getAutomaton().observe(getViewLifecycleOwner(), automatonObserver);
        root.getViewById(R.id.save_button).setOnClickListener((view) ->
                onSaveClick((ConstraintLayout) root.getViewById(R.id.unit_layout)));
        root.getViewById(R.id.delete_button).setOnClickListener((view) -> onDeleteClick());
        root.getViewById(R.id.delete_button).setVisibility(View.VISIBLE);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createAutomatonView(ConstraintLayout root, AutomatonEntity auto) {
        root.removeAllViews();
        editNameId = addEditTextView(root, auto.name, 48f, false);
        SensorEntity sens = auto.sens;
        int sensTextId = addTextView(root, sens.name + " - " + sens.device.location, 24f, R.color.teal_700);
        addSensorListOnClick(sensTextId, mainActivity.getSensors(), false);

        String unit = "";
        if (sens.json_desc != null)
            try {
                unit = sens.json_desc.getString("unit");
            } catch (JSONException ignored) {
            }

        ConstraintLayout sensLineTop = new ConstraintLayout(root.getContext());
        sensLineTop.setId(View.generateViewId());
        linesId[0] = sensLineTop.getId();
        addTextView(sensLineTop, "Górna granica: ", 24f);
        valsId[0] = addEditTextView(sensLineTop, auto.val_top.toString(), 24f, true);
        addTextView(sensLineTop, unit, 24f);
        constraintTextInLine(sensLineTop);
        root.addView(sensLineTop);

        ConstraintLayout sensLineBot = new ConstraintLayout(root.getContext());
        sensLineBot.setId(View.generateViewId());
        linesId[1] = sensLineBot.getId();
        addTextView(sensLineBot, "Dolna granica: ", 24f);
        valsId[1] = addEditTextView(sensLineBot, auto.val_bot.toString(), 24f, true);
        addTextView(sensLineBot, unit, 24f);
        constraintTextInLine(sensLineBot);
        root.addView(sensLineBot);

        SensorEntity acts = auto.acts;
        int actsTextId = addTextView(root, acts.name + " - " + acts.device.location, 24f, R.color.teal_700);
        addSensorListOnClick(actsTextId, mainActivity.getSensors(), true);

        String stateDesc = "ON";
        if (auto.state_up == 0)
            stateDesc = "OFF";
        ConstraintLayout actsLineTop = new ConstraintLayout(root.getContext());
        actsLineTop.setId(View.generateViewId());
        linesId[2] = actsLineTop.getId();
        addTextView(actsLineTop, "Stan powyżej: ", 24f);
        valsId[2] = addTextView(actsLineTop, stateDesc, 24f, R.color.teal_700);
        constraintTextInLine(actsLineTop);
        root.addView(actsLineTop);

        stateDesc = "ON";
        if (auto.state_down == 0)
            stateDesc = "OFF";
        ConstraintLayout actsLineBot = new ConstraintLayout(root.getContext());
        actsLineBot.setId(View.generateViewId());
        linesId[3] = actsLineBot.getId();
        addTextView(actsLineBot, "Stan poniżej: ", 24f);
        valsId[3] = addTextView(actsLineBot, stateDesc, 24f, R.color.teal_700);


        actsLineTop.getViewById(valsId[2]).setOnClickListener((view) -> {
            TextView textTop = (TextView) view;
            TextView textBot = (TextView) actsLineBot.getViewById(valsId[3]);
            if (textTop.getText().toString().equals("OFF")) {
                textTop.setText("ON");
                textBot.setText("OFF");
            } else if (textTop.getText().toString().equals("ON")) {
                textTop.setText("OFF");
                textBot.setText("ON");
            }
        });
        actsLineBot.getViewById(valsId[3]).setOnClickListener((view) -> {
            TextView textBot = (TextView) view;
            TextView textTop = (TextView) actsLineTop.getViewById(valsId[2]);
            if (textBot.getText().toString().equals("OFF")) {
                textBot.setText("ON");
                textTop.setText("OFF");
            } else if (textBot.getText().toString().equals("ON")) {
                textBot.setText("OFF");
                textTop.setText("ON");
            }
        });

        constraintTextInLine(actsLineBot);
        root.addView(actsLineBot);
        constraintTextToView(root, 0);
    }

    public void onSaveClick(ConstraintLayout view) {
        AutomatonEntity automaton = model.getAutomaton().getValue();
        EditText editText = (EditText) view.getViewById(editNameId);
        String nameText = editText.getText().toString();
        if (!nameText.equals(""))
            automaton.name = nameText;


        ConstraintLayout textLine = (ConstraintLayout) view.getViewById(linesId[0]);
        editText = (EditText) textLine.getViewById(valsId[0]);
        automaton.val_top = Float.parseFloat(editText.getText().toString());
        ;

        textLine = (ConstraintLayout) view.getViewById(linesId[1]);
        editText = (EditText) textLine.getViewById(valsId[1]);
        automaton.val_bot = Float.parseFloat(editText.getText().toString());

        textLine = (ConstraintLayout) view.getViewById(linesId[2]);
        String valText = ((TextView) textLine.getViewById(valsId[2])).getText().toString();
        if (valText.equals("OFF"))
            automaton.state_up = 0;
        else if (valText.equals("ON"))
            automaton.state_up = 1;

        textLine = (ConstraintLayout) view.getViewById(linesId[3]);
        valText = ((TextView) textLine.getViewById(valsId[3])).getText().toString();
        if (valText.equals("OFF"))
            automaton.state_down = 0;
        else if (valText.equals("ON"))
            automaton.state_down = 1;
        if (isNew) {
            if (AutomatonApi.addAutomaton(automaton) == 201) {
                Snackbar.make(binding.getRoot(), "Dodano urządzenie", Snackbar.LENGTH_SHORT).show();
                previousFragment();
                return;
            }
            Snackbar.make(binding.getRoot(), "Aktualizacja nieudana", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (AutomatonApi.updateAutomaton(automaton) == 200) {
            Snackbar.make(binding.getRoot(), "Zaktualizowano urządzenie", Snackbar.LENGTH_SHORT).show();
            previousFragment();
            return;
        }
        Snackbar.make(binding.getRoot(), "Aktualizacja nieudana", Snackbar.LENGTH_SHORT).show();
    }

    public void onDeleteClick() {
        AutomatonEntity automaton = model.getAutomaton().getValue();
        if (isNew) {
            previousFragment();
            return;
        }
        if (AutomatonApi.deleteAutomaton(automaton.name) == 200) {
            Snackbar.make(binding.getRoot(), "Usunięto urządzenie", Snackbar.LENGTH_SHORT).show();
            previousFragment();
            return;
        }
        Snackbar.make(binding.getRoot(), "Aktualizacja nieudana", Snackbar.LENGTH_SHORT).show();
    }

    private void addSensorListOnClick(int listTextId, List<SensorEntity> sensTmp, boolean isDiscrete) {
        List<SensorEntity> sensors = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Wybierz czujnik");
        builder.setCancelable(true);
        List<String> names = new ArrayList<>();
        for (SensorEntity sens : sensTmp)
            if (isDiscrete == sens.discrete) {
                sensors.add(sens);
                names.add(sens.name + " - " + sens.device.location);
            }
        builder.setItems(names.toArray(new CharSequence[0]), (dialog, which) -> {
            updatedAuto = model.getAutomaton().getValue();
            SensorEntity sens = sensors.get(which);
            if (isDiscrete) {
                updatedAuto.device_id_acts = sens.device_id;
                updatedAuto.sensor_id_acts = sens.sensor_id;
                updatedAuto.acts = mainActivity.getSensor(sens.device_id, sens.sensor_id);
            } else {
                updatedAuto.device_id_sens = sens.device_id;
                updatedAuto.sensor_id_sens = sens.sensor_id;
                updatedAuto.sens = mainActivity.getSensor(sens.device_id, sens.sensor_id);
            }
            model.getAutomaton().setValue(updatedAuto);
        });
        AlertDialog dialog = builder.create();
        ConstraintLayout view = (ConstraintLayout) binding.getRoot().getViewById(R.id.unit_layout);
        view.getViewById(listTextId).setOnClickListener((v) -> dialog.show());
    }


}
