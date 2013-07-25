/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease.financial;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.AbstractViewerActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.lease.financial.InvoiceDebitViewerView;
import com.propertyvista.crm.rpc.dto.lease.financial.InvoiceDebitDTO;
import com.propertyvista.crm.rpc.services.lease.financial.InvoiceDebitCrudService;

public class InvoiceDebitViewerActivity extends AbstractViewerActivity<InvoiceDebitDTO> {

    public InvoiceDebitViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(InvoiceDebitViewerView.class), GWT.<AbstractCrudService<InvoiceDebitDTO>> create(InvoiceDebitCrudService.class));
    }

}
