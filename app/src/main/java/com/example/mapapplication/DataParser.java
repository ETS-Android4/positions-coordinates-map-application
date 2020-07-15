package com.example.mapapplication;

import com.example.mapapplication.data.Container;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataParser {

    private Container getSingleContainer(JSONObject jsonObject) throws JSONException {
        Container container = new Container();

        try {
            if (!jsonObject.isNull("id")) {
                container.id = (int) jsonObject.get("id");
            }
            if (!jsonObject.isNull("position")) {
                JSONArray jsonArray = (JSONArray) jsonObject.get("position");

                List<Double> list = new ArrayList<Double>();
                for (int i=0; i<jsonArray.length(); i++) {
                    list.add( jsonArray.getDouble(i));
                }

                container.position = list;
            }
            if (!jsonObject.isNull("collection")) {
                container.collection = (String) jsonObject.get("collection");
            }
            if (!jsonObject.isNull("fullness")) {
                container.fullness = (int) jsonObject.get("fullness");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return container;
    }

    private List<Container> getAllContainers(JSONArray jsonArray) {
        int counter = jsonArray.length();

        List<Container> containers = new ArrayList<>();

        Container container = null;

        for (int i = 0; i < counter; i++) {
            try {
                container = getSingleContainer((JSONObject) jsonArray.get(i));
                containers.add(container);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return containers;
    }

    public List<Container> parse(String JsonData) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(JsonData);

            JSONArray _jsonArray = jsonObject.names();

            List<JSONObject> ss= new ArrayList<>();

            for (int i = 0; i < _jsonArray.length(); i++) {
                ss.add(jsonObject.getJSONObject(_jsonArray.getString(i)));
            }

            JSONArray jsArray = new JSONArray(ss);
            jsonArray = jsArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllContainers(jsonArray);
    }
}
