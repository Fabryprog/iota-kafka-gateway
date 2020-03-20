package org.fabryprog.iota.kafka.pojo;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;

import java.io.Serializable;

public class Transaction implements Serializable, SpecificRecord {
    public static final String AVRO_SCHEMA = "{ " +
            "\"type\": \"record\", " +
            "\"namespace\": \"org.fabryprog.iota.kafka.pojo\", " +
            "\"name\": \"Transaction\", " +
            "\"version\": \"1\", " +
            "\"fields\": [ " +
            "{ \"name\": \"hash\", \"type\": \"string\", \"doc\": \"Transaction Hash\" }, " +
            "{ \"name\": \"address\", \"type\": \"string\", \"doc\": \"Address\" }, " +
            "{ \"name\": \"value\", \"type\": \"long\", \"doc\": \"Transaction value\" }, " +
            "{ \"name\": \"tag\", \"type\": \"string\", \"doc\": \"TAG\" }, " +
            "{ \"name\": \"timestamp\", \"type\": \"long\", \"doc\": \"Timestamp\" }, " +
            "{ \"name\": \"payload\", \"type\": \"string\", \"doc\": \"Payload\" } " +
            "] " +
            "}";

    private String hash;
    private String type;
    private String address;
    private Long value;
    private String tag;
    private Long timestamp;
    private String payload;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public void put(int i, Object v) {
        switch (i) {
            case 0:
                setHash(String.valueOf(v));
                break;
            case 1:
                setAddress(String.valueOf(v));
                break;
            case 2:
                setValue(Long.valueOf(String.valueOf(v)));
                break;
            case 3:
                setTag(String.valueOf(v));
                break;
            case 4:
                setTimestamp(Long.valueOf(String.valueOf(v)));
                break;
            case 5:
                setPayload(String.valueOf(v));
                break;
        }
    }

    @Override
    public Object get(int i) {
        switch (i) {
            case 0:
                return getHash();
            case 1:
                return getAddress();
            case 2:
                return getValue();
            case 3:
                return getTag();
            case 4:
                return getTimestamp();
            case 5:
                return getPayload();
            default:
                return null;
        }
    }

    @Override
    public Schema getSchema() {
        return new Schema.Parser().parse(AVRO_SCHEMA);
    }
}
