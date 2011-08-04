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

import com.propertyvista.crm.client.ui.crud.building.catalog.ConcessionEditorView;
import com.propertyvista.crm.client.ui.crud.building.catalog.ConcessionEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.catalog.ConcessionViewerView;
import com.propertyvista.crm.client.ui.crud.building.catalog.ConcessionViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.catalog.FeatureEditorView;
import com.propertyvista.crm.client.ui.crud.building.catalog.FeatureEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.catalog.FeatureViewerView;
import com.propertyvista.crm.client.ui.crud.building.catalog.FeatureViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.catalog.ServiceEditorView;
import com.propertyvista.crm.client.ui.crud.building.catalog.ServiceEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.catalog.ServiceViewerView;
import com.propertyvista.crm.client.ui.crud.building.catalog.ServiceViewerViewImpl;

public class MarketingViewFactory extends ViewFactoryBase {

    public static IView<? extends IEntity> instance(Class<? extends IView<? extends IEntity>> type) {
        if (!map.containsKey(type)) {
            if (ServiceViewerView.class.equals(type)) {
                map.put(type, new ServiceViewerViewImpl());
            } else if (ServiceEditorView.class.equals(type)) {
                map.put(type, new ServiceEditorViewImpl());
            } else if (FeatureViewerView.class.equals(type)) {
                map.put(type, new FeatureViewerViewImpl());
            } else if (FeatureEditorView.class.equals(type)) {
                map.put(type, new FeatureEditorViewImpl());
            } else if (ConcessionViewerView.class.equals(type)) {
                map.put(type, new ConcessionViewerViewImpl());
            } else if (ConcessionEditorView.class.equals(type)) {
                map.put(type, new ConcessionEditorViewImpl());
            }
        }
        return map.get(type);
    }
}
