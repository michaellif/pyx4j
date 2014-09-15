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
package com.propertyvista.crm.client.ui.crud.billing.payment;

import com.pyx4j.site.client.ui.backoffice.prime.form.IViewer;

import com.propertyvista.dto.PaymentRecordDTO;

public interface PaymentRecordViewerView extends IViewer<PaymentRecordDTO> {

    interface Presenter extends IViewer.Presenter {

        void schedulePayment();

        void processPayment();

        void clearPayment();

        void rejectPayment(boolean applyNSF);

        void cancelPayment();
    }

}
