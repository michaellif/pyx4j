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

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.portal.client.activity.NavigItem;

public interface MainNavigView extends IsWidget {

    public void setPresenter(MainNavigPresenter presenter);

    public void setMainNavig(List<NavigItem> items);

    public void setSecondaryNavig(Place mainItemPlace, List<NavigItem> secondayItems);

    public void changePlace(Place place);

    public interface MainNavigPresenter {
        public void navigTo(Place place);

        public Place getWhere();

    }
}