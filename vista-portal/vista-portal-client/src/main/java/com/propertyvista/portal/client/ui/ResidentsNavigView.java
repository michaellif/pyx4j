/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.domain.site.MainNavig;

import com.pyx4j.site.rpc.AppPlace;

public interface ResidentsNavigView extends IsWidget {

    public void setPresenter(ResidentsNavigPresenter presenter);

    public interface ResidentsNavigPresenter {
        public void navigTo(Place place);

        public String getNavigLabel(AppPlace place);

        public String getCaption(AppPlace place);

        public Place getWhere();

        public MainNavig getResidentsNavig();

    }

}
