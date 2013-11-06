/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui;

import com.pyx4j.site.client.IsView;
import com.pyx4j.site.rpc.AppPlace;

public interface MenuView extends IsView {

    public interface MenuPresenter {
        public void navigTo(AppPlace place);

        public AppPlace getWhere();

        public void logout();

    }

    void onLogedOut();

    void onLogedIn(String userName);

    public void setPresenter(MenuPresenter presenter);

}