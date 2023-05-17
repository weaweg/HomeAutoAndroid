package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.fasterxml.jackson.jr.ob.JSON;

import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeviceApi implements BaseApi {
    private final OkHttpClient client = new OkHttpClient();
    private final String url = BaseApi.url + "/devices";

    public List<DeviceEntity> getDevices() {
        /*Request request = new Request.Builder().get().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            return JSON.std.listOfFrom(Device.class, response.body().string());
        }
        catch (Exception e) {
            return null;
        }*/
        List<DeviceEntity> devices = new ArrayList<>();
        for(int i = 0; i < 4; ++i) {
            DeviceEntity dev = new DeviceEntity();
            dev.id = "id " + i;
            dev.name = "name " + i;
            dev.location = "loc " + i;
            devices.add(dev);
        }
        return devices;
    }

    public Response renameDevice(String name) throws IOException {
        RequestBody body = RequestBody.create(JSON.std.asString(name), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().patch(body).url(url + "/rename").build();
        try (Response response = client.newCall(request).execute()) {
            return response;
        }
        catch (Exception e) {
            return null;
        }
    }

    public Response changeLocation(String loc) throws IOException {
        try {
            RequestBody body = RequestBody.create(JSON.std.asString(loc), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().patch(body).url(url + "/location").build();
            try (Response response = client.newCall(request).execute()) {
                return response;
            }
        }
        catch (Exception e) {
            return null;
        }
    }
}
