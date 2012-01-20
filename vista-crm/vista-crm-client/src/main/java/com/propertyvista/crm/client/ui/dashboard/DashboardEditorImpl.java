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
package com.propertyvista.crm.client.ui.dashboard;

import java.util.EnumSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.dialogs.SelectTypeDialog;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;

public class DashboardEditorImpl extends CrmEditorViewImplBase<DashboardMetadata> implements DashboardEditor {

    private static final I18n i18n = I18n.get(DashboardEditorImpl.class);

    public DashboardEditorImpl() {
        super(CrmSiteMap.Dashboard.Edit.class, new DashboardEditorForm());
    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<DashboardType> callback) {
        new SelectTypeDialog<DashboardType>(i18n.tr("Select Dashboard Type"), EnumSet.of(DashboardType.system, DashboardType.building)) {
            @Override
            public boolean onClickOk() {
                callback.onSuccess(getSelectedType());
                return true;
            }
        }.show();

    }
}
