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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;

import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;

public class DashboardEditorImpl extends CrmEditorViewImplBase<DashboardMetadata> implements DashboardEditor {
    public DashboardEditorImpl() {
        super(CrmSiteMap.Dashboard.Edit.class, new DashboardEditorForm());
    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<DashboardType> callback) {
        new SelectTypeBox() {
            @Override
            public boolean onClickOk() {
                callback.onSuccess(getSelectedType());
                return true;
            }
        }.show();

    }

    private abstract class SelectTypeBox extends OkDialog {

        private RadioButton system;

        public SelectTypeBox() {
            super(i18n.tr("Select Dashboard Type"));

            HorizontalPanel main = new HorizontalPanel();
            main.add(system = new RadioButton(i18n.tr("DashboardType"), DashboardType.system.toString()));
            main.add(new RadioButton(i18n.tr("DashboardType"), DashboardType.building.toString()));
            main.setSpacing(8);
            main.setWidth("100%");
            system.setValue(true);

            setBody(main);
        }

        public DashboardType getSelectedType() {
            return (system.getValue() ? DashboardType.system : DashboardType.building);
        }
    }
}
