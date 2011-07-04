/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 4, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.maps;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.impl.MarkerImpl;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.gwt.geo.MapUtils;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.MediaUtils;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget.MarkerType;
import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget.StyleSuffix;
import com.propertyvista.portal.client.ui.util.Formatter;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class PropertyMarker extends Marker {

    private static I18n i18n = I18nFactory.getI18n(PropertyMarker.class);

    private final PropertyDTO property;

    private final MapWidget mapWidget;

    public PropertyMarker(final PropertyDTO property, MapWidget mapWidget) {
        super(MapUtils.newLatLngInstance(property.location().getValue()), createMarkerOptions(property));
        this.property = property;
        this.mapWidget = mapWidget;
        addMarkerClickHandler(new MarkerClickHandler() {
            @Override
            public void onClick(MarkerClickEvent event) {
                showInfo();
            }
        });
    }

    public PropertyDTO getProperty() {
        return property;
    }

    private static MarkerOptions createMarkerOptions(PropertyDTO property) {
        MarkerOptions markerOptions = MarkerOptions.newInstance();
        markerOptions.setTitle(property.address().getStringView());
        Icon icon = null;
        icon = Icon.newInstance(PortalImages.INSTANCE.delRow().getURL());
        //TODO get shadow URL
        //icon.setShadowURL(FmRiaResources.INSTANCE.mapMarkerHouseShadow().getURL());
        icon.setIconSize(Size.newInstance(38, 41));
        icon.setShadowSize(Size.newInstance(44, 35));
        icon.setIconAnchor(Point.newInstance(15, 20));
        icon.setInfoWindowAnchor(Point.newInstance(15, 5));
        markerOptions.setIcon(icon);
        return markerOptions;
    }

    public void setMarkerType(MarkerType markerType) {
        Icon icon = MarkerImpl.impl.getIcon(this);
        switch (markerType) {
        case inbound:
            icon.setImageURL(PortalImages.INSTANCE.mapMarker().getURL());
            icon.setIconSize(Size.newInstance(38, 41));
            icon.setShadowSize(Size.newInstance(44, 35));
            icon.setIconAnchor(Point.newInstance(15, 20));
            icon.setInfoWindowAnchor(Point.newInstance(15, 5));
            break;
        case outbound:
            icon.setImageURL(PortalImages.INSTANCE.mapMarkerOutbound().getURL());
            icon.setIconSize(Size.newInstance(10, 10));
            icon.setShadowSize(Size.newInstance(15, 15));
            icon.setIconAnchor(Point.newInstance(0, 0));
            icon.setInfoWindowAnchor(Point.newInstance(15, 5));
            break;
        }
    }

    public void showInfo() {
        InfoWindowContent bubble = new InfoWindowContent(new PropertyCard(property));
        bubble.setMaxWidth(250);
        mapWidget.getInfoWindow().open(this, bubble);
    }

    public class PropertyCard extends DockPanel {

        private final Anchor viewDetailsItem;

        public PropertyCard(final PropertyDTO property) {
            setStyleName(PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX);
            setSize("100%", "100%");
            getElement().getStyle().setProperty("minHeight", "100px");
            getElement().getStyle().setMarginTop(10d, Unit.PX);

            //format content
            FlowPanel content = new FlowPanel();
            content.setStyleName(PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardContent);
            content.setHeight("100%");
            content.getElement().getStyle().setMarginLeft(10, Unit.PX);
            content.getElement().getStyle().setMarginRight(10, Unit.PX);
            //address
            Label item = new Label(Formatter.formatAddress(property.address()));
            item.setStyleName(PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardContentItem);
            item.setWidth("100%");
            content.add(item);

            //unit(floor plan) types
            String floorString = Formatter.formatFloorplans(property.floorplanNames());
            if (floorString != null && !floorString.isEmpty()) {
                item = new Label(floorString);
                item.setStyleName(PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardContentItem);
                content.add(item);
            }

            //amenities
            String amenityString = Formatter.formatAmenities(property.amenities());
            if (amenityString != null && !amenityString.isEmpty()) {
                item = new Label(amenityString);
                item.setStyleName(PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardContentItem);
                content.add(item);
            }

            viewDetailsItem = new Anchor(i18n.tr("Details >>"));
            viewDetailsItem.setStyleName(PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardMenuItem);
            viewDetailsItem.getElement().getStyle().setFloat(Float.LEFT);
            viewDetailsItem.getElement().getStyle().setFontWeight(FontWeight.BOLD);

            viewDetailsItem.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    //TODO navigation done bypassing activities. Not sure if this is correct
                    AppPlace place = new PortalSiteMap.FindApartment.ApartmentDetails();
                    place.putArg(PortalSiteMap.ARG_PROPERTY_ID, property.id().getValue().toString());
                    AppSite.getPlaceController().goTo(place);
                }
            });
            content.add(viewDetailsItem);

            FlowPanel left = new FlowPanel();
            left.setStyleName(PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardLeft);
            //image
            SimplePanel imageHolder = new SimplePanel();
            imageHolder.getElement().getStyle().setHeight(50, Unit.PX);
            imageHolder.getElement().getStyle().setWidth(70, Unit.PX);
            imageHolder.setStyleName(PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardImage);

            imageHolder.getElement().getStyle().setProperty("minHeight", "50px");
            imageHolder.setWidget(MediaUtils.createPublicMediaImage(property.mainMedia(), ThumbnailSize.small));
            left.add(imageHolder);

            //from date
            item = new Label(i18n.tr("Starting from"));
            left.add(item);
            item = new Label(property.avalableForRent().getStringView());
            item.setStyleName(PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardLeftItem);
            left.add(item);

            //from price
            item = new Label(i18n.tr("from"));
            left.add(item);
            item = new Label("$" + property.price().min().getStringView());
            item.setStyleName(PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardLeftItem);
            left.add(item);

            add(content, DockPanel.CENTER);
            add(left, DockPanel.WEST);

        }

        public HandlerRegistration addViewDetailsClickHandler(ClickHandler h) {
            return viewDetailsItem.addClickHandler(h);
        }
    }
}
