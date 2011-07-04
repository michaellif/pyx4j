/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.searchapt;

import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.geo.GeoPoint;

import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public interface PropertyMapView extends IsWidget {

    void setPresenter(Presenter presenter);

    void populate(PropertySearchCriteria criteria, GeoPoint geoPoint, PropertyListDTO propertyList);

    void updateMarkers(PropertyListDTO inboundPropertyList, PropertyListDTO outboundPropertyList);

    PropertySearchCriteria getValue();

    public interface Presenter {

        void showPropertyDetails(PropertyDTO property);

        void refineSearch();

        void updateMap(LatLngBounds latLngBounds);

    }

}
