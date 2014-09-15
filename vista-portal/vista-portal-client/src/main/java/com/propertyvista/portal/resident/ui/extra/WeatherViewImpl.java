/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.extra;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.layout.frontoffice.FrontOfficeLayoutTheme;

import com.propertyvista.portal.resident.themes.ExtraGadgetsTheme;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherGadgetDTO;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class WeatherViewImpl extends FlowPanel implements WeatherView {

    private static final I18n i18n = I18n.get(WeatherViewImpl.class);

    public WeatherViewImpl() {

    }

    @Override
    public void populateWeather(WeatherGadgetDTO gadgetDTO) {
        clear();

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.setStyleName(PortalRootPaneTheme.StyleName.ExtraGadget.name());

        if (gadgetDTO != null) {
            HTML captionLabel = new HTML(i18n.tr("Today's Weather"));
            captionLabel.setStylePrimaryName(FrontOfficeLayoutTheme.StyleName.FrontOfficeLayoutInlineExtraPanelCaption.name());
            add(captionLabel);
            add(contentPanel);

            Image image = new Image();

            switch (gadgetDTO.weatherType().getValue()) {
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
            contentPanel.add(image);

            FlowPanel weatherTextPanel = new FlowPanel();

            weatherTextPanel.setStyleName(ExtraGadgetsTheme.StyleName.WeatherText.name());
            contentPanel.add(weatherTextPanel);

            HTML temperatureHTML = new HTML(gadgetDTO.temperature().getValue() + "&#176;");
            temperatureHTML.setStyleName(ExtraGadgetsTheme.StyleName.WeatherTemperature.name());
            weatherTextPanel.add(temperatureHTML);

            HTML weatherTypeHTML = new HTML(gadgetDTO.weatherType().getStringView());
            weatherTypeHTML.setStyleName(ExtraGadgetsTheme.StyleName.WeatherType.name());
            weatherTextPanel.add(weatherTypeHTML);
        } else {
            add(contentPanel);
            Image image = new Image(WeatherIcons.INSTANCE.fair());
            image.setStyleName(ExtraGadgetsTheme.StyleName.WeatherIcon.name());
            contentPanel.add(image);
            contentPanel.add(new HTML(i18n.tr("Sorry, service is currently not available.")));
        }

    }

}
