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
import java.util.LinkedHashMap;
import java.util.Map;

public class Updater<MODEL, INST_CLASS> {

    protected final Map<Name, Property<?>> map = new LinkedHashMap<Name, Property<?>>();

    @SuppressWarnings("unchecked")
    public <T> INST_CLASS set(Name name, T value) {
        map.put(name, Property.create(name, value));
        return (INST_CLASS) this;
    }

    Map<Name, Property<?>> getPropertyMap() {
        return map;
    }

    protected void updateProperty(Object model, Property<?> property) {
        try {
            Method setter = null;
            if (property.getValue() != null) {
                setter = model.getClass().getMethod("set" + property.getName(), property.getValue().getClass());
            } else {
                Method[] methods = model.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getName().equals("set" + property.getName())) {
                        setter = method;
                    }
                }
            }
            setter.invoke(model, property.getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
