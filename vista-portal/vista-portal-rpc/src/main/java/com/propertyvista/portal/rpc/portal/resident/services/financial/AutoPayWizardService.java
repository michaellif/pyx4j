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
package com.propertyvista.portal.rpc.portal.resident.services.financial;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPayDTO;

public interface AutoPayWizardService extends AbstractCrudService<AutoPayDTO> {

    void getCurrentAddress(AsyncCallback<InternationalAddress> callback);

    void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback);

    void preview(AsyncCallback<AutopayAgreement> callback, AutoPayDTO currentValue);
}
