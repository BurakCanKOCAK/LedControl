package com.bkocak.ledcontrol.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Building {
    private String name = "";
    private String id = "";
    private String type = "";
    private int unitCount;
    private Map<Integer, List<Integer>> ledMap = new HashMap<>();
    private Map<Integer, Boolean> onSaleMap = new HashMap<>();

    public Building() {
    }

    public Building(String name, String id, String type, int unitCount) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.unitCount = unitCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(int unitCount) {
        this.unitCount = unitCount;
    }

    public Map<Integer, List<Integer>> getLedMap() {
        return ledMap;
    }

    public void setLedMap(Map<Integer, List<Integer>> ledMap) {
        this.ledMap = ledMap;
    }

    public Map<Integer, Boolean> getOnSaleMap() {
        return onSaleMap;
    }

    public void setOnSaleMap(Map<Integer, Boolean> onSaleMap) {
        this.onSaleMap = onSaleMap;
    }
}
