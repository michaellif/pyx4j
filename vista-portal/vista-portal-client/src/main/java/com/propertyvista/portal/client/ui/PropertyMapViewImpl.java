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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.geo.GeoPoint;

import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget;
import com.propertyvista.portal.domain.site.Property;

public class PropertyMapViewImpl extends SimplePanel implements PropertyMapView {

    public PropertyMapViewImpl() {

        PropertiesMapWidget map = new PropertiesMapWidget();

        List<Property> properties = new ArrayList<Property>();
        {
            Property property = EntityFactory.create(Property.class);
            property.address().setValue("<div>320 Avenue Road</div><div>Toronto</div><div>ON M4V 2H3</div>");
            property.location().setValue(new GeoPoint(43.697665, -79.402313));
            properties.add(property);
        }

        map.populate(properties);

        setWidget(map);
    }

}
