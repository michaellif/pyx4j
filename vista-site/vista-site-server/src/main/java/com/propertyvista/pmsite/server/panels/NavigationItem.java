/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.io.Serializable;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.pages.BasePage;
import com.propertyvista.pmsite.server.pages.FindAptPage;
import com.propertyvista.pmsite.server.pages.LandingPage;
import com.propertyvista.pmsite.server.pages.ResidentsPage;
import com.propertyvista.pmsite.server.pages.StaticPage;

public class NavigationItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Class<? extends BasePage> destination;

    private final PageDescriptor descriptor;

    private PageParameters params;

    public NavigationItem(PageDescriptor descriptor) {
        this.descriptor = descriptor;
        switch (descriptor.type().getValue()) {
        case staticContent:
            this.destination = StaticPage.class;
            params = PMSiteContentManager.getStaticPageParams(descriptor);
            break;
        case findApartment:
            this.destination = FindAptPage.class;
            break;
        case residents:
            this.destination = ResidentsPage.class;
            break;
        default:
            this.destination = LandingPage.class;
            break;
        }

    }

    public Class<? extends BasePage> getDestination() {
        return destination;
    }

    public PageParameters getPageParameters() {
        return params;
    }

    public PageDescriptor getPageDescriptor() {
        return descriptor;
    }

}