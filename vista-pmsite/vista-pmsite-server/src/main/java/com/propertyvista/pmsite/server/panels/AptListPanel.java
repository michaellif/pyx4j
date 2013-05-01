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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pyx4j.commons.MinMaxPair;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.rpc.SystemState;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.WicketUtils.AttributeClassModifier;
import com.propertyvista.pmsite.server.model.WicketUtils.JSActionLink;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;
import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.pmsite.server.pages.AptListPage;
import com.propertyvista.portal.server.portal.PropertyFinder;

public class AptListPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(AptListPanel.class);

    public AptListPanel(String id, CompoundPropertyModel<List<Building>> model, AptListPage.ViewMode viewMode) {
        super(id, model);

        Panel aptMap;
        if (SystemMaintenance.getExternalConnectionsState().equals(SystemState.Online)) {
            aptMap = new GwtInclude("gwtInclude");
        } else {
            aptMap = new EmptyPanel("gwtInclude");
        }
        WebMarkupContainer aptList = new WebMarkupContainer("aptResultList");
        aptList.add(new ListView<Building>("aptListItem", model) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Building> item) {
                Building propInfo = item.getModelObject();
                String propCode = propInfo.propertyCode().getValue();
                // PropertyDetailsDTO
                item.add(new SimpleImage("picture", PMSiteContentManager.getFistVisibleMediaImgUrl(propInfo.media(), ThumbnailSize.small)));
                item.add(new BookmarkablePageLink<Void>("aptDetails", AptDetailsPage.class, new PageParameters().add(PMSiteApplication.ParamNameBuilding,
                        propCode)));
                if (SystemMaintenance.getExternalConnectionsState().equals(SystemState.Online)) {
                    GeoPoint pt = propInfo.info().address().location().getValue();
                    item.add(new JSActionLink("aptMapview", "showLocation(" + pt.getLat() + ", " + pt.getLng() + ")", false));
                } else {
                    item.add(new Label("aptMapview").setVisible(false));
                }
                // address
                AddressStructured addr = propInfo.info().address();
                item.add(new Label("address", addr != null ? addr.getStringView() : ""));
                String desc = propInfo.marketing().description().getValue();
                if (desc == null) {
                    desc = "";
                }
                // description
                if (ApplicationMode.isDevelopment()) {
                    desc += " (" + propInfo.propertyCode().getValue() + ")";
                }
                item.add(new Label("description", desc));

                final Map<Floorplan, List<AptUnit>> fpUnits = PropertyFinder.getBuildingFloorplans(propInfo);
                item.add(new ListView<Floorplan>("types", new ArrayList<Floorplan>(fpUnits.keySet())) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(ListItem<Floorplan> item) {
                        Floorplan fp = item.getModelObject();
                        String name = fp.marketingName().getValue();
                        if (name == null) {
                            name = fp.bedrooms().isNull() ? "" : fp.bedrooms().getValue() + " " + i18n.tr("Bedroom");
                        }
                        item.add(new Label("typeName", name));

                        String info = i18n.tr("No information found");
                        if (!fp.bathrooms().isNull() && !fp.bathrooms().isNull()) {
                            MinMaxPair<BigDecimal> minMaxMarketRent = PropertyFinder.getMinMaxMarketRent(fpUnits.get(fp));
                            info = SimpleMessageFormat.format(i18n.tr("{0} Bed, {1} Bath, {2,choice,null#price not available|!null#from $ {2}}"), fp.bedrooms()
                                    .getValue(), fp.bathrooms().getValue(), minMaxMarketRent.getMin());
                        }
                        item.add(new Label("typeInfo", info));
                    }
                });

                final List<BuildingAmenity> amenities = PropertyFinder.getBuildingAmenities(propInfo);
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
