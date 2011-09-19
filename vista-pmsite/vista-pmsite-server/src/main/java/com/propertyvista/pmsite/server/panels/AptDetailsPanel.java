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

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.propertyvista.domain.contact.IAddress;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.FloorplanPropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyDTO;

public class AptDetailsPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AptDetailsPanel(String id, CompoundPropertyModel<PropertyDTO> model) {
        super(id, model);

        PropertyDTO propInfo = model.getObject();
        // PropertyDetailsDTO
        long mediaId = 1;
        if (propInfo.mainMedia().getValue() != null) {
            mediaId = propInfo.mainMedia().getValue().asLong();
        }
        add(new Image("picture").add(new SimpleAttributeModifier("src", PMSiteContentManager.getMediaImgUrl(mediaId, "small"))));
        IAddress addr = propInfo.address();
        String addrFmt = addr.street1().getValue() + " " + addr.street2().getValue() + ", " + addr.city().getValue() + ", " + addr.province().name().getValue()
                + ", " + addr.postalCode().getValue();
        add(new Label("address", addrFmt));
        add(new Label("description", propInfo.description().getValue()));

        add(new ListView<FloorplanPropertyDTO>("types", propInfo.floorplansProperty()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<FloorplanPropertyDTO> item) {
                FloorplanPropertyDTO floorPlan = item.getModelObject();
                String type = floorPlan.name().getValue();
                if (type != null && type.length() > 0) {
                    type += " - ";
                } else {
                    type = "";
                }
                type += floorPlan.bedrooms().getValue() + " Bed, " + floorPlan.bathrooms().getValue() + " Bath";
                String price = "price not available";
                Double numPrice = null;
                if ((numPrice = floorPlan.price().min().getValue()) != null) {
                    price = "from $" + String.valueOf(Math.round(numPrice));
                }
                item.add(new Label("type", type + ", " + price));
            }
        });

        add(new ListView<AmenityDTO>("amenities", propInfo.amenities()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<AmenityDTO> item) {
                item.add(new Label("amenity", item.getModelObject().name().getValue()));
            }
        });
    }
}
