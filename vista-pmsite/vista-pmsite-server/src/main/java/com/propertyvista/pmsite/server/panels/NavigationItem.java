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

import org.apache.wicket.PageParameters;

import com.propertyvista.pmsite.server.pages.BasePage;

public class NavigationItem implements Serializable {

    public static final String NAVIG_PARAMETER_NAME = "id";

    private static final long serialVersionUID = 1L;

    private final Class<? extends BasePage> destination;

    private final PageParameters params;

    private final String caption;

    public NavigationItem(Class<? extends BasePage> destination, String caption) {
        this(destination, caption, null);
    }

    public NavigationItem(Class<? extends BasePage> destination, String caption, String pageId) {
        this.destination = destination;
        this.caption = caption;

        params = new PageParameters();
        params.add(NAVIG_PARAMETER_NAME, pageId);

    }

    public Class<? extends BasePage> getDestination() {
        return destination;
    }

    public PageParameters getPageParameters() {
        return params;
    }

    public String getCaption() {
        return caption;
    }
}