/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2015
 * @author stanp
 */
package com.propertyvista.crm.client.activity.crud.lease.eviction.n4;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.lease.eviction.n4.N4BatchItemViewerView;
import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchItemCrudService;
import com.propertyvista.domain.legal.n4.N4BatchItem;

public class N4BatchItemViewerActivity extends CrmViewerActivity<N4BatchItem> {

    public N4BatchItemViewerActivity(CrudAppPlace place) {
        super(N4BatchItem.class, place, CrmSite.getViewFactory().getView(N4BatchItemViewerView.class), GWT
                .<N4BatchItemCrudService> create(N4BatchItemCrudService.class));
    }

}
