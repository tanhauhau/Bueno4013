package com.client.pack;

import com.sun.tools.javac.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lhtan on 22/3/16.
 */

/*
    This class is for Marshalling
 */
public class Pack {

    private ArrayList<String> properties;
    private HashMap<String, Object> values;

    /*
    To check whether the object is contained in the hashmap
     */
    public Object getValue(String property){
        Assert.check(values.containsKey(property), "No Such Property Existed! ");
        return values.get(property);
    }

    private Pack() {
        this.properties = new ArrayList<>();
        this.values = new HashMap<>();
    }
    private void setValue(String property, Object value){
        this.properties.add(property);
        this.values.put(property, value);
    }

    /*
        By Storing the objects into a hashtable
    */
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
            Assert.check(!pack.values.containsKey(property), "Property already existed!");
            pack.setValue(property, value);
            return this;
        }
        public Pack build(){
            return pack;
        }
    }

    /*
    Methods below are utilised for Marshalling the messages into bytearray
     */

    public byte[] getByteArray(){
        /*
        calculate the size needed for the buffer array by
        scanning through the objects
        Integer     - 4 Bytes
        String      - String length + 4 Bytes
        Long        - 8 Bytes
        byte        - byte length + 4 Bytes
        OneByteInt  - 1 Byte
         */
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
        /*
        create the buffer
         */
        byte[] buffer = new byte[size];
        /*
         convert the object into bytes and stored into the buffer for Marshalling
          */
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
    /*
    Integer consists of 4 bytes, 32 bits
    Byte 3, Byte 2, Byte 1, Byte 0
    1 slot in buffer will use to contain 1 byte
     */
    private int intToByte(int i, byte[] buffer, int index){
        buffer[index++] = (byte)((i >> 24) & 0xFF);     //for Byte 3
        buffer[index++] = (byte)((i >> 16) & 0xFF);     //for Byte 2
        buffer[index++] = (byte)((i >> 8) & 0xFF);      //for Byte 1
        buffer[index++] = (byte)((i) & 0xFF);           //for Byte 0
        return index;
    }

    /*
        Assumption made :
        All string characters are only ASCii characters
        ASCii characters can be fit in 8 bits size, 1 byte size
        hence every slot of buffer can fit a character
     */
    private int stringToByte(String s, byte[] buffer, int index){
        for (byte b : s.getBytes()){
            buffer[index++] = b;
        }
        return index;
    }

    /*
    Similar to Integer, just that Long require 8 bytes, which is 64 bits
     */

    private int longToByte(long l, byte[] buffer, int index){
        buffer[index ++] = (byte)((l >> 56) & 0xFF);        //for byte 7
        buffer[index ++] = (byte)((l >> 48) & 0xFF);        //for byte 6
        buffer[index ++] = (byte)((l >> 40) & 0xFF);        //for byte 5
        buffer[index ++] = (byte)((l >> 32) & 0xFF);        //for byte 4
        buffer[index ++] = (byte)((l >> 24) & 0xFF);        //for byte 3
        buffer[index ++] = (byte)((l >> 16) & 0xFF);        //for byte 2
        buffer[index ++] = (byte)((l >>  8) & 0xFF);        //for byte 1
        buffer[index ++] = (byte)((l >>  0) & 0xFF);        //for byte 0
        return index;
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
