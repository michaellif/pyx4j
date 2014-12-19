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
package com.propertyvista.crm.client.ui.crud.customer.screening;

import com.google.gwt.core.client.GWT;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.services.customer.screening.LeaseParticipantScreeningVersionService;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

public class CustomerScreeningViewerViewImpl extends CrmViewerViewImplBase<LeaseParticipantScreeningTO> implements CustomerScreeningViewerView {

    public CustomerScreeningViewerViewImpl() {
        super(true);
        setForm(new CustomerScreeningForm(this));
        enableVersioning(LeaseParticipantScreeningTO.LeaseParticipantScreeningTOV.class,
                GWT.<LeaseParticipantScreeningVersionService> create(LeaseParticipantScreeningVersionService.class));
    }

    @Override
    public void populate(LeaseParticipantScreeningTO value) {
        super.populate(value);

        setCaption(value.data().screene().getStringView() + " " + getCaption());
    }
}