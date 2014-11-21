/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-07-17
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.tenantsure;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.rpc.dto.TenantSureDTO;

public class TenantSureViewerViewImpl extends OperationsViewerViewImplBase<TenantSureDTO> implements TenantSureViewerView {

    private final TenantSureTransactionLister transactionLister = new TenantSureTransactionLister();

    public TenantSureViewerViewImpl() {
        super(true);
        setForm(new TenantSureForm(this));
    }

    @Override
    public TenantSureTransactionLister getTransactionListerView() {
        return transactionLister;
    }

    @Override
    public void populate(TenantSureDTO value) {
        super.populate(value);

        transactionLister.getDataSource().setParentEntityId(value.policy().getPrimaryKey());
        transactionLister.populate();
    }
}
