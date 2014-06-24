/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-23
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.billing.bill;

import java.util.Collection;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.SecureListerController;
import com.pyx4j.site.client.ui.prime.lister.ILister;

import com.propertyvista.crm.client.ui.crud.billing.bill.BillListerPresenter;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;

public class BillListerController extends SecureListerController<BillDataDTO> implements BillListerPresenter {

    public BillListerController(ILister<BillDataDTO> view) {
        super(view, GWT.<BillCrudService> create(BillCrudService.class), BillDataDTO.class);
    }

    @Override
    public void confirm(Collection<BillDataDTO> bills) {
        ((BillCrudService) getService()).confirm(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                refresh();
            }
        }, new Vector<BillDataDTO>(bills));
    }
}
