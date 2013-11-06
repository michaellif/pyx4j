/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.extra.weather;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface WeatherIcons extends ClientBundle {

    WeatherIcons INSTANCE = GWT.create(WeatherIcons.class);

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("sunny.png")
    ImageResource sunny();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("fair.png")
    ImageResource fair();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("pcloudy.png")
    ImageResource partlyCloudy();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("mcloudy.png")
    ImageResource mostlyCloudy();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("cloudy.png")
    ImageResource cloudy();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("fog.png")
    ImageResource fog();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("lshowers.png")
    ImageResource lightShowers();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("showers.png")
    ImageResource showers();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("thunshowers.png")
    ImageResource thunderShowers();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("rainsnow.png")
    ImageResource rainAndSnow();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("flurries.png")
    ImageResource flurries();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("snowshow.png")
    ImageResource snow();

}
