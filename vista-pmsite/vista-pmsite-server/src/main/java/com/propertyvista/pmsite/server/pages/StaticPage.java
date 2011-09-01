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
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.panels.SecondaryNavigationPanel;

public class StaticPage extends BasePage {

    public StaticPage(final PageParameters parameters) {
        super(parameters);
        final PageDescriptor descriptor = ((PMSiteSession) getSession()).getContentManager().getStaticPageDescriptor(parameters);

        add(new SecondaryNavigationPanel("secondaryNavig", this));

        add(new Label("caption", descriptor.name().getValue()));

        add(new WebComponent("content") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                Response response = getRequestCycle().getResponse();

                EntityQueryCriteria<PageContent> pageContentCriteria = EntityQueryCriteria.create(PageContent.class);
                pageContentCriteria.add(PropertyCriterion.eq(pageContentCriteria.proto().locale(), ((PMSiteSession) getSession()).getContentManager()
                        .getLocale()));

                List<PageContent> pages = PersistenceServicesFactory.getPersistenceService().query(pageContentCriteria);
                if (pages.size() == 1) {
                    response.write(pages.get(0).content().getStringView());
                }
            }
        });

    }

}
