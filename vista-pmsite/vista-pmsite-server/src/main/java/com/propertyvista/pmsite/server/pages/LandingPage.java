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
import org.apache.wicket.request.resource.ResourceReference.Key;
import org.apache.wicket.resource.TextTemplateResourceReference;

import templates.TemplateResources;

import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
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

        add(new QuickSearchCriteriaPanel());
        add(new NewsPanel("newsPanel"));
        add(new PromoPanel("promoPanel"));
        add(new TestimPanel("testimPanel"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String baseColor = ((PMSiteSession) getSession()).getContentManager().getSiteDescriptor().baseColor().getValue();
        TextTemplateResourceReference refCSS = new TextTemplateResourceReference(TemplateResources.class, "landing" + getPmsiteStyle() + ".css", "text/css",
                new StylesheetTemplateModel(baseColor));

        /*
         * TextTemplateResource is auto-registered in ResourceReferenceRegistry cache
         * by the ResourceReferenceRegistry constructor, if not exist, so no changes will
         * be visible after the app is loaded for the first time.
         * We need to remove and re-register it again to pickup possible changes from CRM
         * in sub-sequential requests.
         * NORE: The Key construction is taken from the ResourceReference.java.
         * It should match the original key used by the TextTemplateResourceReference
         */
        Key rcKey = new Key(refCSS.getScope().getName(), refCSS.getName(), null, null, null);
        getApplication().getResourceReferenceRegistry().unregisterResourceReference(rcKey);
        getApplication().getResourceReferenceRegistry().registerResourceReference(refCSS);

        response.renderCSSReference(refCSS);

        super.renderHead(response);
    }
}
