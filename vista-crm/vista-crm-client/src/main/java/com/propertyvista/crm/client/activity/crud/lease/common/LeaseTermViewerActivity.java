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
package com.propertyvista.crm.client.activity.crud.lease.common;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.lease.common.term.LeaseTermViewerView;
import com.propertyvista.crm.client.visor.charges.ChargesVisorController;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.dto.LeaseTermDTO;

public class LeaseTermViewerActivity extends CrmViewerActivity<LeaseTermDTO> implements LeaseTermViewerView.Presenter {

    private ChargesVisorController chargesController;

    private Key currentLeaseId;

    public LeaseTermViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(LeaseTermViewerView.class), GWT.<LeaseTermCrudService> create(LeaseTermCrudService.class));
    }

    @Override
    public ChargesVisorController getChargesVisorController() {
        if (chargesController == null) {
            chargesController = new ChargesVisorController(getView(), currentLeaseId);
        }
        return chargesController;
    }

    @Override
    protected void onPopulateSuccess(LeaseTermDTO result) {
        currentLeaseId = result.lease().getPrimaryKey();
        super.onPopulateSuccess(result);
    }

    @Override
    public void accept() {
        ((LeaseTermCrudService) getService()).acceptOffer(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId());
    }
}
