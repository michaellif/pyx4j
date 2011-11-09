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
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.SecondaryNavigationPanel;

public class StaticPage extends BasePage {

    private static final long serialVersionUID = 1L;

    private final String caption;

    public StaticPage(final PageParameters parameters) {
        super(parameters);
        setVersioned(false);

        WebMarkupContainer mainPanel = new WebMarkupContainer("mainPanel");
        add(mainPanel);

        SecondaryNavigationPanel secondaryNavigationPanel = new SecondaryNavigationPanel("secondaryNavig", this);

        add(secondaryNavigationPanel);

        if (secondaryNavigationPanel.getViewSize() == 0) {
            mainPanel.add(AttributeModifier.replace("style", "width:100%"));
        }

        final PageDescriptor descriptor = ((PMSiteWebRequest) getRequest()).getContentManager().getStaticPageDescriptor(parameters);
        caption = ((PMSiteWebRequest) getRequest()).getContentManager().getCaption(descriptor, ((PMSiteWebRequest) getRequest()).getSiteLocale());
        mainPanel.add(new Label("caption", caption));

        mainPanel.add(new WebComponent("content") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                Response response = getRequestCycle().getResponse();

                EntityQueryCriteria<PageContent> pageContentCriteria = EntityQueryCriteria.create(PageContent.class);
                pageContentCriteria.add(PropertyCriterion.eq(pageContentCriteria.proto().locale(), ((PMSiteWebRequest) getRequest()).getSiteLocale()));
                pageContentCriteria.add(PropertyCriterion.eq(pageContentCriteria.proto().descriptor(), descriptor));

                List<PageContent> pages = Persistence.service().query(pageContentCriteria);
                if (pages.size() == 1) {
                    response.write(pages.get(0).content().getStringView());
                }
            }

        });

    }

    @Override
    public String getLocalizedPageTitle() {
        return caption;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        String fileCSS = "static" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
