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

import org.apache.wicket.PageParameters;
import org.apache.wicket.Response;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.resources.StyleSheetReference;
import org.apache.wicket.resource.TextTemplateResourceReference;

import templates.TemplateResources;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.panels.SecondaryNavigationPanel;

public class StaticPage extends BasePage {

    public StaticPage(final PageParameters parameters) {
        super(parameters);
        setVersioned(false);

        WebMarkupContainer mainPanel = new WebMarkupContainer("mainPanel");
        add(mainPanel);

        String baseColor = ((PMSiteSession) getSession()).getContentManager().getSiteDescriptor().baseColor().getValue();
        add(new StyleSheetReference("static_css", new TextTemplateResourceReference(TemplateResources.class, "static" + getPmsiteStyle() + ".css", "text/css",
                new StylesheetTemplateModel(baseColor))));

        final PageDescriptor descriptor = ((PMSiteSession) getSession()).getContentManager().getStaticPageDescriptor(parameters);

        final PMSiteContentManager contentManager = ((PMSiteSession) getSession()).getContentManager();

        SecondaryNavigationPanel secondaryNavigationPanel = new SecondaryNavigationPanel("secondaryNavig", this);

        add(secondaryNavigationPanel);

        if (secondaryNavigationPanel.getViewSize() == 0) {
            mainPanel.add(new SimpleAttributeModifier("style", "width:100%"));
        }

        mainPanel.add(new Label("caption", PMSiteContentManager.getCaption(descriptor, contentManager.getLocale())));

        mainPanel.add(new WebComponent("content") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                Response response = getRequestCycle().getResponse();

                EntityQueryCriteria<PageContent> pageContentCriteria = EntityQueryCriteria.create(PageContent.class);
                pageContentCriteria.add(PropertyCriterion.eq(pageContentCriteria.proto().locale(), ((PMSiteSession) getSession()).getContentManager()
                        .getLocale()));
                pageContentCriteria.add(PropertyCriterion.eq(pageContentCriteria.proto().descriptor(), descriptor));

                List<PageContent> pages = Persistence.service().query(pageContentCriteria);
                if (pages.size() == 1) {
                    response.write(pages.get(0).content().getStringView());
                }
            }

        });

    }
}
