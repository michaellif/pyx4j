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
import com.pyx4j.site.client.ui.crud.form.IViewerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerPresenterBase;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public interface LeaseViewerView extends IViewerView<LeaseDTO> {

    interface Presenter extends IViewerView.Presenter, LeaseViewerPresenterBase {

        IListerView.Presenter<BillDTO> getBillListerPresenter();

        IListerView.Presenter<PaymentRecordDTO> getPaymentListerPresenter();

        IListerView.Presenter<LeaseAdjustment> getLeaseAdjustmentListerPresenter();

        void startBilling();

        void notice(LogicalDate date, LogicalDate moveOut);

        void cancelNotice();

        void evict(LogicalDate date, LogicalDate moveOut);

        void cancelEvict();

        void activate();

        void sendMail(List<LeaseParticipant> users, EmailTemplateType emailType);

    }

    IListerView<BillDTO> getBillListerView();

    IListerView<PaymentRecordDTO> getPaymentListerView();

    IListerView<LeaseAdjustment> getLeaseAdjustmentListerView();

    void reportSendMailActionResult(String message);
}
