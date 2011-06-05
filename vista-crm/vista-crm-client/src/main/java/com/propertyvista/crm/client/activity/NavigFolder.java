/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.resources.client.ImageResource;

import com.pyx4j.site.rpc.AppPlace;

public class NavigFolder {

    private final String title;

    private final ImageResource normal;

    private final ImageResource hover;

    private final ImageResource active;

    private final List<AppPlace> navigItems;

    public NavigFolder(String title) {
        this(title, null, null, null);
    }

    public NavigFolder(String title, ImageResource normal, ImageResource hover, ImageResource active) {
        this.title = title;
        this.normal = normal;
        this.hover = hover;
        this.active = active;

        navigItems = new ArrayList<AppPlace>();
    }

    public List<AppPlace> getNavigItems() {
        return navigItems;
    }

    public void addNavigItem(AppPlace item) {
        navigItems.add(item);
    }

    // resources:

    public String getTitle() {
        return title;
    }

    public ImageResource getImageNormal() {
        return normal;
    }

    public ImageResource getImageHover() {
        return hover;
    }

    public ImageResource getImageActive() {
        return active;
    }

}
