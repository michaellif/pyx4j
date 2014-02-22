/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application;

import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.ui.AbstractWizardFormView;

public class ApplicationWizardViewImpl extends AbstractWizardFormView<OnlineApplicationDTO> implements ApplicationWizardView {

    public ApplicationWizardViewImpl() {
        super();
    }

    @Override
    public void populate(OnlineApplicationDTO value) {
        ApplicationWizard applicationWizard = new ApplicationWizard(this, value.feePaymentPolicy().getValue());
        applicationWizard.setPresenter((ApplicationWizardPresenter) getPresenter());
        setWizard(applicationWizard);
        super.populate(value);
    }

    @Override
    public void reset() {
        setWizard(null);
    }

    @Override
    public ApplicationWizard getWizard() {
        return (ApplicationWizard) super.getWizard();
    }
}
