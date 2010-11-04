/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Dec 18, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.AbstractSerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.Serializer;

/**
 * 
 * @see com.google.gwt.user.server.rpc.impl.ServerSerializationStreamWriter#escapeString(String)
 * 
 */
public class SymmetricClientSerializationStreamWriter extends AbstractSerializationStreamWriter {

    public static final char VALUE_SEPARATOR_CHAR = ',';

    private final Serializer serializer;

    private final List<String> encodeBuffer = new ArrayList<String>();

    public SymmetricClientSerializationStreamWriter(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void prepareToWrite() {
        super.prepareToWrite();
        encodeBuffer.clear();
    }

    @Override
    protected void append(String token) {
        encodeBuffer.add(token);
    }

    // GWT 2.0.4 version for tests.
    //    @com.google.gwt.core.client.UnsafeNativeLong
    //    private static native double[] makeLongComponents0(long value)
    //    /*-{
    //        return value;
    //    }-*/;
    //
    //    @Override
    //    public void writeLong(long fieldValue) {
    //        double[] parts;
    //        if (com.google.gwt.core.client.GWT.isScript()) {
    //            parts = makeLongComponents0(fieldValue);
    //        } else {
    //            parts = makeLongComponents((int) (fieldValue >> 32), (int) fieldValue);
    //        }
    //        assert parts.length == 2;
    //        writeDouble(parts[0]);
    //        writeDouble(parts[1]);
    //    }

    @Override
    public void writeLong(long value) {
        StringBuilder sb = new StringBuilder();
        sb.append('\'');
        sb.append(com.google.gwt.lang.LongLib.toBase64(value));
        sb.append('\'');
        append(sb.toString());
    }

    @Override
    protected String getObjectTypeSignature(Object instance) throws SerializationException {
        Class<?> clazz = instance.getClass();

        if (instance instanceof Enum<?>) {
            Enum<?> e = (Enum<?>) instance;
            clazz = e.getDeclaringClass();
        }

        return serializer.getSerializationSignature(clazz);
    }

    @Override
    protected void serialize(Object instance, String typeSignature) throws SerializationException {
        serializer.serialize(this, instance, typeSignature);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append('[');
        // Add value in reverse order, as ClientSerializationStreamReader will read
        int len = encodeBuffer.size();
        for (int i = len - 1; i >= 0; i--) {
            buffer.append(encodeBuffer.get(i));
            buffer.append(VALUE_SEPARATOR_CHAR);
        }

        // writeStringTable
        buffer.append('[');
        List<String> stringTable = getStringTable();
        boolean first = true;
        for (String s : stringTable) {
            if (first) {
                first = false;
            } else {
                buffer.append(VALUE_SEPARATOR_CHAR);
            }
            buffer.append(JsonUtils.escapeValue(s));
        }
        buffer.append(']');
        buffer.append(VALUE_SEPARATOR_CHAR);
        buffer.append(getFlags());
        buffer.append(VALUE_SEPARATOR_CHAR);
        buffer.append(getVersion());
        buffer.append(']');
        return buffer.toString();
    }

}
