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

import org.apache.wicket.markup.html.IHeaderResponse;

import templates.TemplateResources;

import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.pmsite.server.PMSiteClientPreferences;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.AdvancedSearchCriteriaPanel;
import com.propertyvista.shared.config.VistaSettings;

public class FindAptPage extends BasePage {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(FindAptPage.class);

    public FindAptPage() {
        super();
        setVersioned(false);

        // set aptlist view mode preference to Map
        PMSiteClientPreferences.setClientPref("aptListMode", AptListPage.ViewMode.map.name());

        add(new AdvancedSearchCriteriaPanel());
    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr("Find Apartment");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "findapt.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);

        if (VistaSettings.googleMapApiVersion.equals("2")) {
            response.renderJavaScriptReference(ServletUtils.getRequestProtocol(Context.getRequest()) + "://maps.google.com/maps?file=api&v="
                    + VistaSettings.googleMapApiVersion + "&sensor=false&key=" + VistaDeployment.getPortalGoogleAPIKey());
        } else {
            response.renderJavaScriptReference(ServletUtils.getRequestProtocol(Context.getRequest()) + "://maps.googleapis.com/maps/api/js?key="
                    + VistaDeployment.getPortalGoogleAPIKey() + "&sensor=false");
        }

        super.renderHead(response);

    }
}
