/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-31
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.tenantsure;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.tenantsure.TenantSureListerView;
import com.propertyvista.operations.rpc.dto.TenantSureDTO;
import com.propertyvista.operations.rpc.services.TenantSureCrudService;

public class TenantSureListerActivity extends AbstractListerActivity<TenantSureDTO> {

    public TenantSureListerActivity(Place place) {
        super(TenantSureDTO.class, place, OperationsSite.getViewFactory().getView(TenantSureListerView.class), GWT
                .<AbstractCrudService<TenantSureDTO>> create(TenantSureCrudService.class));
    }

}
