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

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.MasterApplicationActionDTO;
import com.propertyvista.domain.tenant.ptapp.MasterApplication.Status;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.dto.ApplicationUserDTO;
import com.propertyvista.dto.MasterApplicationDTO;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.dto.TenantInfoDTO;

public class MasterApplicationViewerViewImpl extends CrmViewerViewImplBase<MasterApplicationDTO> implements MasterApplicationViewerView {

    private final static I18n i18n = I18n.get(MasterApplicationViewerViewImpl.class);

    private final IListerView<ApplicationDTO> applicationLister;

    private final IListerView<TenantInLeaseDTO> tenantLister;

    private final Button inviteAction;

    private final Button checkAction;

    private final Button approveAction;

    private final Button moreInfoAction;

    private final Button declineAction;

    private final Button cancelAction;

    private static final String INVITE = i18n.tr("Invite");

    private static final String APPROVE = i18n.tr("Approve");

    private static final String MORE_INFO = i18n.tr("More Info");

    private static final String DECLINE = i18n.tr("Decline");

    private static final String CANCEL = i18n.tr("Cancel");

    public MasterApplicationViewerViewImpl() {
        super(CrmSiteMap.Tenants.MasterApplication.class, true);

        applicationLister = new ListerInternalViewImplBase<ApplicationDTO>(new ApplicationLister());

        tenantLister = new ListerInternalViewImplBase<TenantInLeaseDTO>(new TenantInLeaseLister());

        // Add actions:

        inviteAction = new Button(INVITE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((MasterApplicationViewerView.Presenter) presenter).retrieveUsers(new DefaultAsyncCallback<List<ApplicationUserDTO>>() {
                    @Override
                    public void onSuccess(List<ApplicationUserDTO> result) {
                        new EntitySelectorListDialog<ApplicationUserDTO>(i18n.tr("Select Tenants To Send An Invitation To"), true, result,
                                new EntitySelectorListDialog.Formatter<ApplicationUserDTO>() {
                                    @Override
                                    public String format(ApplicationUserDTO enntity) {
                                        return enntity.getStringView();
                                    }
                                }) {

                            @Override
                            public boolean onClickOk() {
                                ((MasterApplicationViewerView.Presenter) presenter).inviteUsers(getSelectedItems());
                                return true;
                            }

                            @Override
                            public String defineWidth() {
                                return "350px";
                            }

                            @Override
                            public String defineHeight() {
                                return "100px";
                            }
                        }.show();
                    }
                });
            }
        });
        addToolbarItem(inviteAction.asWidget());

        checkAction = new Button(i18n.tr("Credit Check"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new EntitySelectorListDialog<TenantInfoDTO>(i18n.tr("Select Tenants To Check"), true, form.getValue().tenantInfo(),
                        new EntitySelectorListDialog.Formatter<TenantInfoDTO>() {
                            @Override
                            public String format(TenantInfoDTO enntity) {
                                return enntity.person().name().getStringView();
                            }
                        }) {

                    @Override
                    public boolean onClickOk() {
                        // TODO make the credit check happen
                        return true;
                    }

                    @Override
                    public String defineWidth() {
                        return "350px";
                    }

                    @Override
                    public String defineHeight() {
                        return "100px";
                    }
                }.show();
            }
        });
        addToolbarItem(checkAction.asWidget());

        approveAction = new Button(APPROVE, new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new ActionBox(APPROVE) {
                    @Override
                    public boolean onClickOk() {
                        ((MasterApplicationViewerView.Presenter) presenter).action(updateValue(Status.Approved));
                        return true;
                    }
                }.show();
            }
        });
        addToolbarItem(approveAction.asWidget());

        moreInfoAction = new Button(MORE_INFO, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new EntitySelectorListDialog<TenantInfoDTO>(i18n.tr("Select Tenants To Acqure Info"), true, form.getValue().tenantInfo(),
                        new EntitySelectorListDialog.Formatter<TenantInfoDTO>() {
                            @Override
                            public String format(TenantInfoDTO enntity) {
                                return enntity.person().name().getStringView();
                            }
                        }) {

                    @Override
                    public boolean onClickOk() {
                        // TODO make the credit check happen
                        return true;
                    }

                    @Override
                    public String defineWidth() {
                        return "350px";
                    }

                    @Override
                    public String defineHeight() {
                        return "100px";
                    }
                }.show();
            }
        });
        addToolbarItem(moreInfoAction.asWidget());

        declineAction = new Button(DECLINE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ActionBox(DECLINE) {
                    @Override
                    public boolean onClickOk() {
                        ((MasterApplicationViewerView.Presenter) presenter).action(updateValue(Status.Declined));
                        return true;
                    }
                }.show();
            }
        });
        addToolbarItem(declineAction.asWidget());

        cancelAction = new Button(CANCEL, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ActionBox(CANCEL) {
                    @Override
                    public boolean onClickOk() {
                        ((MasterApplicationViewerView.Presenter) presenter).action(updateValue(Status.Cancelled));
                        return true;
                    }
                }.show();
            }
        });
        addToolbarItem(cancelAction.asWidget());

        //set main form here:
        setForm(new MasterApplicationEditorForm(true));
    }

    @Override
    public void reset() {
        inviteAction.setVisible(false);
        checkAction.setVisible(false);
        approveAction.setVisible(false);
        moreInfoAction.setVisible(false);
        declineAction.setVisible(false);
        cancelAction.setVisible(false);
        super.reset();
    }

    @Override
    public void populate(MasterApplicationDTO value) {
        super.populate(value);

        // set buttons state:
        if (!value.lease().unit().isNull()) {
            Status status = value.status().getValue();
            inviteAction.setVisible(status != Status.Declined && status != Status.Cancelled);
            checkAction.setVisible(status != Status.Declined && status != Status.Cancelled);
            approveAction.setVisible(status != Status.Approved && status != Status.Declined && status != Status.Cancelled);
            moreInfoAction.setVisible(status != Status.Declined && status != Status.Cancelled);
            declineAction.setVisible(status != Status.Approved && status != Status.Declined && status != Status.Cancelled);
            cancelAction.setVisible(status != Status.Cancelled);
        }
    }

    @Override
    public IListerView<ApplicationDTO> getApplicationsView() {
        return applicationLister;
    }

    @Override
    public IListerView<TenantInLeaseDTO> getTenantsView() {
        return tenantLister;
    }

    private abstract class ActionBox extends OkCancelDialog {

        private final CTextArea reason = new CTextArea();

        public ActionBox(String title) {
            super(title);
            setBody(createBody());
            setSize("350px", "100px");
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            VerticalPanel content = new VerticalPanel();
            content.add(new HTML(i18n.tr("Please fill the reason") + ":"));
            content.add(reason);

            reason.setWidth("100%");
            content.setWidth("100%");
            return content.asWidget();
        }

        public MasterApplicationActionDTO updateValue(Status status) {
            MasterApplicationActionDTO action = EntityFactory.create(MasterApplicationActionDTO.class);
            action.setPrimaryKey(form.getValue().getPrimaryKey());
            action.decisionReason().setValue(reason.getValue());
            action.status().setValue(status);
            return action;
        }
    }
}