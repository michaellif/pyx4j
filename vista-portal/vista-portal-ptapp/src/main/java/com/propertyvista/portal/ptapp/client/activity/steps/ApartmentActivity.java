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
package com.propertyvista.portal.ptapp.client.activity.steps;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.ptapp.client.ui.steps.ApartmentView;
import com.propertyvista.portal.ptapp.client.ui.steps.ApartmentViewPresenter;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.WizardStepsViewFactory;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;

public class ApartmentActivity extends WizardStepActivity<UnitSelection, ApartmentViewPresenter> implements ApartmentViewPresenter {

    static ApartmentService srv = GWT.create(ApartmentService.class);

    public ApartmentActivity(AppPlace place) {
        super((ApartmentView) WizardStepsViewFactory.instance(ApartmentView.class), UnitSelection.class, srv);
        withPlace(place);
    }

}
