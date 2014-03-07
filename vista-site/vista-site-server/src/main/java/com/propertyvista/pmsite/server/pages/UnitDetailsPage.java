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

import java.util.List;

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
import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.PropertyFinder;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.FloorplanInfoPanel;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.shared.config.VistaFeatures;

public class UnitDetailsPage extends BasePage {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(UnitDetailsPage.class);

    public static final String pageTitle = "Unit Details";

    public UnitDetailsPage(PageParameters params) {
        super(params);

        Long planId = null;
        try {
            planId = params.get(PMSiteApplication.ParamNameFloorplan).toLong();
        } catch (Exception e) {
            // redirect to findapt page
            redirectOrFail(FindAptPage.class, "Invalid parameter: " + PMSiteApplication.ParamNameFloorplan);
        }

        final Floorplan fp = PropertyFinder.getFloorplanDetails(planId);
        if (fp == null) {
            // redirect to findapt page
            redirectOrFail(FindAptPage.class, "Could not get floorplan data");
        }
        final List<AptUnit> fpUnits = PropertyFinder.getFloorplanUnits(fp);
        if (fpUnits == null || fpUnits.size() < 1) {
            // redirect to findapt page
            redirectOrFail(FindAptPage.class, "No units found");
        }
        final List<FloorplanAmenity> amenities = PropertyFinder.getFloorplanAmenities(fp);

        // left side
        add(new FloorplanInfoPanel("floorplanInfoPanel", fp));

        // right side - floorplan details
        AreaMeasurementUnit areaUnits = AreaMeasurementUnit.sqFeet;
        MinMaxPair<Integer> minMaxArea = PropertyFinder.getMinMaxAreaInSqFeet(fpUnits);
        add(new Label("backButton", i18n.tr("Back")).add(AttributeModifier.replace("onClick", "history.back()")));
        add(new Label("name", fp.marketingName().getValue()));
        add(new Label("rooms", "bedrooms: " + fp.bedrooms().getValue() + ", bathrooms: " + fp.bathrooms().getValue()
                + ((minMaxArea.getMin() != null) ? (", " + minMaxArea.getMin() + " - " + minMaxArea.getMax() + " " + areaUnits) : "")));
        add(new Label("description", fp.description().getValue()));
        add(new ListView<FloorplanAmenity>("amenities", amenities) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<FloorplanAmenity> item) {
                FloorplanAmenity am = item.getModelObject();
                String amType = am.type().isNull() ? "N/A" : am.type().getValue().toString();
                item.add(new Label("amenity", amType));
            }
        });
        add(new BookmarkablePageLink<Void>("requestApmnt", InquiryPage.class, params).setBody(new Model<String>(i18n.tr("Request Appointment"))));

        String applyUrl = AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.prospect, true), true, null,
                ProspectPortalSiteMap.ARG_ILS_BUILDING_ID, fp.building().propertyCode().getValue(), //
                ProspectPortalSiteMap.ARG_ILS_FLOORPLAN_ID, fp.name().getValue());

        ExternalLink externalLink = new ExternalLink("applyNow", applyUrl);
        externalLink.setVisible(VistaFeatures.instance().onlineApplication());
        externalLink.setBody(new Model<String>(i18n.tr("Apply Now")));
        add(externalLink);
    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr(pageTitle);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "unitdetails.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);
    }
}
