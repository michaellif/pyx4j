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
 */
package com.propertyvista.portal.resident.ui.financial.paymentmethod;

import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.shared.ui.AbstractWizardView;
import com.propertyvista.portal.shared.ui.IWizardView;

public class PaymentMethodWizardViewImpl extends AbstractWizardView<PaymentMethodDTO> implements PaymentMethodWizardView {

    public PaymentMethodWizardViewImpl() {
        super();
        setWizard(new PaymentMethodWizard(this));

    }

    @Override
    public void setPresenter(IWizardView.IWizardFormPresenter<PaymentMethodDTO> presenter) {
        super.setPresenter(presenter);
        ((PaymentMethodWizard) getWizard()).setPresenter((PaymentMethodWizardView.Presenter) presenter);
    }
}
