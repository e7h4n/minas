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
public enum AnswerResult {

    // 未参与
    ABSTENTION(0, "abstention"),

    POSITIVE(1, "positive"),

    NEGATIVE(-1, "negative"),

    // 弃权
    RENUNCIATION(-2, "renunciation");

    private int value;

    private String name;

    AnswerResult(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AnswerResult findByInt(int value) throws IllegalArgumentException {
        for (AnswerResult item : AnswerResult.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid AnswerResult value: " + value);
    }

    @JsonCreator
    public static AnswerResult findByString(String name) throws IllegalArgumentException {
        for (AnswerResult item : AnswerResult.values()) {
            if (item.name.equals(name)) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid AnswerResult name: " + name);
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

