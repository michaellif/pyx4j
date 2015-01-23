/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 20, 2015
 * @author arminea
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.widgets.client.Button.SecureMenuItem;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.services.maintenance.ac.Schedule;
import com.propertyvista.domain.communication.BroadcastTemplate;

public class BroadcastTemplateViewerViewImpl extends CrmViewerViewImplBase<BroadcastTemplate> implements BroadcastTemplateViewerView {
    private static final I18n i18n = I18n.get(BroadcastTemplateViewerViewImpl.class);

    public BroadcastTemplateViewerViewImpl() {
        setForm(new BroadcastTemplateForm(this));

        addAction(new SecureMenuItem(i18n.tr("Execute"), new Command() {
            @Override
            public void execute() {
                //TODO
            }
        }, DataModelPermission.permissionUpdate(BroadcastTemplate.class)));

        addAction(new SecureMenuItem(i18n.tr("Schedule"), new Command() {
            @Override
            public void execute() {
                ((BroadcastTemplateViewerPresenter) getPresenter()).getSchedulerVisorController().show();
            }
        }, new ActionPermission(Schedule.class)));

    }
}
