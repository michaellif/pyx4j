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
package com.propertyvista.portal.client.ui.residents.payment.autopay;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentListDTO.itemDTO;

public interface PreauthorizedPaymentsView extends IsWidget {

    interface Presenter {

        void viewPaymentMethod(itemDTO preauthorizedPayment);

        void addPreauthorizedPayment();

        void deletePreauthorizedPayment(itemDTO preauthorizedPayment);
    }

    void populate(List<itemDTO> preauthorizedPayments);

    void setPresenter(Presenter presenter);
}
