/**
 * @(#)${FILE_NAME}.java, 22/09/2016.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.sensor.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pw
 */
public enum NotificationType {

    PM25(1, "pm25");

    private int value;

    private String name;

    NotificationType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static NotificationType findByInt(int value) throws IllegalArgumentException {
        for (NotificationType item : NotificationType.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid NotificationType value: " + value);
    }

    @JsonCreator
    public static NotificationType findByString(String name) throws IllegalArgumentException {
        for (NotificationType item : NotificationType.values()) {
            if (item.name.equals(name)) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid NotificationType name: " + name);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name;
    }

    public int toInt() {
        return this.value;
    }
}

