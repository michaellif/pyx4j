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
package com.propertyvista.crm.client.ui.crud.tenant.application;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.ptapp.MasterApplication.Status;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.dto.MasterApplicationDTO;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.dto.TenantInfoDTO;

public class MasterApplicationViewerViewImpl extends CrmViewerViewImplBase<MasterApplicationDTO> implements MasterApplicationViewerView {

    private final IListerView<ApplicationDTO> applicationLister;

    private final IListerView<TenantInLeaseDTO> tenantLister;

    private final Button approveAction;

    private final Button moreInfoAction;

    private final Button declineAction;

    private final Button cancelAction;

    private final Button checkAction;

    private static final String APPROVE = i18n.tr("Approve");

    private static final String MORE_INFO = i18n.tr("More Info");

    private static final String DECLINE = i18n.tr("Decline");

    private static final String CANCEL = i18n.tr("Cancel");

    public MasterApplicationViewerViewImpl() {
        super(CrmSiteMap.Tenants.MasterApplication.class, true);

        applicationLister = new ListerInternalViewImplBase<ApplicationDTO>(new ApplicationLister());

        tenantLister = new ListerInternalViewImplBase<TenantInLeaseDTO>(new TenantInLeaseLister());

        // Add actions:
        approveAction = new Button(APPROVE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ShowPopUpBox<ActionBox>(new ActionBox(APPROVE)) {
                    @Override
                    protected void onClose(ActionBox box) {
                        if (box.isOk()) {
                            ((MasterApplicationViewerView.Presenter) presenter).approve(box.updateValue(form.getValue(), Status.Approved));
                        }
                    }
                };
            }
        });
        addToolbarItem(approveAction.asWidget());

        moreInfoAction = new Button(MORE_INFO, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ShowPopUpBox<ActionBox>(new ActionBox(MORE_INFO)) {
                    @Override
                    protected void onClose(ActionBox box) {
                        if (box.isOk()) {
                            ((MasterApplicationViewerView.Presenter) presenter).moreInfo(box.updateValue(form.getValue(), Status.InformationRequested));
                        }
                    }
                };
            }
        });
        addToolbarItem(moreInfoAction.asWidget());

        declineAction = new Button(DECLINE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ShowPopUpBox<ActionBox>(new ActionBox(DECLINE)) {
                    @Override
                    protected void onClose(ActionBox box) {
                        if (box.isOk()) {
                            ((MasterApplicationViewerView.Presenter) presenter).decline(box.updateValue(form.getValue(), Status.Declined));
                        }
                    }
                };
            }
        });
        addToolbarItem(declineAction.asWidget());

        cancelAction = new Button(CANCEL, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to cancel the application?"), new Command() {

                    @Override
                    public void execute() {
                        ((MasterApplicationViewerView.Presenter) presenter).cancelApp(form.getValue());
                    }
                });
            }
        });
        addToolbarItem(cancelAction.asWidget());

        checkAction = new Button(i18n.tr("Equifax Check Query"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ShowPopUpBox<SelectTenantsBox>(new SelectTenantsBox()) {
                    @Override
                    protected void onClose(SelectTenantsBox box) {
                        if (box.isOk()) {
//                            ((MasterApplicationViewerView.Presenter) presenter).decline(box.updateValue(form.getValue(), Status.Declined));
                        }
                    }
                };
            }
        });
        addToolbarItem(checkAction.asWidget());

        //set main form here: 
        setForm(new MasterApplicationEditorForm(new CrmViewersComponentFactory()));
    }

    @Override
    public IListerView<ApplicationDTO> getApplicationsView() {
        return applicationLister;
    }

    @Override
    public IListerView<TenantInLeaseDTO> getTenantsView() {
        return tenantLister;
    }

    private class ActionBox extends OkCancelBox {

        private final CTextArea reason = new CTextArea();

        public ActionBox(String title) {
            super(title);
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(true);

            VerticalPanel content = new VerticalPanel();
            content.add(new HTML(i18n.tr("Please fill the reason:")));
            content.add(reason);

            reason.setWidth("100%");
            content.setWidth("100%");
            return content.asWidget();
        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        public MasterApplicationDTO updateValue(MasterApplicationDTO currentValue, Status status) {
            currentValue.status().setValue(status);

            currentValue.decidedBy().setPrimaryKey(ClientContext.getUserVisit().getPrincipalPrimaryKey());
            currentValue.decisionReason().setValue(reason.getValue());
            currentValue.decisionDate().setValue(new LogicalDate());

            return currentValue;
        }
    }

    private class SelectTenantsBox extends OkCancelBox {

        private ListBox list;

        private List<TenantInfoDTO> selectedItems;

        public SelectTenantsBox() {
            super(i18n.tr("Select Tenants To Check"));
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);

            list = new ListBox(true);
            list.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    okButton.setEnabled(list.getSelectedIndex() >= 0);
                }
            });

            for (TenantInfoDTO item : form.getValue().tenantInfo()) {
                list.addItem(item.person().name().getStringView(), item.id().toString());
            }

            list.setVisibleItemCount(5);
            list.setWidth("100%");
            return list.asWidget();
        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        protected boolean onOk() {
            selectedItems = new ArrayList<TenantInfoDTO>(4);
            for (int i = 0; i < list.getItemCount(); ++i) {
                if (list.isItemSelected(i)) {
                    for (TenantInfoDTO item : form.getValue().tenantInfo()) {
                        if (list.getValue(i).contentEquals(item.id().toString())) {
                            selectedItems.add(item);
                        }
                    }
                }
            }
            return super.onOk();
        }

        protected List<TenantInfoDTO> getSelectedItems() {
            return selectedItems;
        }
    }
}