/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2013
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.resource.IResourceStream;

import templates.TemplateResources;

import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PortalLogoImageResource;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.ResourceImage;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;

public class MyCommunityPage extends ResidentsPage {
    private static final long serialVersionUID = 1L;

    public MyCommunityPage() {
        super();

        if (!getCM().isWebsiteEnabled() && !getCM().isCustomResidentsContentEnabled()) {
            AvailableLocale locale = ((PMSiteWebRequest) getRequest()).getSiteLocale();
            PortalLogoImageResource logo = getCM().getSiteLogo(locale);
            // header
            WebMarkupContainer header = new WebMarkupContainer("header");
            SimpleImage logoImg = new ResourceImage("logoImg", logo.large());
            header.add(logoImg);
            // footer
            WebMarkupContainer footer = new WebMarkupContainer("footer");
            SimpleImage logoImgSmall = new ResourceImage("logoImgSmall", logo.small());
            footer.add(logoImgSmall);
            String pmcInfoHtml = getCM().getPmcInfo(locale);
            footer.add(new Label("pmcInfo", pmcInfoHtml).setEscapeModelStrings(false));

            add(header);
            add(footer);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        if (!getCM().isWebsiteEnabled() && !getCM().isCustomResidentsContentEnabled()) {
            response.renderCSSReference(new CssResourceReference(TemplateResources.class, "myCommunity/css/style.css"));
        }
        super.renderHead(response);
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        if (getCM().isWebsiteEnabled() && containerClass == getClass()) {
            containerClass = ResidentsPage.class;
        }
        return super.getMarkupResourceStream(container, containerClass);
    }
}
