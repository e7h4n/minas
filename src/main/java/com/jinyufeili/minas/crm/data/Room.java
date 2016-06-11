package com.jinyufeili.minas.crm.data;

/**
 * Created by pw on 6/9/16.
 */
public class Room {

    private int id;

    private int region;

    private int building;

    private int unit;

    private int houseNumber;

    private float roomArea;

    private float parkingSpaceArea;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public int getBuilding() {
        return building;
    }

    public void setBuilding(int building) {
        this.building = building;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public float getRoomArea() {
        return roomArea;
    }

    public void setRoomArea(float roomArea) {
        this.roomArea = roomArea;
    }

    public float getParkingSpaceArea() {
        return parkingSpaceArea;
    }

    public void setParkingSpaceArea(float parkingSpaceArea) {
        this.parkingSpaceArea = parkingSpaceArea;
    }
}
