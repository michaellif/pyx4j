/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.activity;

import com.propertyvista.portal.client.ptapp.ui.WizardStepPresenter;
import com.propertyvista.portal.client.ptapp.ui.WizardStepView;
import com.propertyvista.portal.domain.pt.IBoundToApplication;
import com.propertyvista.portal.rpc.pt.services.AbstractWizardService;

import com.pyx4j.entity.shared.IEntity;

abstract class WizardStepWithSubstepsActivity<E extends IEntity & IBoundToApplication, T extends WizardStepPresenter<E>> extends WizardStepActivity<E, T> {

    public WizardStepWithSubstepsActivity(WizardStepView<E, T> view, Class<E> clazz, AbstractWizardService<E> wizardServices) {
        super(view, clazz, wizardServices);
    }

    @Override
    protected Long getCurrentTenantId() {
        // get secondary step argument (should be tenant ID for Info and Financial views):
        if (currentPlace != null) {
            String stepArg = currentPlace.getArgs().get(SecondNavigActivity.STEP_ARG_NAME);
            if (stepArg != null) {
                return Long.valueOf(stepArg);
            }
        }
        return null;
    }

}
