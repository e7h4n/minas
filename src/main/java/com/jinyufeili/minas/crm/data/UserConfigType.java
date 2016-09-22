/**
 * @(#)${FILE_NAME}.java, 22/09/2016.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.crm.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pw
 */
public enum UserConfigType {

    PM25_NOTIFICATION(1, "pm25Notification");

    private int value;

    private String name;

    UserConfigType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static UserConfigType findByInt(int value) throws IllegalArgumentException {
        for (UserConfigType item : UserConfigType.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid UserConfigType value: " + value);
    }

    @JsonCreator
    public static UserConfigType findByString(String name) throws IllegalArgumentException {
        for (UserConfigType item : UserConfigType.values()) {
            if (item.name.equals(name)) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid UserConfigType name: " + name);
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

