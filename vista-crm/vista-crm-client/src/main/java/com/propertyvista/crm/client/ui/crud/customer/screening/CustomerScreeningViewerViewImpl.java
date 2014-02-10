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
package com.propertyvista.crm.client.ui.crud.customer.screening;

import com.google.gwt.core.client.GWT;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.services.customer.screening.CustomerScreeningVersionService;
import com.propertyvista.dto.CustomerScreeningDTO;

public class CustomerScreeningViewerViewImpl extends CrmViewerViewImplBase<CustomerScreeningDTO> implements CustomerScreeningViewerView {

    public CustomerScreeningViewerViewImpl() {
        setForm(new CustomerScreeningForm(this));
        enableVersioning(CustomerScreeningDTO.CustomerScreeningV.class, GWT.<CustomerScreeningVersionService> create(CustomerScreeningVersionService.class));
    }

    @Override
    public void populate(CustomerScreeningDTO value) {
        super.populate(value);

        setCaption(value.screene().getStringView() + " " + getCaption());
    }
}