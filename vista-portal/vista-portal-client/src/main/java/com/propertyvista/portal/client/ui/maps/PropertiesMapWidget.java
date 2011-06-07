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
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.maps;

import java.util.HashMap;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.propertyvista.common.domain.IAddress;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderItemViewerDecorator;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.geo.CircleOverlay;
import com.pyx4j.gwt.geo.MapUtils;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class PropertiesMapWidget extends AbstractMapWidget {

    private final HashMap<PropertyDTO, Marker> markers = new HashMap<PropertyDTO, Marker>();

    private PropertyListDTO propertyList;

    private GeoPoint geoPoint;

    private double distance;

    private CircleOverlay distanceOverlay;

    public static String PROPERTY_CARD_STYLE_PREFIX = "PropertyCard";

    public static enum StyleSuffix implements IStyleSuffix {
        CardHeader, CardContent, CardImage, CardMenu, CardMenuItem
    }

    private static I18n i18n = I18nFactory.getI18n(BaseFolderItemViewerDecorator.class);

    public PropertiesMapWidget() {
        super("100%", "500px");
    }

    @Override
    protected void mapsLoaded() {

        super.mapsLoaded();

        if (propertyList != null && !propertyList.properties().isNull()) {
            populate(propertyList);
        }

        if (geoPoint != null) {
            setDistanceOverlay(geoPoint, distance);
        }

    }

    public void populate(PropertyListDTO propertyList) {
        this.propertyList = propertyList;

        if (isMapLoadComplete()) {
            for (Marker marker : markers.values()) {
                getMap().removeOverlay(marker);
            }
            LatLngBounds bounds = LatLngBounds.newInstance();
            markers.clear();
            for (PropertyDTO property : propertyList.properties()) {
                Marker marker = createMarker(property);
                if (marker != null) {
                    getMap().addOverlay(marker);
                    markers.put(property, marker);
                    bounds.extend(marker.getLatLng());
                }
            }
            //TODO calc base on  markers
            getMap().setCenter(bounds.getCenter());
            int zoomLevel = getMap().getBoundsZoomLevel(bounds) - 1;
            if (zoomLevel > 10) {
                zoomLevel = 10;
            }
            getMap().setZoomLevel(zoomLevel);
        }
    }

    public void setDistanceOverlay(GeoPoint geoPoint, final double distance) {
        this.geoPoint = geoPoint;
        this.distance = distance;
        if (isMapLoadComplete()) {
            LatLng latLng = MapUtils.newLatLngInstance(geoPoint);
            if (distanceOverlay != null) {
                getMap().removeOverlay(distanceOverlay);
                distanceOverlay = null;
            }
            if (latLng != null && distance != 0) {
                distanceOverlay = new CircleOverlay(latLng, distance, "green", 2, 0.4, "green", 0.1);
                getMap().addOverlay(distanceOverlay);
            }
            if (latLng != null) {
                getMap().setCenter(latLng, 14 - (int) Math.ceil(Math.log(distance) / Math.log(2)));
            } else {
                LatLng pos = LatLng.newInstance(43.7571145, -79.5082499);
                getMap().setCenter(pos, 10);
            }
        }
    }

    private Marker createMarker(final PropertyDTO property) {
        MarkerOptions markerOptions = MarkerOptions.newInstance();
        markerOptions.setTitle(property.address().getStringView());

        Icon icon = Icon.newInstance(PortalImages.INSTANCE.mapMarker().getURL());
        //TODO get shadow URL
        //icon.setShadowURL(FmRiaResources.INSTANCE.mapMarkerHouseShadow().getURL());
        icon.setIconSize(Size.newInstance(38, 41));
        icon.setShadowSize(Size.newInstance(44, 35));
        icon.setIconAnchor(Point.newInstance(15, 20));
        icon.setInfoWindowAnchor(Point.newInstance(15, 5));
        markerOptions.setIcon(icon);

        if (!property.location().isNull()) {
            final Marker marker = new Marker(MapUtils.newLatLngInstance(property.location().getValue()), markerOptions);

            marker.addMarkerClickHandler(new MarkerClickHandler() {

                @Override
                public void onClick(MarkerClickEvent event) {
                    showMarker(property);

                }
            });
            return marker;
        } else {
            return null;
        }
    }

    public void showMarker(PropertyDTO property) {
        getMap().getInfoWindow().open(markers.get(property), new InfoWindowContent(new PropertyCard(property)));
    }

    public class PropertyCard extends DockPanel {

        private final Anchor viewDetailsItem;

        public PropertyCard(final PropertyDTO property) {
            setStyleName(PROPERTY_CARD_STYLE_PREFIX);
            setSize("100%", "100%");
            getElement().getStyle().setProperty("minHeight", "100px");
            SimplePanel header = new SimplePanel();
            header.setSize("100%", "15%");
            header.setStyleName(PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardHeader);
            header.setWidget(new Label(formatAddress(property.address())));

            FlowPanel contentHolder = new FlowPanel();
            contentHolder.setSize("100%", "70%");

            SimplePanel imgEnvelope = new SimplePanel();
            imgEnvelope.getElement().getStyle().setHeight(50, Unit.PX);
            imgEnvelope.getElement().getStyle().setWidth(70, Unit.PX);
            imgEnvelope.getElement().getStyle().setFloat(Float.LEFT);
            SimplePanel imageHolder = new SimplePanel();
            imageHolder.setStyleName(PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardImage);
            imageHolder.setSize("100%", "100%");
            imageHolder.getElement().getStyle().setProperty("minHeight", "50px");
            if (property.mainMedia().isNull()) {
                imageHolder.setWidget(new Image(PortalImages.INSTANCE.noImage()));
            } else {
                imageHolder.setWidget(new Image("media/" + property.mainMedia().getValue().toString() + "/" + ThumbnailSize.small.name() + ".jpg"));
            }
            imgEnvelope.setWidget(imageHolder);

            SimplePanel cEnvelope = new SimplePanel();
            cEnvelope.getElement().getStyle().setHeight(100, Unit.PCT);
            cEnvelope.getElement().getStyle().setFloat(Float.LEFT);
            SimplePanel content = new SimplePanel();
            content.setStyleName(PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardContent);
            content.setHeight("100%");
            content.getElement().getStyle().setMarginLeft(15, Unit.PX);
            content.setWidget(new Label(formatFloorplans(property.floorplanNames())));
            cEnvelope.setWidget(content);
            contentHolder.add(imgEnvelope);
            contentHolder.add(cEnvelope);

            SimplePanel footer = new SimplePanel();
            footer.setSize("100%", "15%");
            footer.setStyleName(PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardMenu);
            viewDetailsItem = new Anchor(i18n.tr("View Details"));
            viewDetailsItem.setStyleName(PROPERTY_CARD_STYLE_PREFIX + StyleSuffix.CardMenuItem);
            viewDetailsItem.getElement().getStyle().setFloat(Float.LEFT);
            viewDetailsItem.getElement().getStyle().setFontWeight(FontWeight.BOLD);

            viewDetailsItem.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    //TODO navigation done bypassing activities. Not sure if this is correct
                    AppPlace place = new PortalSiteMap.FindApartment.ApartmentDetails();
                    HashMap<String, String> args = new HashMap<String, String>();
                    args.put(PortalSiteMap.ARG_PROPERTY_ID, property.id().getValue().toString());
                    place.setArgs(args);
                    AppSite.getPlaceController().goTo(place);
                }
            });

            footer.setWidget(viewDetailsItem);

            add(header, DockPanel.NORTH);
            add(contentHolder, DockPanel.CENTER);
            add(footer, DockPanel.SOUTH);

        }

        public HandlerRegistration addViewDetailsClickHandler(ClickHandler h) {
            return viewDetailsItem.addClickHandler(h);
        }

        private String formatAddress(IAddress address) {
            if (address.isNull())
                return "";

            StringBuffer addrString = new StringBuffer();

            addrString.append(address.street1().getStringView());
            if (!address.street2().isNull()) {
                addrString.append(" ");
                addrString.append(address.street2().getStringView());
            }

            if (!address.city().isNull()) {
                addrString.append(", ");
                addrString.append(address.city().getStringView());
            }

            if (!address.province().isNull()) {
                addrString.append(" ");
                addrString.append(address.province().getStringView());
            }

            if (!address.postalCode().isNull()) {
                addrString.append(" ");
                addrString.append(address.postalCode().getStringView());
            }

            return addrString.toString();
        }

        private String formatFloorplans(IPrimitiveSet<String> floorplans) {
            final String delimiter = "/ ";

            if (floorplans.isNull())
                return "";

            StringBuffer planString = new StringBuffer();

            for (String planName : floorplans.getValue()) {
                if (planName != null && !planName.isEmpty()) {
                    planString.append(planName);
                    planString.append(delimiter);
                }
            }
            String finalString = planString.toString();
            if (!finalString.isEmpty()) {
                finalString = finalString.substring(0, finalString.lastIndexOf(delimiter));
            }
            return finalString;
        }
    }
}
