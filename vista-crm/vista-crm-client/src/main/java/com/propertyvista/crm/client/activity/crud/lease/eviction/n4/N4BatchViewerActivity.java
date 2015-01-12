/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.activity.crud.lease.eviction.n4;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.lease.eviction.n4.N4BatchViewerView;
import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchCrudService;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchViewerActivity extends CrmViewerActivity<N4BatchDTO> implements N4BatchViewerView.Presenter {

    public N4BatchViewerActivity(CrudAppPlace place) {
        super(N4BatchDTO.class, place, CrmSite.getViewFactory().getView(N4BatchViewerView.class), GWT.<N4BatchCrudService> create(N4BatchCrudService.class));
    }

    @Override
    public void serviceBatch() {
        // TODO - call service
    }

}
