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
 * Created on Jan 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.test.client;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamReader;
import com.google.gwt.user.client.rpc.impl.Serializer;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.serialization.client.SymmetricClientSerializationStreamWriter;

public class SymmetricSerializationGWTTest extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(SymmetricSerializationGWTTest.class);

    static final SomeRemoteServiceAsync service;

    /**
     * Trigger generation of Serialized by GWT
     */
    static {
        service = (SomeRemoteServiceAsync) GWT.create(SomeRemoteService.class);
    }

    private Object serializeDeserialize(Object data) throws SerializationException {
        String testName = getName();

        RPCSerializer sa = GWT.create(RPCSerializer.class);

        Serializer serializer = sa.getSerializer();

        SymmetricClientSerializationStreamWriter w = new SymmetricClientSerializationStreamWriter(serializer);
        w.prepareToWrite();
        w.writeObject(data);

        String payload = w.toString();
        log.debug("{} payload {} ", testName, payload);

        ClientSerializationStreamReader r = new ClientSerializationStreamReader(serializer);
        r.prepareToRead(payload);
        log.debug("{} payload processed", testName);
        Object o = r.readObject();
        if (o != null) {
            log.debug("{} got Object class {}", testName, o.getClass().getName());
        }
        return o;
    }

    private void assertFullyEquals(String caseName, SomeSerializableData r, SomeSerializableData r2) {
        assertEquals(caseName + " equals.Name", r.getName(), r2.getName());
        assertEquals(caseName + " equals.Description", r.getDescription(), r2.getDescription());
        assertEquals(caseName + " equals.LongValue", r.getLongValue(), r2.getLongValue());
        assertEquals(caseName + " equals.LongClassValue", r.getLongClassValue(), r2.getLongClassValue());
        assertEquals(caseName + " equals.DoubleValue", r.getDoubleValue(), r2.getDoubleValue());
        assertEquals(caseName + " equals.DateValue", r.getDateValue(), r2.getDateValue());
        assertEquals(caseName + " equals.ObjectValue", r.getObjectValue(), r2.getObjectValue());
    }

    public void testSimpleObject() throws SerializationException {

        SomeSerializableData r = new SomeSerializableData();
        r.setLongClassValue(Long.valueOf(21));
        r.setLongValue(3);
        r.setDoubleValue(36.6);
        r.setName("Bob");
        r.setDateValue(new Date());

        Object o = serializeDeserialize(r);
        assertTrue("Object Type", (o instanceof SomeSerializableData));
        assertFullyEquals("Simple", r, (SomeSerializableData) o);
    }

    /**
     * @see com.google.gwt.user.server.rpc.impl.ServerSerializationStreamWriter#escapeString(String)
     */
    public void testEscape() throws SerializationException {

        SomeSerializableData r = new SomeSerializableData();
        r.setLongValue(3);
        r.setDoubleValue(36.6);
        r.setName("Bob's");
        r.setDescription("S\" d");

        assertFullyEquals("quotes", r, (SomeSerializableData) serializeDeserialize(r));

        r.setName("BobT[\t]s");
        r.setDescription("B[\b] N[\n] F[\f] R[\r] ESCAPE [\\]");
        assertFullyEquals("special", r, (SomeSerializableData) serializeDeserialize(r));
    }

    public void testVector() throws SerializationException {
        SomeSerializableData r = new SomeSerializableData();
        r.setLongValue(Long.valueOf(1));
        r.setName("Bob with members");
        Vector<SomeSerializableData> members = new Vector<SomeSerializableData>();

        SomeSerializableData r1 = new SomeSerializableData();
        r1.setLongValue(Long.valueOf(11));
        r1.setName("Bob member 1");
        members.add(r1);

        SomeSerializableData r2 = new SomeSerializableData();
        r2.setLongValue(Long.valueOf(12));
        r2.setName("Bob member 2");
        members.add(r2);

        r.setObjectsVectorValue(members);

        SomeSerializableData rDs = (SomeSerializableData) serializeDeserialize(r);
        assertFullyEquals("main", r, rDs);

        Iterator<SomeSerializableData> iterSs = rDs.getObjectsVectorValue().iterator();
        SomeSerializableData r1Ds = iterSs.next();
        SomeSerializableData r2Ds = iterSs.next();

        assertFullyEquals("set member1", r1, r1Ds);
        assertFullyEquals("set member2", r2, r2Ds);

        assertTrue("Set eq", EqualsHelper.equals(members, rDs.getObjectsVectorValue()));
    }
}
