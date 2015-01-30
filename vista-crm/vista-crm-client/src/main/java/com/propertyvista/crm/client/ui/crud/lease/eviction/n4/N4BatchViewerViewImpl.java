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
package com.propertyvista.crm.client.ui.crud.lease.eviction.n4;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.widgets.client.Button.SecureMenuItem;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.services.legal.eviction.ac.ServiceN4;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchViewerViewImpl extends CrmViewerViewImplBase<N4BatchDTO> implements N4BatchViewerView {

    private static final I18n i18n = I18n.get(N4BatchViewerViewImpl.class);

    private final N4BatchItemLister itemLister = new N4BatchItemLister();

    public N4BatchViewerViewImpl() {
        setForm(new N4BatchForm(this));

        // Issue Forms
        addAction(new SecureMenuItem(i18n.tr("Issue Forms"), new Command() {
            @Override
            public void execute() {
                if (getForm().getValue().isReadyForService().getValue(false)) {
                    ((N4BatchViewerView.Presenter) getPresenter()).serviceBatch(getForm().getValue());
                } else {
                    MessageDialog.error(i18n.tr("Batch Not Ready"), i18n.tr("Batch must be 'Ready For Service'"));
                }
            }
        }, new ActionPermission(ServiceN4.class)));

        // TODO - add IssueWithSummary and ServiceByAutoMail

        addAction(new SecureMenuItem(i18n.tr("Print Forms"), new Command() {
            @Override
            public void execute() {
                if (!getForm().getValue().serviceDate().isNull()) {
                    // TODO - implement
                } else {
                    MessageDialog.error(i18n.tr("Forms Not Issued"), i18n.tr("Issue Forms before downloading"));
                }
            }
        }, new ActionPermission(ServiceN4.class)));
    }

    @Override
    public N4BatchItemLister getItemLister() {
        return itemLister;
    }

    @Override
    public void populate(N4BatchDTO value) {
        super.populate(value);

        itemLister.getDataSource().setParentEntityId(value.getPrimaryKey());
        itemLister.populate();
    }
}
