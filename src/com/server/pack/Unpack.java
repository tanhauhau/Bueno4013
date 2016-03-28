package com.server.pack;

import com.sun.tools.javac.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by lhtan on 22/3/16.
 */
public class Unpack {

    public enum TYPE{
        INTEGER, LONG, STRING, BYTE_ARRAY, ONE_BYTE_INT;
    }

    private ArrayList<String> properties;
    private HashMap<String, TYPE> values;

    public static class Result{
        HashMap<String, Object> map;

        public Result(HashMap<String, Object> map) {
            this.map = map;
        }
        public Integer getInt(String value){
            if (map.containsKey(value) && map.get(value) instanceof Integer){
                return (Integer) map.get(value);
            }
            return null;
        }
        public String getString(String value){
            if (map.containsKey(value) && map.get(value) instanceof String){
                return (String) map.get(value);
            }
            return null;
        }
        public Long getLong(String value){
            if (map.containsKey(value) && map.get(value) instanceof Long){
                return (Long) map.get(value);
            }
            return null;
        }
        public byte[] getByteArray(String value){
            if (map.containsKey(value) && map.get(value) instanceof byte[]){
                return (byte[]) map.get(value);
            }
            return null;
        }
        public OneByteInt getOneByteInt(String value){
            if (map.containsKey(value) && map.get(value) instanceof OneByteInt){
                return (OneByteInt) map.get(value);
            }
            return null;
        }
    }

    public Result parseByteArray(byte[] data){
        int offset = 0;
        HashMap<String, Object> map = new HashMap<>();
        for (String property : properties){
            TYPE value = values.get(property);
            switch (value){
                case INTEGER:
                    map.put(property, parseInt(data, offset));
                    offset += 4;
                    break;
                case LONG:
                    map.put(property, parseLong(data, offset));
                    offset += 8;
                    break;
                case STRING:
                    int length = parseInt(data, offset);
                    map.put(property, parseString(data, offset + 4, length));
                    offset += 4 + length;
                    break;
                case ONE_BYTE_INT:
                    map.put(property, new OneByteInt(data[offset] & 0xFF));
                    offset += 1;
                    break;
                case BYTE_ARRAY:
                    int byte_length = parseInt(data, offset);
                    map.put(property, Arrays.copyOfRange(data, offset + 4, offset + 4 + byte_length));
                    break;
            }
        }
        //4. return the result
        return new Result(map);
    }

    private Integer parseInt(byte[] data, int offset){
        try{
            int i = ((int)data[offset ++] & 0xFF) << 24;
            i += ((int)data[offset ++] & 0xFF) << 16;
            i += ((int)data[offset ++] & 0xFF) << 8;
            i += ((int)data[offset ++] & 0xFF);
            return i;
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }
    private Long parseLong(byte[] data, int offset){
        try{
            return  ((data[offset ++] & 0xFFL) << 56) |
                    ((data[offset ++] & 0xFFL) << 48) |
                    ((data[offset ++] & 0xFFL) << 40) |
                    ((data[offset ++] & 0xFFL) << 32) |
                    ((data[offset ++] & 0xFFL) << 24) |
                    ((data[offset ++] & 0xFFL) << 16) |
                    ((data[offset ++] & 0xFFL) <<  8) |
                    ((data[offset ++] & 0xFFL) <<  0) ;
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }
    private String parseString(byte[] data, int offset, int length){
        try{
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<length; i++, offset++){
                sb.append((char) data[offset]);
            }
            return sb.toString();
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    private Unpack() {
        this.properties = new ArrayList<>();
        this.values = new HashMap<>();
    }
    public Unpack include(Unpack unpack){
        if (unpack != null) {
            for (String prop : unpack.properties) {
                Assert.check(!this.properties.contains(prop), String.format("You already have this property %s ley, you mother fucker", prop));
            }
            this.properties.addAll(unpack.properties);
            this.values.putAll(unpack.values);
        }
        return this;
    }

    public static class Builder{
        private Unpack pack;
        public Builder() {
            pack = new Unpack();
        }
        public Builder setType(String property, TYPE type){
            Assert.check(!pack.values.containsKey(property), "You already have this property dy lah, you mother fucker..");
            pack.properties.add(property);
            pack.values.put(property, type);
            return this;
        }
        public Unpack build(){
            return pack;
        }
    }
}
