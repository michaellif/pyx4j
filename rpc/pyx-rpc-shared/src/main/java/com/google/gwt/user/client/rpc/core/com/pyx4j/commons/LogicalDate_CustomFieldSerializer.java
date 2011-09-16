/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-06-01
 * @author vlads
 * @version $Id$
 */
package com.google.gwt.user.client.rpc.core.com.pyx4j.commons;

import com.google.gwt.user.client.rpc.CustomFieldSerializer;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

import com.pyx4j.commons.LogicalDate;

public class LogicalDate_CustomFieldSerializer extends CustomFieldSerializer<LogicalDate> {

    @Override
    public boolean hasCustomInstantiateInstance() {
        return true;
    }

    @Override
    public LogicalDate instantiateInstance(SerializationStreamReader streamReader) throws SerializationException {
        return instantiate(streamReader);
    }

    @Override
    public void deserializeInstance(SerializationStreamReader streamReader, LogicalDate instance) throws SerializationException {
        // No fields
    }

    @Override
    public void serializeInstance(SerializationStreamWriter streamWriter, LogicalDate instance) throws SerializationException {
        serialize(streamWriter, instance);
    }

    public static void deserialize(SerializationStreamReader streamReader, LogicalDate instance) {
        // No fields
    }

    public static LogicalDate instantiate(SerializationStreamReader streamReader) throws SerializationException {
        return new LogicalDate(streamReader.readInt(), streamReader.readInt(), streamReader.readInt());
    }

    public static void serialize(SerializationStreamWriter streamWriter, LogicalDate instance) throws SerializationException {
        streamWriter.writeInt(instance.getYear());
        streamWriter.writeInt(instance.getMonth());
        streamWriter.writeInt(instance.getDate());
    }

}
