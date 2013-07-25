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
package com.propertyvista.crm.client.activity.crud.customer.tenant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.customer.common.LeaseParticipantEditorActivity;
import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantEditorView;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentDTO;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorActivity extends LeaseParticipantEditorActivity<TenantDTO, TenantCrudService> {

    public TenantEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(TenantEditorView.class), GWT.<TenantCrudService> create(TenantCrudService.class), TenantDTO.class);
    }

    public void createPreauthorizedPayment(AsyncCallback<PreauthorizedPaymentDTO> callback) {
        ((TenantCrudService) getService()).createPreauthorizedPayment(callback, EntityFactory.createIdentityStub(Tenant.class, getEntityId()));
    }
}
