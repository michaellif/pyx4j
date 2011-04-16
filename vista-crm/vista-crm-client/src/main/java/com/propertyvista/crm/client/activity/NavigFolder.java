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
import java.util.Iterator;
import java.util.List;

import com.pyx4j.site.rpc.AppPlace;

public class NavigFolder {

    private final String title;

    private final List<AppPlace> navigItems;

    public NavigFolder(String title) {
        this.title = title;
        navigItems = new ArrayList<AppPlace>();
    }

    public Iterator<AppPlace> getNavigItemsIterator() {
        return navigItems.iterator();
    }

    public void addNavigItem(AppPlace item) {
        navigItems.add(item);
    }

    public String getTitle() {
        return title;
    }

}
