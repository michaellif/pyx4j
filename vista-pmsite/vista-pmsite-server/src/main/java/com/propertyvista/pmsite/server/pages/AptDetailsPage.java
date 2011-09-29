/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import com.propertyvista.domain.contact.Address;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;

public class AptDetailsPage extends BasePage {

    private static final long serialVersionUID = 1L;

    public AptDetailsPage() {
        super();

        Long propId = null;
        try {
            propId = getRequest().getRequestParameters().getParameterValue("propId").toLong();
        } catch (java.lang.NumberFormatException ignore) {
            // do nothing
        }
        if (propId == null) {
            setResponsePage(AptListPage.class);
            return;
        }

        final Building propInfo = PMSiteContentManager.getBuildingDetails(propId);
        final Map<Floorplan, List<AptUnit>> fpUnits = PMSiteContentManager.getBuildingFloorplans(propInfo);
        final List<BuildingAmenity> amenities = PMSiteContentManager.getBuildingAmenities(propInfo);

        long mediaId = 1;
        if (propInfo.media().size() > 0) {
            mediaId = propInfo.media().get(0).getPrimaryKey().asLong();
        }
        SimpleImage pic = new SimpleImage("picture", PMSiteContentManager.getMediaImgUrl(mediaId, "large"));
        final String picId = "largeView";
        add(pic.add(AttributeModifier.replace("id", picId)));
        add(new ListView<Media>("gallery", propInfo.media()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Media> item) {
                long mediaId = item.getModelObject().getPrimaryKey().asLong();
                String largeSrc = PMSiteContentManager.getMediaImgUrl(mediaId, "large");
                SimpleImage tn = new SimpleImage("thumbnail", PMSiteContentManager.getMediaImgUrl(mediaId, "small"));
                item.add(tn.add(AttributeModifier.replace("onClick", "setImgSrc('" + picId + "','" + largeSrc + "')")));
            }
        });
        Address addr = propInfo.info().address();
        String addrFmt = "";
        if (addr != null) {
            addrFmt += addr.streetNumber().getValue() + " " + addr.streetName().getValue() + ", " + addr.city().getValue() + ", "
                    + addr.province().code().getValue() + " " + addr.postalCode().getValue();
        }
        add(new Label("address", addrFmt));
        add(new Label("description", propInfo.marketing().description().getValue()));

        add(new ListView<Floorplan>("types", new ArrayList<Floorplan>(fpUnits.keySet())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Floorplan> item) {
                Floorplan floorPlan = item.getModelObject();
                String type = floorPlan.name().getValue();
                if (type != null && type.length() > 0) {
                    type += " - ";
                } else {
                    type = "";
                }
                type += floorPlan.bedrooms().getValue() + " Bed, " + floorPlan.bathrooms().getValue() + " Bath";
                String price = "price not available";
                Double minPrice = null;
                for (AptUnit u : fpUnits.get(floorPlan)) {
                    Double _prc = u.financial().unitRent().getValue();
                    if (minPrice == null || minPrice > _prc) {
                        minPrice = _prc;
                    }
                }
                if (minPrice != null) {
                    price = "from $" + String.valueOf(Math.round(minPrice));
                }
                item.add(new Label("type", type + ", " + price));
            }
        });

        add(new ListView<BuildingAmenity>("amenities", amenities) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<BuildingAmenity> item) {
                item.add(new Label("amenity", item.getModelObject().name().getValue()));
            }
        });
        /*
         * for (Media m : propInfo.media()) {
         * System.out.println("==> Bldg media: " + m.getPrimaryKey().asLong());
         * }
         * for (Floorplan fp : plans) {
         * System.out.println("==> Floorplan: " + fp.name().getValue());
         * for (Media m : fp.media()) {
         * System.out.println("--->   Media found: " + m.getPrimaryKey().asLong());
         * }
         * List<AptUnit> units = PMSiteContentManager.getBuildingAptUnits(propInfo, fp);
         * for (AptUnit u : units) {
         * System.out.println("--->   Unit found: " + u.info().number().getValue() + ":" + u.financial().unitRent().getValue());
         * }
         * }
         */
    }

}
