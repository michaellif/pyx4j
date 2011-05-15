/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.site.rpc.AppPlace;

public class MainNavigMenu {

    private final List<AppPlace> menu;

    public MainNavigMenu() {
        menu = new ArrayList<AppPlace>(10);
    }

    public List<AppPlace> getMenuItems() {
        return menu;
    }

    public void addMenuItem(AppPlace menuItem) {
        menu.add(menuItem);
    }

}
