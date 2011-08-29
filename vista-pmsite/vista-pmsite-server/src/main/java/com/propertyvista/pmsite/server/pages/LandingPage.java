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

import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;

import com.pyx4j.entity.server.PersistenceServicesFactory;

import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.panels.QuickSearchCriteriaPanel;

public class LandingPage extends BasePage {

    public LandingPage() {
        super();
        final PageDescriptor descriptor = ((PMSiteSession) getSession()).getContentManager().getLandingPage();

        add(new QuickSearchCriteriaPanel());

        add(new WebComponent("landing_content") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                Response response = getRequestCycle().getResponse();

                //TODO make image component for landing page banner
                //https://cwiki.apache.org/WICKET/how-to-load-an-external-image.html
                response.write("<div style='height: 375px; position: relative; width: 960px;'>");
                response.write("<img style='position: absolute; bottom:0; left:0' src='resources/templates.TemplateResources/images/template0/landing.png' alt=''>");
                response.write("</div>");

                PersistenceServicesFactory.getPersistenceService().retrieve(descriptor.content());
                response.write(descriptor.content().content().getStringView());
            }
        });

    }

}
