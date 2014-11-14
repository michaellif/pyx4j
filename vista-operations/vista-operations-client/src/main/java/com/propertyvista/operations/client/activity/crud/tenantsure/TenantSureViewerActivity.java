/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.tenantsure;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeViewerActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.tenantsure.TenantSureViewerView;
import com.propertyvista.operations.rpc.dto.TenantSureDTO;
import com.propertyvista.operations.rpc.services.TenantSureCrudService;

public class TenantSureViewerActivity extends AbstractPrimeViewerActivity<TenantSureDTO> implements TenantSureViewerView.IViewerPresenter {

    public TenantSureViewerActivity(CrudAppPlace place) {
        super(TenantSureDTO.class, place, OperationsSite.getViewFactory().getView(TenantSureViewerView.class), GWT
                .<AbstractCrudService<TenantSureDTO>> create(TenantSureCrudService.class));
    }

    @Override
    public boolean canEdit() {
        return false;
    }

}
