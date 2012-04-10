/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant;

import java.util.EnumSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorViewImpl extends CrmEditorViewImplBase<TenantDTO> implements TenantEditorView {
    private static final I18n i18n = I18n.get(TenantEditorViewImpl.class);

    public TenantEditorViewImpl() {
        super(CrmSiteMap.Tenants.Tenant.class, new TenantEditorForm());
    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<Customer.Type> callback) {
        new SelectEnumDialog<Customer.Type>(i18n.tr("Select Tenant Type"), EnumSet.of(Customer.Type.person, Customer.Type.company)) {
            @Override
            public boolean onClickOk() {
                callback.onSuccess(getSelectedType());
                return true;
            }
        }.show();
    }
}
