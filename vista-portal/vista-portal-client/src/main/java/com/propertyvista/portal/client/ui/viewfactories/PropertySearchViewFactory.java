/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.portal.client.ui.searchapt.ApartmentDetailsView;
import com.propertyvista.portal.client.ui.searchapt.ApartmentDetailsViewImpl;
import com.propertyvista.portal.client.ui.searchapt.FloorplanDetailsView;
import com.propertyvista.portal.client.ui.searchapt.FloorplanDetailsViewImpl;
import com.propertyvista.portal.client.ui.searchapt.PropertyMapView;
import com.propertyvista.portal.client.ui.searchapt.PropertyMapViewImpl;
import com.propertyvista.portal.client.ui.searchapt.SearchApartmentView;
import com.propertyvista.portal.client.ui.searchapt.SearchApartmentViewImpl;

public class PropertySearchViewFactory extends ViewFactoryBase {

    public static IsWidget instance(Class<?> type) {
        if (!map.containsKey(type)) {
            if (ApartmentDetailsView.class.equals(type)) {
                map.put(type, new ApartmentDetailsViewImpl());
            } else if (FloorplanDetailsView.class.equals(type)) {
                map.put(type, new FloorplanDetailsViewImpl());
            } else if (PropertyMapView.class.equals(type)) {
                map.put(type, new PropertyMapViewImpl());
            } else if (SearchApartmentView.class.equals(type)) {
                map.put(type, new SearchApartmentViewImpl());
            }

        }
        return map.get(type);
    }

}
