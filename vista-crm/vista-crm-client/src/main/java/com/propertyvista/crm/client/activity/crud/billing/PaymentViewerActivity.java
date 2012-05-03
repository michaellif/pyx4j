/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.billing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentViewerActivity extends CrmViewerActivity<PaymentRecordDTO> implements PaymentViewerView.Presenter {

    public PaymentViewerActivity(Place place) {
        super(place, LeaseViewFactory.instance(PaymentViewerView.class), GWT.<AbstractCrudService<PaymentRecordDTO>> create(PaymentCrudService.class));
    }
}
