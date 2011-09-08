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
package com.propertyvista.portal.client.ui.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.Window;

public class PropertyMapController {

    private static Logger log = LoggerFactory.getLogger(PropertyMapController.class);

    private final PropertiesMapWidget map;

    public PropertyMapController() {

        System.out.println("+++++" + Window.Location.getParameterMap());

        map = new PropertiesMapWidget();
    }

    public PropertiesMapWidget getMap() {
        return map;
    }

}
