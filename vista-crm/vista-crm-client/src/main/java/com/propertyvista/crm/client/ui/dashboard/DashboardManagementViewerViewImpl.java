/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.services.selections.SelectCrmUserListService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.security.CrmUser;

public class DashboardManagementViewerViewImpl extends CrmViewerViewImplBase<DashboardMetadata> implements DashboardManagementViewerView {

    private static final I18n i18n = I18n.get(DashboardManagementViewerViewImpl.class);

    public class NewDashboardOwnerSelectionDialog extends EntitySelectorTableVisorController<CrmUser> {

        public NewDashboardOwnerSelectionDialog(IPane parentView) {
            super(parentView, CrmUser.class, false, new ArrayList<CrmUser>(), i18n.tr("Choose a new dashboard owner"));
        }

        @Override
        public void onClickOk() {
            if (!getSelectedItems().isEmpty()) {
                MessageDialog.confirm("", i18n.tr("Are you sure you want to pass your dashboard to {0}?", getSelectedItems().get(0).getStringView()),
                        new Command() {
                            @Override
                            public void execute() {
                                ((DashboardManagementViewerView.Presenter) getPresenter()).changeOwnership(getForm().getValue()
                                        .<DashboardMetadata> createIdentityStub(), getSelectedItems().get(0).<CrmUser> createIdentityStub());
                            }
                        });
            }
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build()
            );//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false));
        }

        @Override
        protected AbstractListService<CrmUser> getSelectService() {
            return GWT.<SelectCrmUserListService> create(SelectCrmUserListService.class);
        }

    }

    private Button takeOwnershipButton;

    private Button changeOwnershipButton;

    public DashboardManagementViewerViewImpl() {
        setForm(new DashboardManagementForm(this));

        addHeaderToolbarItem(takeOwnershipButton = new Button(i18n.tr("Take Ownership"), new Command() {
            @Override
            public void execute() {
                ((DashboardManagementViewerView.Presenter) getPresenter()).takeOwnership(getForm().getValue().<DashboardMetadata> createIdentityStub());
            }
        }));
        addHeaderToolbarItem(changeOwnershipButton = new Button(i18n.tr("Change Ownership"), new Command() {
            @Override
            public void execute() {
                new NewDashboardOwnerSelectionDialog(DashboardManagementViewerViewImpl.this).show();
            }
        }));
    }

    @Override
    public void setTakeOwnershipEnabled(boolean isEnabled) {
        takeOwnershipButton.setVisible(isEnabled);
    }

    @Override
    public void setChangeOwnershipEnabled(boolean isEnabled) {
        changeOwnershipButton.setVisible(isEnabled);
    }

}
