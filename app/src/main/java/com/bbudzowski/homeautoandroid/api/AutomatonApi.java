package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.AutomatonEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public abstract class AutomatonApi extends BaseApi {
    private static final String base_url = host + "/automaton";
    private static final JavaType listType = mapper.getTypeFactory().
            constructCollectionType(List.class, AutomatonEntity.class);
    private static final JavaType type = mapper.constructType(AutomatonEntity.class);

    public static Timestamp getUpdateTime() {
        Response res = getResponse(base_url + "/update_time");
        return getUpdateTime(res);
    }

    public static List<AutomatonEntity> getAutomatons() {
        Response res = getResponse(base_url + "/all");
        List<AutomatonEntity> automatons = (List<AutomatonEntity>) getResultList(res, listType);
        return automatons == null ? new ArrayList<>() : automatons;
    }

    public static AutomatonEntity getAutomaton(String name) {
        Response res = getResponse(base_url + "?name=" + name);
        return (AutomatonEntity) getSingleResult(res, type);
    }

    public static int addAutomaton(AutomatonEntity automaton) {
        ObjectNode json = mapper.createObjectNode();
        json.put("name", automaton.name);
        json.put("device_id_sens", automaton.device_id_sens);
        json.put("sensor_id_sens", automaton.sensor_id_sens);
        json.put("val_top", automaton.val_top);
        json.put("val_bot", automaton.val_bot);
        json.put("device_id_acts", automaton.device_id_acts);
        json.put("sensor_id_acts", automaton.sensor_id_acts);
        json.put("state_up", automaton.state_up);
        json.put("state_down", automaton.state_down);
        String bodyString;
        try {
            bodyString = mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            return -1;
        }
        try (Response res = postResponse(base_url + "/add", bodyString)) {
            return res.code();
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public static int updateAutomaton(AutomatonEntity automaton) {
        ObjectNode json = mapper.createObjectNode();
        json.put("name", automaton.name);
        json.put("device_id_sens", automaton.device_id_sens);
        json.put("sensor_id_sens", automaton.sensor_id_sens);
        json.put("val_top", automaton.val_top);
        json.put("val_bot", automaton.val_bot);
        json.put("device_id_acts", automaton.device_id_acts);
        json.put("sensor_id_acts", automaton.sensor_id_acts);
        json.put("state_up", automaton.state_up);
        json.put("state_down", automaton.state_down);
        String bodyString;
        try {
            bodyString = mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            return -1;
        }
        try (Response res = putResponse(base_url + "/update", bodyString)) {
            return res.code();
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public static int deleteAutomaton(String name) {
        try (Response res = deleteResponse(base_url + "/delete?name=" + name)) {
            return res.code();
        } catch (NullPointerException e) {
            return -1;
        }
    }


}
