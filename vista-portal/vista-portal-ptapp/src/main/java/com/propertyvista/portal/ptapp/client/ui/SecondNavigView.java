/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-12
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.domain.pt.ApplicationWizardStep;

import com.pyx4j.site.rpc.AppPlace;

public interface SecondNavigView extends IsWidget {

    public void setPresenter(SecondNavigPresenter presenter);

    public interface SecondNavigPresenter {
        public void navigTo(Place place);

        public String getNavigLabel(AppPlace place);

        public ApplicationWizardStep getWizardStep();

        public AppPlace getWhere();
    }
}