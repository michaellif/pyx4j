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
package com.propertyvista.crm.client.ui.crud.lease.application;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.IsView;
import com.pyx4j.site.client.ui.prime.lister.ILister;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public interface LeaseApplicationViewerView extends LeaseViewerViewBase<LeaseApplicationDTO>, IsView {

    interface Presenter extends LeaseViewerViewBase.Presenter {

        void viewLease();

        void startOnlineApplication();

        void inviteUsers(List<LeaseTermParticipant<?>> users);

        void creditCheck(List<LeaseTermParticipant<?>> users);

        void applicationAction(LeaseApplicationActionDTO action);

        void getCreditCheckServiceStatus(AsyncCallback<PmcEquifaxStatus> callback);

        void isCreditCheckViewAllowed(AsyncCallback<VoidSerializable> callback);
    }

    ILister<PaymentRecordDTO> getPaymentListerView();

    void reportStartOnlineApplicationSuccess();

    void reportInviteUsersActionResult(String message);

    void reportCreditCheckActionResult(String message);

    void reportApplicationApprovalSuccess();

    void reportApplicationApprovalFailure(UserRuntimeException caught);
}
