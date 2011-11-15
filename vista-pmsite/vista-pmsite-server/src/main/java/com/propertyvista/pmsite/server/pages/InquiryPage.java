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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.BuildingInfoPanel;
import com.propertyvista.pmsite.server.panels.FloorplanInfoPanel;
import com.propertyvista.pmsite.server.panels.InquiryPanel;

public class InquiryPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(InquiryPage.class);

    public InquiryPage(PageParameters params) {
        super(params);
        Long planId = null, propId = null;
        try {
            planId = params.get("fpId").toLong();
        } catch (Exception ignore) {
            // ignore
        }
        try {
            propId = params.get("propId").toLong();
        } catch (Exception ignore) {
            // ignore
        }

        // left side
        Floorplan fp = null;
        Building bld = null;
        if (planId != null) {
            fp = PMSiteContentManager.getFloorplanDetails(planId);
            Persistence.service().retrieve(fp.building());
            add(new FloorplanInfoPanel("infoPanel", fp));
        } else if (propId != null) {
            bld = PMSiteContentManager.getBuildingDetails(propId);
            add(new BuildingInfoPanel("infoPanel", bld));
        } else {
//          throw new RuntimeException();
            throw new RestartResponseException(FindAptPage.class);
        }

        // right side - inquiry form
        add(new Label("backButton", "Back").add(AttributeModifier.replace("onClick", "history.back()")));
        add(new InquiryPanel("inquiryPanel", bld, fp));
    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr("Request Appointment");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        String fileCSS = "inquiry" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
