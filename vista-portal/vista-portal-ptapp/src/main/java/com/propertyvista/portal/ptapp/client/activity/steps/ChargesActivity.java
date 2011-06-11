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
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity.steps;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.ptapp.client.ui.steps.ChargesView;
import com.propertyvista.portal.ptapp.client.ui.steps.ChargesViewPresenter;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.WizardStepsViewFactory;
import com.propertyvista.portal.rpc.ptapp.services.ChargesService;

public class ChargesActivity extends WizardStepActivity<Charges, ChargesViewPresenter> implements ChargesViewPresenter {

    public ChargesActivity(AppPlace place) {
        super((ChargesView) WizardStepsViewFactory.instance(ChargesView.class), Charges.class, (ChargesService) GWT.create(ChargesService.class));
        withPlace(place);
    }
}
