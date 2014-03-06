/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.financial.payment;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.dto.payment.ConvenienceFeeCalculationResponseTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentDTO;
import com.propertyvista.portal.rpc.portal.shared.dto.PaymentConvenienceFeeDTO;
import com.propertyvista.portal.shared.ui.IWizardView;

public interface PaymentWizardView extends IWizardView<PaymentDTO> {

    interface Presenter extends IWizardFormPresenter<PaymentDTO> {

        void getCurrentAddress(AsyncCallback<AddressSimple> callback);

        void getProfiledPaymentMethods(AsyncCallback<List<LeasePaymentMethod>> callback);

        void getConvenienceFee(AsyncCallback<ConvenienceFeeCalculationResponseTO> callback, PaymentConvenienceFeeDTO inData);

    }
}
