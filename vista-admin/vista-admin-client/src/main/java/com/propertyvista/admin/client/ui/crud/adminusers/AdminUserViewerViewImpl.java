/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.adminusers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.AdminUserDTO;

public class AdminUserViewerViewImpl extends AdminViewerViewImplBase<AdminUserDTO> implements AdminUserViewerView {

    private final static I18n i18n = I18n.get(AdminUserViewerViewImpl.class);

    private final Button passwordAction;

    public AdminUserViewerViewImpl() {
        super(AdminSiteMap.Administration.AdminUsers.class);
        setForm(new AdminUserForm(true));

        passwordAction = new Button(i18n.tr("Change Password"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((AdminUserViewerView.Presenter) getPresenter()).goToChangePassword(getForm().getValue().getPrimaryKey(), getForm().getValue().name()
                        .getStringView());
            }
        });
        addHeaderToolbarTwoItem(passwordAction.asWidget());
    }

    @Override
    public void populate(AdminUserDTO value) {
        super.populate(value);
        passwordAction.setVisible(presenter.canEdit());
    }
}
