/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease.common.deposit;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.lease.common.deposit.DepositViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.lease.common.DepositCrudService;
import com.propertyvista.domain.tenant.lease.Deposit;

public class DepositViewerActivity extends CrmViewerActivity<Deposit> implements DepositViewerView.Presenter {

    public DepositViewerActivity(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(DepositViewerView.class), GWT.<AbstractCrudService<Deposit>> create(DepositCrudService.class));
    }
}
