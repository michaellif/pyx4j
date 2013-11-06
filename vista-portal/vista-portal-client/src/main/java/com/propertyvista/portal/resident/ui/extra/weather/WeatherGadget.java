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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.domain.dto.extra.WeatherGadgetDTO;
import com.propertyvista.portal.resident.themes.ExtraGadgetsTheme;
import com.propertyvista.portal.resident.ui.extra.ExtraGadget;

public class WeatherGadget extends ExtraGadget<WeatherGadgetDTO> {

    private static final I18n i18n = I18n.get(WeatherGadget.class);

    public WeatherGadget(WeatherGadgetDTO gadgetDTO) {
        super(gadgetDTO, i18n.tr("Today's Weather"));
    }

    @Override
    protected Widget createBody() {
        FlowPanel panel = new FlowPanel();
        Image image = new Image();

        switch (getGadgetDTO().weatherType().getValue()) {
        case sunny:
            image.setResource(WeatherIcons.INSTANCE.sunny());
            break;
        case fair:
            image.setResource(WeatherIcons.INSTANCE.fair());
            break;
        case partlyCloudy:
            image.setResource(WeatherIcons.INSTANCE.partlyCloudy());
            break;
        case mostlyCloudy:
            image.setResource(WeatherIcons.INSTANCE.mostlyCloudy());
            break;
        case cloudy:
            image.setResource(WeatherIcons.INSTANCE.cloudy());
            break;
        case fog:
            image.setResource(WeatherIcons.INSTANCE.fog());
            break;
        case lightShowers:
            image.setResource(WeatherIcons.INSTANCE.lightShowers());
            break;
        case showers:
            image.setResource(WeatherIcons.INSTANCE.showers());
            break;
        case thunderShowers:
            image.setResource(WeatherIcons.INSTANCE.thunderShowers());
            break;
        case rainAndSnow:
            image.setResource(WeatherIcons.INSTANCE.rainAndSnow());
            break;
        case flurries:
            image.setResource(WeatherIcons.INSTANCE.flurries());
            break;
        case snow:
            image.setResource(WeatherIcons.INSTANCE.snow());
            break;
        default:
            break;
        }
        image.setStyleName(ExtraGadgetsTheme.StyleName.WeatherIcon.name());
        panel.add(image);

        FlowPanel weatherTextPanel = new FlowPanel();
        weatherTextPanel.setStyleName(ExtraGadgetsTheme.StyleName.WeatherText.name());
        panel.add(weatherTextPanel);

        HTML temperatureHTML = new HTML(getGadgetDTO().temperature().getValue() + "&#176;");
        temperatureHTML.setStyleName(ExtraGadgetsTheme.StyleName.WeatherTemperature.name());
        weatherTextPanel.add(temperatureHTML);

        HTML weatherTypeHTML = new HTML(getGadgetDTO().weatherType().getStringView());
        weatherTypeHTML.setStyleName(ExtraGadgetsTheme.StyleName.WeatherType.name());
        weatherTextPanel.add(weatherTypeHTML);

        return panel;
    }
}
