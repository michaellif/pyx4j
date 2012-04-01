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
package com.propertyvista.crm.client.ui.crud.building.catalog.service;

import java.util.EnumSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Service;

public class ServiceEditorViewImpl extends CrmEditorViewImplBase<Service> implements ServiceEditorView {

    private static final I18n i18n = I18n.get(ServiceEditorViewImpl.class);

    public ServiceEditorViewImpl() {
        super(CrmSiteMap.Properties.Service.class, new ServiceEditorForm());
    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<Service.Type> callback) {
        new SelectEnumDialog<Service.Type>(i18n.tr("Select Service Type"), EnumSet.allOf(Service.Type.class)) {
            @Override
            public boolean onClickOk() {
                defaultCaption = getSelectedType().toString();
                callback.onSuccess(getSelectedType());
                return true;
            }

            @Override
            public String defineWidth() {
                return "250px";
            }

            @Override
            public String defineHeight() {
                return "100px";
            }
        }.show();
    }
}
