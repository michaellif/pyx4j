/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease.financial;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.AbstractViewerActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.lease.financial.InvoiceCreditViewerView;
import com.propertyvista.crm.rpc.dto.lease.financial.InvoiceCreditDTO;
import com.propertyvista.crm.rpc.services.lease.financial.InvoiceCreditCrudService;

public class InvoiceCreditViewerActivity extends AbstractViewerActivity<InvoiceCreditDTO> {

    public InvoiceCreditViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(InvoiceCreditViewerView.class), GWT.<AbstractCrudService<InvoiceCreditDTO>> create(InvoiceCreditCrudService.class));
    }

}
