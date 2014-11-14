/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.transfer;

import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView;

import com.propertyvista.domain.financial.AggregatedTransfer;

public interface AggregatedTransferViewerView extends IPrimeViewerView<AggregatedTransfer> {

    interface Presenter extends IPrimeViewerView.IPrimeViewerPresenter {

        void cancelAction();

    }

    PaymentRecordLister getPaymentsListerView();

    PaymentRecordLister getReturnedPaymentsListerView();

    PaymentRecordLister getRejectedBatchPaymentsListerView();
}
