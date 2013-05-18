/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.mock;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.propertyvista.yardi.mock.TransactionChargeUpdater.Name;

public class Updater<PROP, INST> {

    private final Map<Name, Property<?>> map = new HashMap<Name, Property<?>>();

    @SuppressWarnings("unchecked")
    public <T> INST set(Name name, T value) {
        map.put(name, Property.create(value));
        return (INST) this;
    }

    public PROP update(PROP detail) {
        for (Name name : map.keySet()) {
            Property<?> property = map.get(name);
            try {
                Method setter = null;
                if (property.get() != null) {
                    setter = detail.getClass().getMethod("set" + name, property.get().getClass());
                } else {
                    Method[] methods = detail.getClass().getMethods();
                    for (Method method : methods) {
                        if (method.getName().equals("set" + name)) {
                            setter = method;
                        }
                    }
                }
                setter.invoke(detail, property.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return detail;
    }
}
