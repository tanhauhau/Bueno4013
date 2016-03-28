package com.server.pack;

import com.sun.tools.javac.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lhtan on 22/3/16.
 */
public class Pack {

    private ArrayList<String> properties;
    private HashMap<String, Object> values;

    public Object getValue(String property){
        Assert.check(values.containsKey(property), "No such property lah, you mother fucker..");
        return values.get(property);
    }
    public byte[] getByteArray(){
        //1. calculate the size needed for the buffer array
        int size = 1;
        for (Object value : values.values()){
            if (value instanceof Integer){
                size += 4;
            }else if (value instanceof String){
                size += 4 + ((String) value).length();
            }else if (value instanceof Long){
                size += 8;
            }else if (value instanceof byte[]){
                size += 4 + ((byte[]) value).length;
            }else if (value instanceof OneByteInt){
                size += 1;
            }
        }
        //2. create the buffer
        byte[] buffer = new byte[size];
        //3. serialize the pack
        int index = 0;
        for (String property : properties){
            Object value = values.get(property);
            if (value instanceof Integer){
                index = intToByte((Integer) value, buffer, index);
            }else if (value instanceof String){
                index = intToByte(((String) value).length(), buffer, index);
                index = stringToByte((String) value, buffer, index);
            }else if (value instanceof Long){
                index = longToByte((Long) value, buffer, index);
            }else if (value instanceof byte[]){
                index = intToByte(((byte[]) value).length, buffer, index);
                System.arraycopy(value, 0, buffer, index, ((byte[]) value).length);
                index += ((byte[]) value).length;
            }else if (value instanceof OneByteInt){
                buffer[index ++] = (byte) (((OneByteInt) value).getValue() & 0xFF);
            }
        }
        //4. return the buffer
        return buffer;
    }
    //standardize use
    private int intToByte(int i, byte[] buffer, int index){
        buffer[index++] = (byte)((i >> 24) & 0xFF);
        buffer[index++] = (byte)((i >> 16) & 0xFF);
        buffer[index++] = (byte)((i >> 8) & 0xFF);
        buffer[index++] = (byte)((i) & 0xFF);
        return index;
    }

    private int stringToByte(String s, byte[] buffer, int index){
        for (byte b : s.getBytes()){
            buffer[index++] = b;
        }
        return index;
    }

    private int longToByte(long l, byte[] buffer, int index){
        buffer[index ++] = (byte)((l >> 56) & 0xFF);
        buffer[index ++] = (byte)((l >> 48) & 0xFF);
        buffer[index ++] = (byte)((l >> 40) & 0xFF);
        buffer[index ++] = (byte)((l >> 32) & 0xFF);
        buffer[index ++] = (byte)((l >> 24) & 0xFF);
        buffer[index ++] = (byte)((l >> 16) & 0xFF);
        buffer[index ++] = (byte)((l >>  8) & 0xFF);
        buffer[index ++] = (byte)((l >>  0) & 0xFF);
        return index;
    }

    private Pack() {
        this.properties = new ArrayList<>();
        this.values = new HashMap<>();
    }
    public Pack include(Pack pack){
        if (pack != null) {
            for (String prop : pack.properties) {
                Assert.check(!this.properties.contains(prop), String.format("You already have this property %s ley, you mother fucker", prop));
            }
            this.properties.addAll(pack.properties);
            this.values.putAll(pack.values);
        }
        return this;
    }
    private void setValue(String property, Object value){
        this.properties.add(property);
        this.values.put(property, value);
    }
    public static class Builder{
        private Pack pack;
        public Builder() {
            pack = new Pack();
        }
        public Builder setValue(String property, Long value){
            return set(property, value);
        }
        public Builder setValue(String property, Integer value){
            return set(property, value);
        }
        public Builder setValue(String property, String value){
            return set(property, value);
        }
        public Builder setValue(String property, byte[] value){
            return set(property, value);
        }
        public Builder setValue(String property, OneByteInt value){
            return set(property, value);
        }
        private Builder set(String property, Object value){
            Assert.check(!pack.values.containsKey(property), "You already have this property dy lah, you mother fucker..");
            pack.setValue(property, value);
            return this;
        }
        public Pack build(){
            return pack;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean has = false;
        for (String property : properties){
            if (has) sb.append(", ");
            Object value = values.get(property);
            if (value instanceof Integer){
                sb.append(property).append(": ").append((Integer) value);
            }else if (value instanceof String){
                sb.append(property).append(": \"").append(((String) value)).append("\"");
            }else if (value instanceof Long){
                sb.append(property).append(": ").append((Long) value);
            }else if (value instanceof byte[]){
                sb.append(property).append(": <byte array>");
            }else if (value instanceof OneByteInt){
                sb.append(property).append(": ").append(((OneByteInt) value).getValue());
            }
            has = true;
        }
        sb.append("}");
        return sb.toString();
    }
}
