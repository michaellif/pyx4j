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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.pyx4j.commons.MinMaxPair;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.PropertyFinder;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.BuildingInfoPanel;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;

public class AptDetailsPage extends BasePage {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(AptDetailsPage.class);

    public static final String pageTitle = "Apartment Details";

    public AptDetailsPage(PageParameters params) {
        super(params);

        String propCode = null;
        propCode = params.get(PMSiteApplication.ParamNameBuilding).toString();
        if (propCode == null) {
            // we may get indexed arg
            String propRef = null;
            propRef = params.get(0).toString();
            if (propRef != null) {
                propCode = propRef.substring(propRef.lastIndexOf("-") + 1);
            }
        }

        final Building building = PropertyFinder.getBuildingDetails(propCode);
        if (building == null) {
            // redirect to findapt page
            redirectOrFail(FindAptPage.class, "Could not get building details: " + propCode);
        }
        final Map<Floorplan, List<AptUnit>> fpUnits = PropertyFinder.getBuildingFloorplans(building);
        if (fpUnits == null || fpUnits.size() < 1) {
            // redirect to findapt page
            redirectOrFail(FindAptPage.class, "No units found");
        }
        final List<BuildingAmenity> amenities = PropertyFinder.getBuildingAmenities(building);

        // left side - building info
        add(new BuildingInfoPanel("buildingInfoPanel", building));

        // right side - floorplan listing
        add(new Label("backButton", i18n.tr("Back")).add(AttributeModifier.replace("onClick", "history.back()")));
        add(new ListView<Floorplan>("units", new ArrayList<Floorplan>(fpUnits.keySet())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Floorplan> item) {
                Floorplan floorPlan = item.getModelObject();
                long mediaId = 0;
                if (floorPlan.media().size() > 0) {
                    mediaId = floorPlan.media().get(0).getPrimaryKey().asLong();
                }
                item.add(new SimpleImage("plan", PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.medium)));
                item.add(new Label("name", floorPlan.marketingName().getValue()));
                item.add(new Label("beds", String.valueOf(floorPlan.bedrooms().getValue())));
                item.add(new Label("bath", String.valueOf(floorPlan.bathrooms().getValue())));

                // get price and area range
                AreaMeasurementUnit areaUnits = AreaMeasurementUnit.sqFeet;
                MinMaxPair<BigDecimal> minMaxMarketRent = PropertyFinder.getMinMaxMarketRent(fpUnits.get(floorPlan));
                MinMaxPair<Integer> minMaxArea = PropertyFinder.getMinMaxAreaInSqFeet(fpUnits.get(floorPlan));

                // price
                String price = i18n.tr("Not Available");
                if (minMaxMarketRent.getMin() != null) {
                    BigDecimal min = DomainUtil.roundMoney(minMaxMarketRent.getMin());
                    BigDecimal max = DomainUtil.roundMoney(minMaxMarketRent.getMax());
                    if (max.compareTo(min) > 0) {
                        price = "$" + min + " - $" + max;
                    } else {
                        price = "$" + min;
                    }
                }
                item.add(new Label("price", price));
                // area
                String area = i18n.tr("Not Available");
                if (minMaxArea.getMin() != null) {
                    area = minMaxArea.getMin() + " - " + minMaxArea.getMax() + " " + areaUnits;
                }
                item.add(new Label("area", area));
                // UnitDetails link
                item.add(new BookmarkablePageLink<Void>("unitDetails", UnitDetailsPage.class, new PageParameters().add(PMSiteApplication.ParamNameFloorplan,
                        floorPlan.id().getValue())));
            }
        });
        add(new Label("description", building.marketing().description().getValue()));
        add(new ListView<BuildingAmenity>("amenities", amenities) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<BuildingAmenity> item) {
                item.add(new Label("amenity", item.getModelObject().name().getValue()));
            }
        });
        add(new BookmarkablePageLink<Void>("requestApmnt", InquiryPage.class, params).setBody(new Model<String>(i18n.tr("Request Appointment"))));

        String applyUrl = AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.prospect, true), true, null,
                ProspectPortalSiteMap.ARG_ILS_BUILDING_ID, building.propertyCode().getValue());
        add(new ExternalLink("applyNow", applyUrl).setBody(new Model<String>(i18n.tr("Apply Now"))));

    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr(pageTitle);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "aptdetails.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
