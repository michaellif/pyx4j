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

import java.util.HashMap;
import java.util.Map;

public abstract class MultiUpdater<PROP, INST> {

    @SuppressWarnings("rawtypes")
    private final Map<Object, Updater> updatersByObject = new HashMap<Object, Updater>();

    private final Map<Class<?>, Updater<?, INST>> updatersByName = new HashMap<Class<?>, Updater<?, INST>>();

    protected void addForUpdate(Class<?> name, Object obj) {
        addForUpdate(name, new Updater<PROP, INST>(), obj);
    }

    protected void addForUpdate(Class<?> name, Updater<?, INST> updater, Object obj) {
        if (!updatersByName.containsKey(name) && !updatersByObject.containsKey(obj)) {
            updatersByName.put(name, updater);
            updatersByObject.put(obj, updater);
        }
    }

    public <T> INST set(Name name, T value) {
        Updater<?, INST> updater = updatersByName.get(name.getClass());
        if (updater == null) {
            throw new Error(String.format("updater for %s not found", name));
        }
        updater.set(name, value);
        return (INST) this;
    }

    @SuppressWarnings("unchecked")
    public void update() {
        for (Map.Entry<Object, Updater> entry : updatersByObject.entrySet()) {
            entry.getValue().update(entry.getKey());
        }
    }

    public abstract PROP update(PROP detail);

}
