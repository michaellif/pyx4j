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
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.tester.client.ui;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class NavigationViewImpl extends Tree implements NavigationView {

    public NavigationViewImpl() {
        /**
         * TODO replace with real tree structure
         */
        TreeItem ccompnode = new TreeItem("CComponent Tests");
        ccompnode.addItem("CButton");
        ccompnode.addItem("CCheckBox");
        ccompnode.addItem("Etc...");
        this.addItem(ccompnode);
    }

}
