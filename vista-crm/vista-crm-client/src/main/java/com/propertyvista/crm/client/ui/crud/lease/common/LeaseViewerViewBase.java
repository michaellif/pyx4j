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
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView;

import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseViewerViewBase<DTO extends LeaseDTO> extends IPrimeViewerView<DTO> {

    interface Presenter extends IPrimeViewerView.IPrimeViewerPresenter {

        void retrieveParticipants(AsyncCallback<List<LeaseTermParticipant<?>>> callback, Boolean includeDependants);

        void navigateParticipant(List<LeaseTermParticipant<?>> users);

        void viewTerm(LeaseTerm leaseTermId);

        void editTerm(LeaseTerm leaseTermId);

        void reserveUnit(int durationHours);

        void releaseUnit();

        void newPayment();
    }

    PaymentLister getPaymentListerView();
}
