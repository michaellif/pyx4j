/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface PortalImages extends ClientBundle {

    PortalImages INSTANCE = GWT.create(PortalImages.class);

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("map_marker.png")
    ImageResource mapMarker();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("building2.jpg")
    ImageResource building2();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("building3.jpg")
    ImageResource building3();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("building4.jpg")
    ImageResource building4();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("building5.jpg")
    ImageResource building5();

}
