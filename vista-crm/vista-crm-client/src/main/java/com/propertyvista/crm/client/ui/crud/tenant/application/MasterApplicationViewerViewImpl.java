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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
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
import com.propertyvista.domain.tenant.ptapp.MasterApplication.Decision;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.dto.MasterApplicationDTO;
import com.propertyvista.dto.TenantInLeaseDTO;

public class MasterApplicationViewerViewImpl extends CrmViewerViewImplBase<MasterApplicationDTO> implements MasterApplicationViewerView {

    private final IListerView<ApplicationDTO> applicationLister;

    private final IListerView<TenantInLeaseDTO> tenantLister;

    private final Button approveAction;

    private final Button moreInfoAction;

    private final Button declineAction;

    private final Button cancelAction;

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
                            ((MasterApplicationViewerView.Presenter) presenter).approve(box.updateValue(form.getValue(), Decision.Approve));
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
                            ((MasterApplicationViewerView.Presenter) presenter).moreInfo(box.updateValue(form.getValue(), Decision.RequestInfo));
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
                            ((MasterApplicationViewerView.Presenter) presenter).decline(box.updateValue(form.getValue(), Decision.Decline));
                        }
                    }
                };
            }
        });
        addToolbarItem(declineAction.asWidget());

        cancelAction = new Button(CANCEL, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to cancel the application?"), new Runnable() {
                    @Override
                    public void run() {
                        ((MasterApplicationViewerView.Presenter) presenter).cancelApp(form.getValue());
                    }
                });
            }
        });
        addToolbarItem(cancelAction.asWidget());

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

        public MasterApplicationDTO updateValue(MasterApplicationDTO currentValue, Decision decision) {
            currentValue.suggestedDecision().setValue(decision);
            currentValue.decidedBy().setPrimaryKey(ClientContext.getUserVisit().getPrincipalPrimaryKey());
            currentValue.decisionReason().setValue(reason.getValue());
            currentValue.decisionDate().setValue(new LogicalDate());
            return currentValue;
        }
    }

}