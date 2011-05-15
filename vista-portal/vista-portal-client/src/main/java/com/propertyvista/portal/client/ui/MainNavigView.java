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
package com.propertyvista.portal.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.domain.site.MainNavig;

public interface MainNavigView extends IsWidget {

    public void setPresenter(MainNavigPresenter presenter);

    public interface MainNavigPresenter {
        public void navigTo(Place place);

        public String getNavigLabel(AppPlace place);

        public String getCaption(AppPlace place);

        public Place getWhere();

        public MainNavig getMainNavig();

    }
}