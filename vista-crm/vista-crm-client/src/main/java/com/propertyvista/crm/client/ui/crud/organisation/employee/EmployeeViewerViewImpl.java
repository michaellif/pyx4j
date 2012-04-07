/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;

public class EmployeeViewerViewImpl extends CrmViewerViewImplBase<EmployeeDTO> implements EmployeeViewerView {

    private static final I18n i18n = I18n.get(EmployeeViewerViewImpl.class);

    private final Button passwordAction;

    public EmployeeViewerViewImpl() {
        super(CrmSiteMap.Organization.Employee.class, new EmployeeEditorForm(true));

        // Add actions:        
        passwordAction = new Button(i18n.tr("Change Password"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((EmployeeViewerView.Presenter) getPresenter()).goToChangePassword(getForm().getValue().user().getPrimaryKey(), getForm().getValue().name()
                        .getStringView());
            }
        });
        addHeaderToolbarTwoItem(passwordAction.asWidget());
    }

    @Override
    public void populate(EmployeeDTO value) {
        super.populate(value);
        passwordAction.setVisible(presenter.canEdit());
    }
}