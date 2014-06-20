/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 14, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import templates.TemplateResources;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.BuildingInfoPanel;
import com.propertyvista.pmsite.server.panels.FloorplanInfoPanel;
import com.propertyvista.server.common.util.PropertyFinder;

public class InquirySuccessPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(InquirySuccessPage.class);

    private static final I18n i18n = I18n.get(InquirySuccessPage.class);

    public InquirySuccessPage(PageParameters params) {
        super(params);
        Long planId = null;
        String propCode = null;
        try {
            planId = params.get(PMSiteApplication.ParamNameFloorplan).toLong();
        } catch (Exception ignore) {
            // ignore
        }
        try {
            propCode = params.get(PMSiteApplication.ParamNameBuilding).toString();
        } catch (Exception ignore) {
            // ignore
        }

        // left side
        Floorplan fp = null;
        Building bld = null;
        BookmarkablePageLink<Void> backLink = null;
        if (planId != null) {
            fp = PropertyFinder.getFloorplanDetails(planId);
            if (fp != null) {
                add(new FloorplanInfoPanel("infoPanel", fp));
                backLink = new BookmarkablePageLink<Void>("backLink", UnitDetailsPage.class, params);
                backLink.setBody(new Model<String>(i18n.tr("Back to") + " " + i18n.tr(UnitDetailsPage.pageTitle)));
            } else {
                redirectOrFail(FindAptPage.class, "Invalid floorplan id: " + planId);
            }
        } else if (propCode != null) {
            bld = PropertyFinder.getBuildingDetails(propCode);
            if (bld != null) {
                add(new BuildingInfoPanel("infoPanel", bld));
                backLink = new BookmarkablePageLink<Void>("backLink", AptDetailsPage.class, params);
                backLink.setBody(new Model<String>(i18n.tr("Back to") + " " + i18n.tr(AptDetailsPage.pageTitle)));
            } else {
                redirectOrFail(FindAptPage.class, "Invalid property code: " + propCode);
            }
        } else {
            redirectOrFail(FindAptPage.class, "No floorplan or property code provided");
        }

        // right side - Continue button
        add(backLink);
    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr("Request Appointment Success");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "inquiryok.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
