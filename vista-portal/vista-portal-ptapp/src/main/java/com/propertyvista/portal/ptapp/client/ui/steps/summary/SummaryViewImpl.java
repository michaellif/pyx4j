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
package com.propertyvista.portal.ptapp.client.ui.steps.summary;

import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepViewImpl;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;

public class SummaryViewImpl extends WizardStepViewImpl<SummaryDTO, SummaryViewPresenter> implements SummaryView {

    public SummaryViewImpl() {
        super(new SummaryViewForm());
    }

    @Override
    public void setPresenter(SummaryViewPresenter presenter) {
        super.setPresenter(presenter);
        ((SummaryViewForm) getForm()).setPresenter(getPresenter());
    }

    @Override
    protected String actionName() {
        return (SecurityController.checkBehavior(VistaTenantBehavior.Guarantor) ? i18n.tr("Continue") : super.actionName());
    }
}
