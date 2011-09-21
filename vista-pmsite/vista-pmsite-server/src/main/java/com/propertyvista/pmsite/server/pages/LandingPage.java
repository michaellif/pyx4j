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

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.request.Response;
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
        setVersioned(false);

        add(new QuickSearchCriteriaPanel());

        add(new WebComponent("landing_content") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                Response response = getRequestCycle().getResponse();

                //TODO make image component for landing page banner
                //https://cwiki.apache.org/WICKET/how-to-load-an-external-image.html
                response.write("<img style='position: absolute; bottom:0; left:0' src='wicket/resource/templates.TemplateResources/images/template"
                        + getPmsiteStyle() + "/landing.png' alt=''>");
            }
        });

        add(new NewsPanel("newsPanel"));
        add(new PromoPanel("promoPanel"));
        add(new TestimPanel("testimPanel"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String baseColor = ((PMSiteSession) getSession()).getContentManager().getSiteDescriptor().baseColor().getValue();
        TextTemplateResourceReference refCSS = new TextTemplateResourceReference(TemplateResources.class, "landing" + getPmsiteStyle() + ".css", "text/css",
                new StylesheetTemplateModel(baseColor));
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }

}
