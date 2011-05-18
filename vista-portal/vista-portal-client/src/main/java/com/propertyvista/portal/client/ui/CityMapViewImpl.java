/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget;

public class CityMapViewImpl extends SimplePanel implements CityMapView {

    public CityMapViewImpl() {
        setWidget(new PropertiesMapWidget());
    }

    @Override
    public void setPresenter(Presenter presenter) {
        // TODO Auto-generated method stub

    }
}
