/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 25, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.operations.client.ui.crud.simulateddatapreload.SimulatedDataPreloadView;
import com.propertyvista.operations.client.ui.crud.simulateddatapreload.SimulatedDataPreloadViewImpl;
import com.propertyvista.operations.client.viewfactories.crud.ViewFactoryBase;

public class MiscViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (type.equals(SimulatedDataPreloadView.class)) {
                map.put(SimulatedDataPreloadView.class, new SimulatedDataPreloadViewImpl());
            }
        }
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}
