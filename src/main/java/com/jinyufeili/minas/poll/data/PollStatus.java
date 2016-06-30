/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pw
 */
public enum PollStatus {

    INIT(0, "init"),

    PUBLISHED(1, "published"),

    FINISHED(2, "finished"),

    CANCELLED(-1, "cancelled");

    private int value;

    private String name;

    PollStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PollStatus findByInt(int value) throws IllegalArgumentException {
        for (PollStatus item : PollStatus.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid PollStatus value: " + value);
    }

    @JsonCreator
    public static PollStatus findByString(String name) throws IllegalArgumentException {
        for (PollStatus item : PollStatus.values()) {
            if (item.name.equals(name)) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid PollStatus name: " + name);
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

