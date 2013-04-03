/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.field.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.field.client.ui.building.BuildingDetailsView;
import com.propertyvista.field.client.ui.building.BuildingDetailsViewImpl;
import com.propertyvista.field.client.ui.building.BuildingListerView;
import com.propertyvista.field.client.ui.building.BuildingListerViewImpl;

public class BuildingViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (BuildingListerView.class.equals(type)) {
                map.put(type, new BuildingListerViewImpl());
            }
            if (BuildingDetailsView.class.equals(type)) {
                map.put(type, new BuildingDetailsViewImpl());
            }
        }

        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}
