/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.financial.dashboard;

import com.pyx4j.site.client.IsView;

import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPayInfoDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPaySummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.LatestActivitiesDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodInfoDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodSummaryDTO;

public interface FinancialDashboardView extends IsView {

    public interface FinancialDashboardPresenter {

        void viewCurrentBill();

        void viewBillilngHistory();

        void viewTransactionHistory();

        void makePayment();

        void addPaymentMethod();

        void deletePaymentMethod(PaymentMethodInfoDTO paymentMethod);

        void viewPaymentMethod(PaymentMethodInfoDTO paymentMethod);

        void addAutoPay();

        void deletePreauthorizedPayment(AutoPayInfoDTO autoPay);

        void viewPreauthorizedPayment(AutoPayInfoDTO autoPay);
    }

    void setPresenter(FinancialDashboardPresenter presenter);

    void populate(BillingSummaryDTO value);

    void populate(AutoPaySummaryDTO value);

    void populate(LatestActivitiesDTO value);

    void populate(PaymentMethodSummaryDTO value);
}
