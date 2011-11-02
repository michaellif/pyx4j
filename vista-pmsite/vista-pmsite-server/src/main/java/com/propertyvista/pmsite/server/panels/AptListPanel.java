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
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.WicketUtils.AttributeClassModifier;
import com.propertyvista.pmsite.server.model.WicketUtils.JSActionLink;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;
import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.pmsite.server.pages.AptListPage;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class AptListPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AptListPanel(String id, CompoundPropertyModel<List<Building>> model, AptListPage.ViewMode viewMode) {
        super(id, model);

        GwtInclude aptMap = new GwtInclude("gwtInclude");
        WebMarkupContainer aptList = new WebMarkupContainer("aptResultList");
        aptList.add(new ListView<Building>("aptListItem", model) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Building> item) {
                Building propInfo = item.getModelObject();
                // PropertyDetailsDTO
                item.add(new SimpleImage("picture", PMSiteContentManager.getFistVisibleMediaImgUrl(propInfo.media(), ThumbnailSize.small)));
                item.add(new BookmarkablePageLink<Void>("aptDetails", AptDetailsPage.class, new PageParameters().add("propId", propInfo.id().getValue())));
                item.add(new JSActionLink("aptMapview", "display_map()", false));
                AddressStructured addr = propInfo.info().address();
                String addrFmt = "";
                if (addr != null) {
                    addrFmt += addr.streetNumber().getValue() + " " + addr.streetName().getValue() + ", " + addr.city().getValue() + ", "
                            + addr.province().code().getValue() + " " + addr.postalCode().getValue();
                }
                item.add(new Label("address", addrFmt));
                item.add(new Label("description", propInfo.marketing().description().getValue()));

                final Map<Floorplan, List<AptUnit>> fpUnits = PMSiteContentManager.getBuildingFloorplans(propInfo);
                item.add(new ListView<Floorplan>("types", new ArrayList<Floorplan>(fpUnits.keySet())) {
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

                final List<BuildingAmenity> amenities = PMSiteContentManager.getBuildingAmenities(propInfo);
                item.add(new ListView<BuildingAmenity>("amenities", amenities) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(ListItem<BuildingAmenity> item) {
                        item.add(new Label("amenity", item.getModelObject().name().getValue()));
                    }
                });
            }
        });

        if (viewMode == AptListPage.ViewMode.map) {
            aptList.add(new AttributeClassModifier(null, "display_none"));
        } else {
            aptMap.add(new AttributeClassModifier(null, "display_none"));
        }
        add(aptMap);
        add(aptList);
    }
}
