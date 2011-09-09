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

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.contact.IAddress;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.AttributeClassModifier;
import com.propertyvista.pmsite.server.model.SearchCriteriaModel;
import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.FloorplanPropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyDTO;

public class AptListPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AptListPanel(String id, CompoundPropertyModel<IList<PropertyDTO>> model) {
        super(id, model);

        // TODO - the model object has to be replaced by the SearchCriteriaModel.displayMode component 
        SearchCriteriaModel.DisplayMode displayModeModel = SearchCriteriaModel.DisplayMode.Map;
        RadioGroup<SearchCriteriaModel.DisplayMode> displayModeRadio = new RadioGroup<SearchCriteriaModel.DisplayMode>("displayMode",
                new Model<SearchCriteriaModel.DisplayMode>(displayModeModel)) {
            // TODO replace this BS (server round trip) with JS (onclick handler)
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(Object newMode) {
                // get components
                Component map = getParent().get("aptResultMap");
                Component list = getParent().get("aptResultList");

                // TODO replace this BS with JS
                if (newMode == SearchCriteriaModel.DisplayMode.Map) {
                    if (map != null) {
                        map.add(new AttributeClassModifier("display_none", null));
                    }
                    if (list != null) {
                        list.add(new AttributeClassModifier(null, "display_none"));
                    }
                } else if (newMode == SearchCriteriaModel.DisplayMode.List) {
                    if (list != null) {
                        list.add(new AttributeClassModifier("display_none", null));
                    }
                    if (map != null) {
                        map.add(new AttributeClassModifier(null, "display_none"));
                    }
                }
            }
        };
        displayModeRadio.add(new Radio<SearchCriteriaModel.DisplayMode>("displayMap", new Model<SearchCriteriaModel.DisplayMode>(
                SearchCriteriaModel.DisplayMode.Map)));
        displayModeRadio.add(new Radio<SearchCriteriaModel.DisplayMode>("displayList", new Model<SearchCriteriaModel.DisplayMode>(
                SearchCriteriaModel.DisplayMode.List)));
        add(displayModeRadio.setRequired(true));

        WebMarkupContainer aptList = new WebMarkupContainer("aptResultList");
        add(aptList.add(new AttributeClassModifier(null, "display_none")));
        aptList.add(new ListView<PropertyDTO>("aptListItem", model) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<PropertyDTO> item) {
                PropertyDTO propInfo = item.getModelObject();
                // PropertyDetailsDTO
                long mediaId = 1;
                if (propInfo.mainMedia().getValue() != null) {
                    mediaId = propInfo.mainMedia().getValue().asLong();
                }
                item.add(new Image("picture", PMSiteContentManager.getMediaImgUrl(mediaId, "small")));
                item.add(new BookmarkablePageLink<Void>("aptDetails", AptDetailsPage.class, new PageParameters("propid=" + propInfo.id().getValue())));
                IAddress addr = propInfo.address();
                String addrFmt = addr.street1().getValue() + " " + addr.street2().getValue() + ", " + addr.city().getValue() + ", "
                        + addr.province().name().getValue() + ", " + addr.postalCode().getValue();
                item.add(new Label("address", addrFmt));
                item.add(new Label("description", propInfo.description().getValue()));

                item.add(new ListView<FloorplanPropertyDTO>("types", propInfo.floorplansProperty()) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(ListItem<FloorplanPropertyDTO> item) {
                        FloorplanPropertyDTO floorPlan = item.getModelObject();
                        String type = floorPlan.name().getValue();
                        if (type != null && type.length() > 0) {
                            type += " - ";
                        }
                        type += floorPlan.bedrooms().getValue() + " Bedroom, " + floorPlan.bathrooms().getValue() + " Bathroom";
                        String price = "price not available";
                        Double numPrice = null;
                        if ((numPrice = floorPlan.price().min().getValue()) != null) {
                            price = "from $" + String.valueOf(Math.round(numPrice));
                        }
                        item.add(new Label("type", type + ", " + price));
                    }
                });

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
