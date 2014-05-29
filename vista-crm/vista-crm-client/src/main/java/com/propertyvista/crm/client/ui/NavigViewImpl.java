/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.site.client.ui.sidemenu.SideMenuItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuList;

import com.propertyvista.crm.client.resources.CrmImages;

public class NavigViewImpl extends ScrollPanel implements NavigView {

    private NavigPresenter presenter;

    public NavigViewImpl() {
        setHeight("100%");

        SideMenuList firstLevel = new SideMenuList();

        {
            SideMenuList list = new SideMenuList();
            list.addMenuItem(new SideMenuItem(null, null, "Item11", CrmImages.INSTANCE.editButton()));
            list.addMenuItem(new SideMenuItem(null, null, "Item12", CrmImages.INSTANCE.editButton()));
            list.addMenuItem(new SideMenuItem(null, null, "Item13", CrmImages.INSTANCE.editButton()));

            firstLevel.addMenuItem(new SideMenuItem(null, list, "Item1", CrmImages.INSTANCE.editButton()));

        }

        {
            SideMenuList list = new SideMenuList();
            firstLevel.addMenuItem(new SideMenuItem(null, list, "Item2", CrmImages.INSTANCE.editButton()));

            list.addMenuItem(new SideMenuItem(null, null, "Item21", CrmImages.INSTANCE.editButton()));
            list.addMenuItem(new SideMenuItem(null, null, "Item22", CrmImages.INSTANCE.editButton()));
            list.addMenuItem(new SideMenuItem(null, null, "Item23", CrmImages.INSTANCE.editButton()));

        }

        {
            SideMenuList list = new SideMenuList();
            list.addMenuItem(new SideMenuItem(null, null, "Item31", CrmImages.INSTANCE.editButton()));
            list.addMenuItem(new SideMenuItem(null, null, "Item32", CrmImages.INSTANCE.editButton()));
            list.addMenuItem(new SideMenuItem(null, null, "Item33", CrmImages.INSTANCE.editButton()));

            firstLevel.addMenuItem(new SideMenuItem(null, list, "Item3", CrmImages.INSTANCE.editButton()));

        }

        {
            SideMenuList list = new SideMenuList();

            SideMenuList list41 = new SideMenuList();
            list41.addMenuItem(new SideMenuItem(null, null, "Item411", CrmImages.INSTANCE.editButton()));
            list41.addMenuItem(new SideMenuItem(null, null, "Item412", CrmImages.INSTANCE.editButton()));

            SideMenuList list42 = new SideMenuList();
            list42.addMenuItem(new SideMenuItem(null, null, "Item421", CrmImages.INSTANCE.editButton()));
            list42.addMenuItem(new SideMenuItem(null, null, "Item422", CrmImages.INSTANCE.editButton()));

            list.addMenuItem(new SideMenuItem(null, list41, "Item41", CrmImages.INSTANCE.editButton()));
            list.addMenuItem(new SideMenuItem(null, list42, "Item42", CrmImages.INSTANCE.editButton()));

            firstLevel.addMenuItem(new SideMenuItem(null, list, "Item4", CrmImages.INSTANCE.editButton()));

        }

        add(firstLevel.asWidget());

    }

    @Override
    public void setPresenter(NavigPresenter presenter) {
        this.presenter = presenter;
    }

}
