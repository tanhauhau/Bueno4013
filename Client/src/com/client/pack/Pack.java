package com.client.pack;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lhtan on 22/3/16.
 * This Pack Class is the Marshalling handler
 * The messages will be converted into byte array according to the
 * type of objects
 */
public class Pack {

    private ArrayList<String> properties;
    private HashMap<String, Object> values;

    /**
     * Class Constructor for Pack
     */
    private Pack() {
        this.properties = new ArrayList<>();
        this.values = new HashMap<>();
    }

    /**
     * This method will store the object into hashmap based on the property key
     *
     * @param property Key for the hashmap to store the object
     * @param value    Object to be stored in hashmap
     */
    private void setValue(String property, Object value) {
        this.properties.add(property);
        this.values.put(property, value);
    }

    /**
     * This method will retrieve the value based on the input string
     *
     * @param property Key for the hashmap to retrieve the value
     * @return The correspond object based on the string
     */
    public Object getValue(String property) {
//        Assert.check(values.containsKey(property), "No Such Property Existed! ");
        return values.get(property);
    }

    /**
     * This method will translate the message and convert
     * into byte array
     *
     * @return byte array containing the converted messages
     */
    public byte[] getByteArray() {
        /*
            Calculate the size required for the byte array
            based on the type of the object
            Integer     4 bytes
            String      4 + length of string bytes
            Long        8 bytes
            Byte Array  4 + length of byte array
            OneByteInt  1 byte
         */
        int size = 1;

        for (Object value : values.values()) {
            if (value instanceof Integer) {
                size += 4;
            } else if (value instanceof String) {
                size += 4 + ((String) value).length();
            } else if (value instanceof Long) {
                size += 8;
            } else if (value instanceof byte[]) {
                size += 4 + ((byte[]) value).length;
            } else if (value instanceof OneByteInt) {
                size += 1;
            }
        }
        /*
            Declared a buffer with the size calculated
         */
        byte[] buffer = new byte[size];
         /*
            Perform the conversion of messages
            based on the component and stored in the buffer
         */
        int index = 0;
        for (String property : properties) {
            Object value = values.get(property);
            if (value instanceof Integer) {
                index = intToByte((Integer) value, buffer, index);                      /* convert Integer to bytes */
            } else if (value instanceof String) {
                index = intToByte(((String) value).length(), buffer, index);            /* first integer is the length of string */
                index = stringToByte((String) value, buffer, index);                    /* convert the content of string to bytes */
            } else if (value instanceof Long) {
                index = longToByte((Long) value, buffer, index);                        /* convert Long to bytes */
            } else if (value instanceof byte[]) {
                index = intToByte(((byte[]) value).length, buffer, index);              /* first integer is the length of the byte array */
                System.arraycopy(value, 0, buffer, index, ((byte[]) value).length);     /* store the bytes into the buffer */
                index += ((byte[]) value).length;
            } else if (value instanceof OneByteInt) {
                buffer[index++] = (byte) (((OneByteInt) value).getValue() & 0xFF);     /* convert OneByteInt into bytes */
            }
        }
        /*
            Return the buffer containing the converted message
         */
        return buffer;
    }

    /**
     * Converting integer into bytes array
     * Integer has a size of 4 bytes, hence 4 slots of buffer
     * are needed to store it.
     *
     * @param i      Integer that to be converted
     * @param buffer buffer
     * @param index  current buffer index number
     * @return Lastest and updated current index number
     */
    private int intToByte(int i, byte[] buffer, int index) {
        buffer[index++] = (byte) ((i >> 24) & 0xFF);         /* most left byte, byte 3 */
        buffer[index++] = (byte) ((i >> 16) & 0xFF);         /* byte 2 */
        buffer[index++] = (byte) ((i >> 8) & 0xFF);          /* byte 1 */
        buffer[index++] = (byte) ((i) & 0xFF);               /* most right byte, byte 0 */
        return index;                                       /* update and return current index number */
    }

    /**
     * Converting string into byte array
     * Since string are made up of characters, there is an assumptions that
     * All characters are only ASCii characters, which can be fully represented by 8 bits, which is 1 byte
     * hence 1 slot of buffer can store 1 character from the string
     *
     * @param s      String that will be converted
     * @param buffer buffer
     * @param index  current buffer index number
     * @return Lastest and updated current index number
     */
    private int stringToByte(String s, byte[] buffer, int index) {
        for (byte b : s.getBytes()) {
            buffer[index++] = b;
        }
        return index;
    }

    /**
     * Converting Long into bytes array
     * Long has a size of 8 bytes, hence 8 slots of buffer
     * are needed to store it.
     *
     * @param l      Long that to be converted
     * @param buffer buffer
     * @param index  current buffer index number
     * @return Lastest and updated current index number
     */
    private int longToByte(long l, byte[] buffer, int index) {
        buffer[index++] = (byte) ((l >> 56) & 0xFF);    /* byte 7 */
        buffer[index++] = (byte) ((l >> 48) & 0xFF);    /* byte 6 */
        buffer[index++] = (byte) ((l >> 40) & 0xFF);    /* byte 5 */
        buffer[index++] = (byte) ((l >> 32) & 0xFF);    /* byte 4 */
        buffer[index++] = (byte) ((l >> 24) & 0xFF);    /* byte 3 */
        buffer[index++] = (byte) ((l >> 16) & 0xFF);    /* byte 2 */
        buffer[index++] = (byte) ((l >> 8) & 0xFF);    /* byte 1 */
        buffer[index++] = (byte) ((l) & 0xFF);    /* byte 0 */
        return index;
    }

    /**
     * This method is overriding the default
     * toString() method such that to have a
     * better representation of the result
     *
     * @return A string with the component of the messages combined
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean has = false;
        for (String property : properties) {
            if (has) sb.append(", ");
            Object value = values.get(property);
            if (value instanceof Integer) {
                sb.append(property).append(": ").append(value);
            } else if (value instanceof String) {
                sb.append(property).append(": \"").append(((String) value)).append("\"");
            } else if (value instanceof Long) {
                sb.append(property).append(": ").append(value);
            } else if (value instanceof byte[]) {
                sb.append(property).append(": <byte array>");
            } else if (value instanceof OneByteInt) {
                sb.append(property).append(": ").append(((OneByteInt) value).getValue());
            }
            has = true;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * This class is the Builder class for Pack
     */
    public static class Builder {
        private Pack pack;

        public Builder() {
            pack = new Pack();
        }

        public Builder setValue(String property, Long value) {
            return set(property, value);
        }

        public Builder setValue(String property, Integer value) {
            return set(property, value);
        }

        public Builder setValue(String property, String value) {
            return set(property, value);
        }

        public Builder setValue(String property, byte[] value) {
            return set(property, value);
        }

        public Builder setValue(String property, OneByteInt value) {
            return set(property, value);
        }

        private Builder set(String property, Object value) {
            pack.setValue(property, value);
            return this;
        }

        public Pack build() {
            return pack;
        }
    }
}
