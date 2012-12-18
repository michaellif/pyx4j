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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.YesNoOption;

import com.propertyvista.crm.client.ui.components.boxes.ReasonBox;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO.Action;
import com.propertyvista.domain.tenant.lease.LeaseApplication.Status;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseApplicationViewerViewImpl extends LeaseViewerViewImplBase<LeaseApplicationDTO> implements LeaseApplicationViewerView {

    private static final I18n i18n = I18n.get(LeaseApplicationViewerViewImpl.class);

    private final MenuItem viewLease;

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
        setForm(new LeaseApplicationForm(this));

        // Actions:

        viewLease = new MenuItem(i18n.tr("View Lease"), new Command() {
            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Lease().formViewerPlace(getForm().getValue().getPrimaryKey()));
            }
        });
        if (VistaTODO.VISTA_2108_View_Lease_Application) {
            addAction(viewLease);
        }

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
                ((LeaseViewerViewBase.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseTermParticipant<?>>>() {
                    @Override
                    public void onSuccess(List<LeaseTermParticipant<?>> result) {
                        new EntitySelectorListDialog<LeaseTermParticipant<?>>(i18n.tr("Select Tenants/Guarantors To Send An Invitation To"), true, result) {
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
                ((LeaseApplicationViewerView.Presenter) getPresenter()).isCreditCheckActivated(new DefaultAsyncCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            ((LeaseViewerViewBase.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseTermParticipant<?>>>() {
                                @Override
                                public void onSuccess(List<LeaseTermParticipant<?>> result) {
                                    new EntitySelectorListDialog<LeaseTermParticipant<?>>(i18n.tr("Select Tenants/Guarantors To Check"), true, result) {
                                        @Override
                                        public boolean onClickOk() {
                                            ((LeaseApplicationViewerView.Presenter) getPresenter()).creditCheck(getSelectedItems());
                                            return true;
                                        }
                                    }.show();
                                }
                            });
                        } else {
                            new CreditCheckSubscribeDialog().show();
                        }
                    }
                });
            }
        });
        if (!VistaTODO.Equifax_Short_VISTA_478) {
            addAction(checkAction);
        }

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
                    ((LeaseViewerViewBase.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseTermParticipant<?>>>() {
                        @Override
                        public void onSuccess(List<LeaseTermParticipant<?>> result) {
                            new EntitySelectorListDialog<LeaseTermParticipant<?>>(i18n.tr("Select Tenants/Guarantors To Acquire Info"), true, result) {

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
                        if (CommonsStringUtils.isEmpty(getReason())) {
                            MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the reason"));
                            return false;
                        }
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
        if (VistaTODO.VISTA_2108_View_Lease_Application) {
            setActionVisible(viewLease, false);
        }
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
        if (VistaTODO.VISTA_2108_View_Lease_Application) {
            setActionVisible(viewLease, status.isCurrent());
        }
        setActionVisible(onlineApplication, status == Status.Created);
        setActionVisible(inviteAction, status == Status.OnlineApplication);
        setActionVisible(checkAction, status.isDraft());
        setActionVisible(approveAction, status.isDraft());
        setActionVisible(moreInfoAction, status.isDraft() && status != Status.Created);
        setActionVisible(declineAction, status.isDraft());
        setActionVisible(cancelAction, status != Status.Cancelled);
    }

    private abstract class ActionBox extends ReasonBox {

        public ActionBox(String title) {
            super(title);
        }

        public LeaseApplicationActionDTO updateValue(Action status) {
            LeaseApplicationActionDTO action = EntityFactory.create(LeaseApplicationActionDTO.class);
            action.leaseId().set(getForm().getValue().createIdentityStub());
            action.decisionReason().setValue(getReason());
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

    @Override
    public void reportCreditCheckActionResult(String message) {
        MessageDialog.info(message);
    }

    @Override
    public void reportApplicationApprovalSuccess() {
        MessageDialog.info(i18n.tr("Application has been approved successfully"));
    }

    @Override
    public void reportApplicationApprovalFailure(UserRuntimeException caught) {
        MessageDialog.info(caught.getMessage());

    }

    private class CreditCheckSubscribeDialog extends Dialog implements YesNoOption {

        public CreditCheckSubscribeDialog() {
            super(i18n.tr("Credit Check"));
            setBody(new HTML(i18n.tr("No credit check service for this account has been set up.") + "<br/>" + i18n.tr("Do you want to apply now?")));
            setDialogOptions(this);
        }

        @Override
        public boolean onClickYes() {
            // TODO : go to credit check application...
            return true;
        }

        @Override
        public boolean onClickNo() {
            return true;
        }
    }
}