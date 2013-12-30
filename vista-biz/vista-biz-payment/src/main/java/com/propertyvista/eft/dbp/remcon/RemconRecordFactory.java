/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.dbp.remcon;

import java.util.HashMap;
import java.util.Map;

class RemconRecordFactory {

    private final Map<Character, Class<? extends RemconRecord>> registry = new HashMap<Character, Class<? extends RemconRecord>>();

    private RemconRecordFactory() {
        register(RemconRecordFileHeader.class);
        register(RemconRecordFileTrailer.class);
        register(RemconRecordBatchHeader.class);
        register(RemconRecordBatchTrailer.class);
        register(RemconRecordBoxHeader.class);
        register(RemconRecordDetailRecord.class);

        RemconFieldReflection.initialize(registry.values());
    }

    private void register(Class<? extends RemconRecord> klass) {
        RemconRecord recInstance;
        try {
            recInstance = klass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
        registry.put(recInstance.recordType(), klass);
    }

    private static class SingletonHolder {
        public static final RemconRecordFactory INSTANCE = new RemconRecordFactory();
    }

    static RemconRecordFactory instance() {
        return SingletonHolder.INSTANCE;
    }

    static RemconRecord createRemconRecord(char recordType) {
        Class<? extends RemconRecord> klass = instance().registry.get(recordType);
        if (klass == null) {
            throw new Error("RemconRecord type '" + recordType + "' not registered");
        }
        try {
            return klass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }
}
