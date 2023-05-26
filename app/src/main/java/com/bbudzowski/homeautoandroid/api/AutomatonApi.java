package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.AutomatonEntity;
import com.fasterxml.jackson.databind.JavaType;

import java.sql.Timestamp;
import java.util.List;

import okhttp3.Response;

public abstract class AutomatonApi extends BaseApi{
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
        return (List<AutomatonEntity>) getResultList(res, listType);
    }

    public static AutomatonEntity getAutomaton(String name) {
        Response res = getResponse(base_url + "?name=" + name);
        return (AutomatonEntity) getSingleResult(res, type);
    }
}
