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

import com.pyx4j.site.rpc.AppPlace;

public class NavigItem {

    private final AppPlace place;

    private final String caption;

    private List<NavigItem> secondaryNavigation;

    public NavigItem(AppPlace place, String caption) {
        this.place = place;
        this.caption = caption;
        secondaryNavigation = new LinkedList<NavigItem>();
    }

    public AppPlace getPlace() {
        return place;
    }

    public String getCaption() {
        return caption;
    }

    public List<NavigItem> getSecondaryNavigation() {
        return secondaryNavigation;
    }

    public void addSecondaryNavigItem(NavigItem item) {
        if (item != null) {
            secondaryNavigation.add(item);
        }
    }

    public void setSecondaryNavigation(List<NavigItem> secondaryNavigation) {
        if (secondaryNavigation == null) {
            this.secondaryNavigation = new LinkedList<NavigItem>();
        } else {
            this.secondaryNavigation = secondaryNavigation;
        }
    }

}
