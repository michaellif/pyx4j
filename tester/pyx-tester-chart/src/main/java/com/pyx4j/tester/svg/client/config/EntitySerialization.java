/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Mar 9, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.svg.client.config;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamReader;
import com.google.gwt.user.client.rpc.impl.Serializer;

import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.serialization.client.SymmetricClientSerializationStreamWriter;

public class EntitySerialization {

    private static final Serializer serializer;

    /**
     * Trigger generation of Serialized by GWT
     */
    static {
        serializer = RPCManager.getSerializer();
    }

    public static String serialize(Serializable data) throws SerializationException {
        SymmetricClientSerializationStreamWriter w = new SymmetricClientSerializationStreamWriter(serializer);
        w.prepareToWrite();
        w.writeObject(data);
        return w.toString();
    }

    @SuppressWarnings("unchecked")
    public static <E extends Serializable> E deserialize(String payload) throws SerializationException {
        ClientSerializationStreamReader r = new ClientSerializationStreamReader(serializer);
        r.prepareToRead(payload);
        return (E) r.readObject();
    }
}
