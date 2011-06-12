/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 7, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import java.util.LinkedList;
import java.util.List;

import com.propertyvista.portal.domain.site.PageDescriptor;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

import com.pyx4j.site.rpc.AppPlace;

public class NavigItem {

    private final AppPlace place;

    private final String caption;

    private final List<NavigItem> secondaryNavigation;

    public NavigItem(AppPlace place, String caption) {
        this.place = place;
        this.caption = caption;
        secondaryNavigation = new LinkedList<NavigItem>();
    }

    public NavigItem(String path, String caption) {
        this.place = new PortalSiteMap.Page();
        this.caption = caption;
        place.putArg(PortalSiteMap.ARG_PAGE_ID, path);
        secondaryNavigation = new LinkedList<NavigItem>();
    }

    public AppPlace getPlace() {
        return place;
    }

    public String getCaption() {
        return caption;
    }

    public static AppPlace convertTypeToPlace(PageDescriptor.Type type) {
        switch (type) {
        case staticContent:
            return new PortalSiteMap.Page();
        case findApartment:
            return new PortalSiteMap.FindApartment();
        case residence:
            return new PortalSiteMap.Residents();
        case landing:
            return new PortalSiteMap.Landing();
        default:
            return new PortalSiteMap.Landing();
        }
    }

    public List<NavigItem> getSecondaryNavigation() {
        return secondaryNavigation;
    }

    public void addSecondaryNavigItem(NavigItem item) {
        if (item != null) {
            secondaryNavigation.add(item);
        }
    }

}
