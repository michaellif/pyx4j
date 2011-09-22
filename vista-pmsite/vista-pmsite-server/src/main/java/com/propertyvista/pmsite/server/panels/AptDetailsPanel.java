/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 3, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.propertyvista.domain.contact.Address;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.pmsite.server.PMSiteContentManager;

public class AptDetailsPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AptDetailsPanel(String id, Model<Long> model) {
        super(id);

        Long propId = model.getObject();
        Building propInfo = PMSiteContentManager.getBuildingDetails(propId);
        List<Floorplan> plans = PMSiteContentManager.getBuildingFloorplans(propInfo);
        List<BuildingAmenity> amenities = PMSiteContentManager.getBuildingAmenities(propInfo);

        long mediaId = 1;
        if (propInfo.media().size() > 0) {
            mediaId = propInfo.media().get(0).getPrimaryKey().asLong();
        }
        add(new Image("picture", "").add(AttributeModifier.replace("src", PMSiteContentManager.getMediaImgUrl(mediaId, "small"))));
        Address addr = propInfo.info().address();
        String addrFmt = "";
        if (addr != null) {
            addrFmt += addr.streetNumber().getValue() + " " + addr.streetName().getValue() + ", " + addr.city().getValue() + ", "
                    + addr.province().code().getValue() + " " + addr.postalCode().getValue();
        }
        add(new Label("address", addrFmt));
        add(new Label("description", propInfo.marketing().description().getValue()));

        add(new ListView<Floorplan>("types", plans) {
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
                /*
                 * Double numPrice = null;
                 * if ((numPrice = floorPlan.price().min().getValue()) != null) {
                 * price = "from $" + String.valueOf(Math.round(numPrice));
                 * }
                 */
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
        for (Floorplan fp : plans) {
            System.out.println("===> Floorplan: " + fp.name().getValue());
            for (Media m : fp.media()) {
                System.out.println("===> Media found: " + m.getPrimaryKey().asLong());
            }
            List<AptUnit> units = PMSiteContentManager.getBuildingAptUnits(propInfo, fp);
            for (AptUnit u : units) {
                System.out.println("===> Unit found: " + u.info().number().getValue() + ":" + u.financial().unitRent().getValue());
            }
        }
    }
}
