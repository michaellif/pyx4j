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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.contact.IAddress;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.PropertyDTO;

public class AptListPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AptListPanel(String id, CompoundPropertyModel<IList<PropertyDTO>> model) {
        super(id, model);

        add(new ListView<PropertyDTO>("aptListInfo", model) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<PropertyDTO> item) {
                PropertyDTO propInfo = item.getModelObject();
                String price = "Not Available";
                if (propInfo.price() != null && propInfo.price().min() != null && propInfo.price().min().getValue() != null) {
                    price = "From $" + String.valueOf(Math.round(propInfo.price().min().getValue()));
                }
                item.add(new Label("price", price));
                IAddress addr = propInfo.address();
                String addrFmt = addr.street1().getValue() + " " + addr.street2().getValue() + ", " + addr.city().getValue() + ", "
                        + addr.province().name().getValue() + ", " + addr.postalCode().getValue();
                item.add(new Label("address", addrFmt));
                item.add(new Label("description", propInfo.description().getValue()));
                item.add(new ListView<AmenityDTO>("amenities", propInfo.amenities()) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(ListItem<AmenityDTO> item) {
                        item.add(new Label("amenity", item.getModelObject().name().getValue()));
                    }
                });
            }
        });
    }
}
