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
package com.propertyvista.crm.client.ui.crud.lease;

import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public interface LeaseViewerView extends LeaseViewerViewBase<LeaseDTO> {

    interface Presenter extends LeaseViewerViewBase.Presenter {

        void startBilling();

        void notice(LogicalDate date, LogicalDate moveOut);

        void cancelNotice(String decisionReason);

        void evict(LogicalDate date, LogicalDate moveOut);

        void cancelEvict(String decisionReason);

        void sendMail(List<LeaseParticipant> users, EmailTemplateType emailType);

        void activate();

        void cancelLease(String decisionReason);

        void createOffer(LeaseTerm.Type type);
    }

    IListerView<BillDataDTO> getBillListerView();

    IListerView<PaymentRecordDTO> getPaymentListerView();

    IListerView<LeaseAdjustment> getLeaseAdjustmentListerView();

    void reportSendMailActionResult(String message);
}
