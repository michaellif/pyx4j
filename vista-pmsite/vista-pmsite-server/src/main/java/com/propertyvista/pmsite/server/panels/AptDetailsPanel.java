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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
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
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;

public class AptDetailsPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AptDetailsPanel(String id, Model<Long> model) {
        super(id);

        Long propId = model.getObject();
        final Building propInfo = PMSiteContentManager.getBuildingDetails(propId);
        final List<Floorplan> plans = PMSiteContentManager.getBuildingFloorplans(propInfo);
        final HashMap<Floorplan, List<AptUnit>> fpUnits = new HashMap<Floorplan, List<AptUnit>>();
        for (Floorplan fp : plans) {
            List<AptUnit> units = PMSiteContentManager.getBuildingAptUnits(propInfo, fp);
            // do some sanity check so we don't render incomplete floorplans
            // TODO - may need to move this to PMSiteContentManager
            if (units.size() > 0) {
                fpUnits.put(fp, units);
            }
        }
        List<BuildingAmenity> amenities = PMSiteContentManager.getBuildingAmenities(propInfo);

        long mediaId = 1;
        if (propInfo.media().size() > 0) {
            mediaId = propInfo.media().get(0).getPrimaryKey().asLong();
        }
        add(new SimpleImage("picture", PMSiteContentManager.getMediaImgUrl(mediaId, "small")));
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
        for (Media m : propInfo.media()) {
            System.out.println("==> Bldg media: " + m.getPrimaryKey().asLong());
        }
        for (Floorplan fp : plans) {
            System.out.println("==> Floorplan: " + fp.name().getValue());
            for (Media m : fp.media()) {
                System.out.println("--->   Media found: " + m.getPrimaryKey().asLong());
            }
            List<AptUnit> units = PMSiteContentManager.getBuildingAptUnits(propInfo, fp);
            for (AptUnit u : units) {
                System.out.println("--->   Unit found: " + u.info().number().getValue() + ":" + u.financial().unitRent().getValue());
            }
        }
    }
}
