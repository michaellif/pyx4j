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

}
