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
 * Created on 2011-05-14
 * @author vlads
 * @version $Id$
 */
package com.google.gwt.user.client.rpc.core.java.sql;

import java.sql.Date;

import com.google.gwt.user.client.rpc.CustomFieldSerializer;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

public class Date_CustomFieldSerializer extends CustomFieldSerializer<Date> {

    /**
     * Since class search order is not defined in war/WEB-INF/lib there is a chance that this fix may not work.
     * 
     * You have an option to disable this locally. But let me know about the problem.
     */
    private static final boolean disableSqlDateSerializationFix = false;

    @Override
    public boolean hasCustomInstantiateInstance() {
        return true;
    }

    @Override
    public Date instantiateInstance(SerializationStreamReader streamReader) throws SerializationException {
        return instantiate(streamReader);
    }

    @Override
    public void deserializeInstance(SerializationStreamReader streamReader, Date instance) throws SerializationException {
        // No fields
    }

    @Override
    public void serializeInstance(SerializationStreamWriter streamWriter, Date instance) throws SerializationException {
        serialize(streamWriter, instance);
    }

    public static void deserialize(SerializationStreamReader streamReader, Date instance) {
        // No fields
    }

    @SuppressWarnings("deprecation")
    public static Date instantiate(SerializationStreamReader streamReader) throws SerializationException {
        if (disableSqlDateSerializationFix) {
            return new Date(streamReader.readLong());
        } else {
            long s = streamReader.readLong();
            if (s != -77L) {
                throw new Error("Wrong class loader order, Call VladS #" + s);
            }
            return new Date(streamReader.readInt(), streamReader.readInt(), streamReader.readInt());
        }
    }

    @SuppressWarnings("deprecation")
    public static void serialize(SerializationStreamWriter streamWriter, Date instance) throws SerializationException {
        if (disableSqlDateSerializationFix) {
            streamWriter.writeLong(instance.getTime());
        } else {
            streamWriter.writeLong(-77L);
            streamWriter.writeInt(instance.getYear());
            streamWriter.writeInt(instance.getMonth());
            streamWriter.writeInt(instance.getDate());
        }
    }

}
