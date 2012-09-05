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
package com.propertyvista.crm.client.ui.crud.lease.application;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO.Action;
import com.propertyvista.domain.tenant.lease.LeaseApplication.Status;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseApplicationViewerViewImpl extends LeaseViewerViewImplBase<LeaseApplicationDTO> implements LeaseApplicationViewerView {

    private static final I18n i18n = I18n.get(LeaseApplicationViewerViewImpl.class);

    private final MenuItem onlineApplication;

    private final MenuItem inviteAction;

    private final MenuItem checkAction;

    private final MenuItem approveAction;

    private final MenuItem moreInfoAction;

    private final MenuItem declineAction;

    private final MenuItem cancelAction;

    private static final String INVITE = i18n.tr("Invite");

    private static final String APPROVE = i18n.tr("Approve");

    private static final String MORE_INFO = i18n.tr("More Info");

    private static final String DECLINE = i18n.tr("Decline");

    private static final String CANCEL = i18n.tr("Cancel");

    public LeaseApplicationViewerViewImpl() {
        super(CrmSiteMap.Tenants.LeaseApplication.class);

        //set main form here:
        setForm(new LeaseApplicationForm());

        // Actions:

        onlineApplication = new MenuItem(i18n.tr("Start Online Application"), new Command() {
            @Override
            public void execute() {
                ((LeaseApplicationViewerView.Presenter) getPresenter()).startOnlineApplication();
            }
        });
        if (VistaFeatures.instance().onlineApplication()) {
            addAction(onlineApplication);
        }

        inviteAction = new MenuItem(INVITE, new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseParticipant>>() {
                    @Override
                    public void onSuccess(List<LeaseParticipant> result) {
                        new EntitySelectorListDialog<LeaseParticipant>(i18n.tr("Select Tenants/Guarantors To Send An Invitation To"), true, result) {

                            @Override
                            public boolean onClickOk() {
                                ((LeaseApplicationViewerView.Presenter) getPresenter()).inviteUsers(getSelectedItems());
                                return true;
                            }
                        }.show();
                    }
                });
            }
        });
        if (VistaFeatures.instance().onlineApplication()) {
            addAction(inviteAction);
        }

        checkAction = new MenuItem(i18n.tr("Credit Check"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseParticipant>>() {
                    @Override
                    public void onSuccess(List<LeaseParticipant> result) {
                        new EntitySelectorListDialog<LeaseParticipant>(i18n.tr("Select Tenants/Guarantors To Check"), true, result) {

                            @Override
                            public boolean onClickOk() {
                                // TODO make the credit check happen
                                return true;
                            }
                        }.show();
                    }
                });
            }
        });
// TODO : credit check (Equifax) isn't implemented yet (see LeaseApplicationForm)!          
//        addAction(checkAction.asWidget());

        // TODO Move Lease
        {
            approveAction = new MenuItem(APPROVE, new Command() {

                @Override
                public void execute() {
                    new ActionBox(APPROVE) {
                        @Override
                        public boolean onClickOk() {
                            ((LeaseApplicationViewerView.Presenter) getPresenter()).applicationAction(updateValue(Action.Approve));
                            return true;
                        }
                    }.show();
                }
            });
            addAction(approveAction);

            moreInfoAction = new MenuItem(MORE_INFO, new Command() {
                @Override
                public void execute() {
                    ((LeaseViewerViewBase.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseParticipant>>() {
                        @Override
                        public void onSuccess(List<LeaseParticipant> result) {
                            new EntitySelectorListDialog<LeaseParticipant>(i18n.tr("Select Tenants/Guarantors To Acquire Info"), true, result) {

                                @Override
                                public boolean onClickOk() {
                                    // TODO make the credit check happen
                                    return true;
                                }
                            }.show();
                        }
                    });
                }
            });
            addAction(moreInfoAction);

            declineAction = new MenuItem(DECLINE, new Command() {
                @Override
                public void execute() {
                    new ActionBox(DECLINE) {
                        @Override
                        public boolean onClickOk() {
                            ((LeaseApplicationViewerView.Presenter) getPresenter()).applicationAction(updateValue(Action.Decline));
                            return true;
                        }
                    }.show();
                }
            });
            addAction(declineAction);
        }

        cancelAction = new MenuItem(CANCEL, new Command() {
            @Override
            public void execute() {
                new ActionBox(CANCEL) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseApplicationViewerView.Presenter) getPresenter()).applicationAction(updateValue(Action.Cancel));
                        return true;
                    }
                }.show();
            }
        });
        addAction(cancelAction);
    }

    @Override
    public void reset() {
        setActionVisible(onlineApplication, false);
        setActionVisible(inviteAction, false);
        setActionVisible(checkAction, false);
        setActionVisible(approveAction, false);
        setActionVisible(moreInfoAction, false);
        setActionVisible(declineAction, false);
        setActionVisible(cancelAction, false);
        super.reset();
    }

    @Override
    public void populate(LeaseApplicationDTO value) {
        super.populate(value);

        Status status = value.leaseApplication().status().getValue();

        // set buttons state:
        if (!value.unit().isNull()) {
            setActionVisible(onlineApplication, status == Status.Created);
            setActionVisible(inviteAction, status == Status.OnlineApplication);
            setActionVisible(checkAction, status.isDraft());
            setActionVisible(approveAction, status.isDraft());
            setActionVisible(moreInfoAction, status.isDraft() && status != Status.Created);
            setActionVisible(declineAction, status.isDraft());
            setActionVisible(cancelAction, status != Status.Cancelled);
        }
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

        public LeaseApplicationActionDTO updateValue(Action status) {
            LeaseApplicationActionDTO action = EntityFactory.create(LeaseApplicationActionDTO.class);
            action.leaseId().set(getForm().getValue().createIdentityStub());
            action.decisionReason().setValue(reason.getValue());
            action.action().setValue(status);
            return action;
        }
    }

    @Override
    public void reportStartOnlineApplicationSuccess() {
        MessageDialog.info(i18n.tr("Started Online Application"));
    }

    @Override
    public void reportInviteUsersActionResult(String message) {
        MessageDialog.info(message);
    }
}