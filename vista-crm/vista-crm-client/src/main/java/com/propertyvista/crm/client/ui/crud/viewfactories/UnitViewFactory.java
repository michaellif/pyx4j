/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionEditorView;
import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionViewerView;
import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.unit.UnitEditorView;
import com.propertyvista.crm.client.ui.crud.unit.UnitEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.unit.UnitItemEditorView;
import com.propertyvista.crm.client.ui.crud.unit.UnitItemEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.unit.UnitItemViewerView;
import com.propertyvista.crm.client.ui.crud.unit.UnitItemViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.unit.UnitListerView;
import com.propertyvista.crm.client.ui.crud.unit.UnitListerViewImpl;
import com.propertyvista.crm.client.ui.crud.unit.UnitOccupancyEditorView;
import com.propertyvista.crm.client.ui.crud.unit.UnitOccupancyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.unit.UnitOccupancyViewerView;
import com.propertyvista.crm.client.ui.crud.unit.UnitOccupancyViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.unit.UnitViewerView;
import com.propertyvista.crm.client.ui.crud.unit.UnitViewerViewImpl;

public class UnitViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView<E>> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (UnitListerView.class.equals(type)) {
                map.put(type, new UnitListerViewImpl());
            } else if (UnitEditorView.class.equals(type)) {
                map.put(type, new UnitEditorViewImpl());
            } else if (UnitViewerView.class.equals(type)) {
                map.put(type, new UnitViewerViewImpl());

            } else if (UnitItemEditorView.class.equals(type)) {
                map.put(type, new UnitItemEditorViewImpl());
            } else if (UnitItemViewerView.class.equals(type)) {
                map.put(type, new UnitItemViewerViewImpl());

            } else if (UnitOccupancyEditorView.class.equals(type)) {
                map.put(type, new UnitOccupancyEditorViewImpl());
            } else if (UnitOccupancyViewerView.class.equals(type)) {
                map.put(type, new UnitOccupancyViewerViewImpl());

            } else if (ConcessionViewerView.class.equals(type)) {
                map.put(type, new ConcessionViewerViewImpl());
            } else if (ConcessionEditorView.class.equals(type)) {
                map.put(type, new ConcessionEditorViewImpl());
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
