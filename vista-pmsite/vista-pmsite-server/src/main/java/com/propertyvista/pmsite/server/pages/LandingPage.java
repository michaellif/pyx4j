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

import com.propertyvista.pmsite.server.PMSiteClientPreferences;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.NewsPanel;
import com.propertyvista.pmsite.server.panels.PromoPanel;
import com.propertyvista.pmsite.server.panels.QuickSearchCriteriaPanel;
import com.propertyvista.pmsite.server.panels.TestimPanel;

public class LandingPage extends BasePage {

    private static final long serialVersionUID = 1L;

    public LandingPage() {
        super();
        setStatelessHint(true);
        setVersioned(false);

        // set aptlist view mode preference to Map
        PMSiteClientPreferences.setClientPref("aptListMode", AptListPage.ViewMode.map.name());

        add(new QuickSearchCriteriaPanel());
        add(new NewsPanel("newsPanel"));
        add(new PromoPanel("promoPanel"));
        add(new TestimPanel("testimPanel"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        String fileCSS = "landing" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());

        response.renderCSSReference(refCSS);

        super.renderHead(response);
    }
}
