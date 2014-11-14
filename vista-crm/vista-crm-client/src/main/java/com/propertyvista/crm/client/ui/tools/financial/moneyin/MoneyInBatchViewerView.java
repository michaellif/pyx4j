/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView;

import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public interface MoneyInBatchViewerView extends IPrimeViewerView<MoneyInBatchDTO> {

    interface Presenter extends IPrimeViewerView.IPrimeViewerPresenter {

        void showPaymentRecord(Key paymentRecordId);

        void createDownloadableDepositSlipPrintout();

        void postBatch();

        void cancelBatch();

        boolean canPost();

        boolean canCancel();

    }

}
