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
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

import com.propertyvista.portal.domain.ptapp.AvailableUnitsByFloorplan;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.domain.ptapp.UnitSelectionCriteria;
import com.propertyvista.portal.ptapp.client.ui.ApartmentView;
import com.propertyvista.portal.ptapp.client.ui.ApartmentViewPresenter;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

public class ApartmentActivity extends WizardStepActivity<UnitSelection, ApartmentViewPresenter> implements ApartmentViewPresenter {

    static ApartmentService srv = GWT.create(ApartmentService.class);

    @Inject
    public ApartmentActivity(ApartmentView view) {
        super(view, UnitSelection.class, srv);
    }

    @Override
    public void selectByDates(UnitSelectionCriteria entity) {
        srv.retrieveUnitSelection(new DefaultAsyncCallback<AvailableUnitsByFloorplan>() {

            @Override
            public void onSuccess(AvailableUnitsByFloorplan result) {
                log.debug("received AvailableUnits {}", result.units().size());
                ((ApartmentView) getView()).setAvailableUnits(result);
            }

        }, entity);

    }
}
