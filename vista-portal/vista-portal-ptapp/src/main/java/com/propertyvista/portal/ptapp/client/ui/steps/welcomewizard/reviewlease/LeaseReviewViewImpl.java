/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.reviewlease;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepViewImpl;
import com.propertyvista.portal.rpc.ptapp.welcomewizard.LeaseReviewDTO;

public class LeaseReviewViewImpl extends WizardStepViewImpl<LeaseReviewDTO, LeaseReviewPresenter> {

    private final static I18n i18n = I18n.get(LeaseReviewViewImpl.class);

    public LeaseReviewViewImpl() {
        super(new LeaseReviewForm());
    }

    @Override
    protected String actionName() {
        return i18n.tr("Accept and Continue");
    }

}
