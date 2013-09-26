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
import org.apache.wicket.request.resource.CssResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import templates.TemplateResources;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.GwtInclude;

public class ResidentsPage extends CustomizablePage {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ResidentsPage.class);

    private static final I18n i18n = I18n.get(ResidentsPage.class);

    private boolean residentPortalEnabled;

    public ResidentsPage() {
        super();

        // redirect if not enabled
        try {
            residentPortalEnabled = getCM().getSiteDescriptor().residentPortalSettings().enabled().isBooleanTrue();
        } catch (Exception ignore) {
        }
        if (!residentPortalEnabled) {
            log.debug("redirect to page: {}", LandingPage.class);
            throw new RestartResponseException(LandingPage.class);
        }

        // redirect if not secure
        PMSiteApplication.onSecurePage(getRequest());

        if (getCM().isWebsiteEnabled()) {
            add(new GwtInclude("gwtInclude"));
        } else {
            add(new Label(BasePage.RESIDENT_LOGIN_PANEL).add(AttributeModifier.replace("id", "siteAuthInsert")));
            add(new GwtInclude(BasePage.RESIDENT_CUSTOM_CONTENT_PANEL).add(AttributeModifier.replace("id", "residentInsert")));
        }
    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr("Residents");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        if (getCM().isResidentOnlyMode()) {
            response.renderCSSReference(new CssResourceReference(TemplateResources.class, "common/resident.css"));
        } else {
            String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
            String fileCSS = skin + "/" + "resident.css";
            VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                    ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
            response.renderCSSReference(refCSS);
        }
        super.renderHead(response);

    }
}
