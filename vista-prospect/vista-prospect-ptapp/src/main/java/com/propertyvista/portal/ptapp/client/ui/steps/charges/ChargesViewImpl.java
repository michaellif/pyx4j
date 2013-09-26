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
package com.propertyvista.portal.ptapp.client.ui.steps.charges;

import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepViewImpl;

public class ChargesViewImpl extends WizardStepViewImpl<Charges, ChargesViewPresenter> implements ChargesView {

    public ChargesViewImpl() {
        super(new ChargesViewForm());
    }

    @Override
    protected String actionName() {
        if (SecurityController.checkBehavior(VistaCustomerBehavior.ProspectiveApplicant)) {
            return super.actionName();
        } else {
            return i18n.tr("Continue");
        }
    }

    @Override
    protected void doAction() {
        if (SecurityController.checkBehavior(VistaCustomerBehavior.ProspectiveApplicant)) {
            super.doAction();
        } else {
            nextAction();
        }
    }
}
