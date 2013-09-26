/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity.steps.welcomewizardmockup;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.moveinwizardmockup.MoveInScheduleDTO;
import com.propertyvista.portal.ptapp.client.activity.steps.WizardStepActivity;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.moveinschedule.MoveInSchedulePresenter;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.moveinschedule.MoveInScheduleView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.WizardStepsViewFactory;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcomewizardmockup.MoveInScheduleService;

public class MoveInScheduleActivity extends WizardStepActivity<MoveInScheduleDTO, MoveInSchedulePresenter> {

    public MoveInScheduleActivity(AppPlace place) {
        super(WizardStepsViewFactory.instance(MoveInScheduleView.class), MoveInScheduleDTO.class, GWT
                .<MoveInScheduleService> create(MoveInScheduleService.class));
        withPlace(place);
    }

}
