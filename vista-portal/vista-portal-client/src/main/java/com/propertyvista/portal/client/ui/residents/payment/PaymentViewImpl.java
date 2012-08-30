/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.payment;

import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.client.ui.residents.BasicViewImpl;
import com.propertyvista.portal.client.ui.residents.View;

public class PaymentViewImpl extends BasicViewImpl<PaymentRecordDTO> implements PaymentView {

    public PaymentViewImpl() {
        setForm(new PaymentForm() {
            @Override
            protected void onIAgree(boolean set) {
                getSubmitButton().setEnabled(set);
            }
        });
    }

    @Override
    public void populate(PaymentRecordDTO value) {
        super.populate(value);

        getSubmitButton().setEnabled(false);
    }

    @Override
    public void setPresenter(View.Presenter<PaymentRecordDTO> presenter) {
        super.setPresenter(presenter);
        ((PaymentForm) form).setPresenter((PaymentView.Presenter) presenter);
    }
}
