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
package com.propertyvista.crm.client.ui.crud.lease;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.validators.DateInPeriodValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.components.boxes.LeaseTermSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.ReasonBox;
import com.propertyvista.crm.client.ui.crud.billing.adjustments.LeaseAdjustmentLister;
import com.propertyvista.crm.client.ui.crud.billing.bill.BillLister;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentLister;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.lease.common.deposit.DepositLifecycleLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.misc.VistaTODO;

public class LeaseViewerViewImpl extends LeaseViewerViewImplBase<LeaseDTO> implements LeaseViewerView {

    private static final I18n i18n = I18n.get(LeaseViewerViewImpl.class);

    private final IListerView<DepositLifecycleDTO> depositLister;

    private final IListerView<BillDataDTO> billLister;

    private final IListerView<PaymentRecordDTO> paymentLister;

    private final IListerView<LeaseAdjustment> adjustmentLister;

    private final MenuItem viewApplication;

    private final MenuItem sendMailAction;

    private final MenuItem runBillAction;

    private final MenuItem noticeAction;

    private final MenuItem cancelNoticeAction;

    private final MenuItem evictAction;

    private final MenuItem cancelEvictAction;

    private final MenuItem terminateAction;

    private final MenuItem cancelTerminateAction;

    private final MenuItem activateAction;

    private final MenuItem moveOutAction;

    private final MenuItem closeAction;

    private final MenuItem cancelAction;

    private final Button renewButton;

    private final MenuItem offerAction;

    private final MenuItem viewOfferedTerms;

    public LeaseViewerViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);

        depositLister = new ListerInternalViewImplBase<DepositLifecycleDTO>(new DepositLifecycleLister());
        billLister = new ListerInternalViewImplBase<BillDataDTO>(new BillLister());
        paymentLister = new ListerInternalViewImplBase<PaymentRecordDTO>(new PaymentLister());
        adjustmentLister = new ListerInternalViewImplBase<LeaseAdjustment>(new LeaseAdjustmentLister());

        // set main form here:
        setForm(new LeaseForm());

        // Actions:

        viewApplication = new MenuItem(i18n.tr("View Application"), new Command() {
            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.LeaseApplication().formViewerPlace(getForm().getValue().getPrimaryKey()));
            }
        });
        if (VistaTODO.VISTA_2108_View_Lease_Application) {
            addAction(viewApplication);
        }

        sendMailAction = new MenuItem(i18n.tr("Send Mail..."), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseTermParticipant<?>>>() {
                    @Override
                    public void onSuccess(List<LeaseTermParticipant<?>> result) {
                        new SendMailBox(result) {
                            @Override
                            public boolean onClickOk() {
                                ((LeaseViewerView.Presenter) getPresenter()).sendMail(getSelectedItems(), getEmailType());
                                return true;
                            }
                        }.show();
                    }
                });
            }
        });
        addAction(sendMailAction);

        runBillAction = new MenuItem(i18n.tr("Run Bill"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).startBilling();
            }
        });
        addAction(runBillAction);

        addActionSeparator();

        noticeAction = new MenuItem(i18n.tr("Notice..."), new Command() {
            @Override
            public void execute() {
                new TermLeaseBox(CompletionType.Notice) {
                    @Override
                    public boolean onClickOk() {
                        if (!isValid()) {
                            return false;
                        }
                        ((LeaseViewerView.Presenter) getPresenter()).createCompletionEvent(CompletionType.Notice, getValue().moveOutNotice().getValue(),
                                getValue().expectedMoveOut().getValue(), null);
                        return true;
                    }
                }.show();
            }
        });
        addAction(noticeAction);

        cancelNoticeAction = new MenuItem(i18n.tr("Cancel Notice"), new Command() {
            @Override
            public void execute() {
                cancelCmpletion(CompletionType.Notice);
            }
        });
        addAction(cancelNoticeAction);

        evictAction = new MenuItem(i18n.tr("Evict..."), new Command() {
            @Override
            public void execute() {
                new TermLeaseBox(CompletionType.Eviction) {
                    @Override
                    public boolean onClickOk() {
                        if (!isValid()) {
                            return false;
                        }
                        ((LeaseViewerView.Presenter) getPresenter()).createCompletionEvent(CompletionType.Eviction, getValue().moveOutNotice().getValue(),
                                getValue().expectedMoveOut().getValue(), null);
                        return true;
                    }
                }.show();
            }
        });
        addAction(evictAction);

        cancelEvictAction = new MenuItem(i18n.tr("Cancel Eviction"), new Command() {
            @Override
            public void execute() {
                cancelCmpletion(CompletionType.Eviction);
            }
        });
        addAction(cancelEvictAction);

        terminateAction = new MenuItem(i18n.tr("Terminate..."), new Command() {
            @Override
            public void execute() {
                new TermLeaseBox(CompletionType.Termination) {
                    @Override
                    public boolean onClickOk() {
                        if (!isValid()) {
                            return false;
                        }
                        ((LeaseViewerView.Presenter) getPresenter()).createCompletionEvent(CompletionType.Termination, getValue().moveOutNotice().getValue(),
                                getValue().expectedMoveOut().getValue(), getValue().terminationLeaseTo().getValue());
                        return true;
                    }
                }.show();
            }
        });
        addAction(terminateAction);

        cancelTerminateAction = new MenuItem(i18n.tr("Cancel Termination"), new Command() {
            @Override
            public void execute() {
                cancelCmpletion(CompletionType.Termination);
            }
        });
        addAction(cancelTerminateAction);

        activateAction = new MenuItem(i18n.tr("Activate"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).activate();
            }
        });
        addAction(activateAction);

        moveOutAction = new MenuItem(i18n.tr("Move Out"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Move Out"), i18n.tr("This will make this unit available to other leases (Tenant has brought the key).")
                        + "<br/><br/>" + i18n.tr("Are you sure you would like to continue?"), new Command() {
                    @Override
                    public void execute() {
                        ((LeaseViewerView.Presenter) getPresenter()).moveOut();
                    }
                });
            }
        });
        addAction(moveOutAction);

        closeAction = new MenuItem(i18n.tr("Close"), new Command() {
            @Override
            public void execute() {
                new ReasonBox(i18n.tr("Close Lease")) {
                    @Override
                    public boolean onClickOk() {
                        if (CommonsStringUtils.isEmpty(getReason())) {
                            MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the reason"));
                            return false;
                        }
                        ((LeaseViewerView.Presenter) getPresenter()).closeLease(getReason());
                        return true;
                    }
                }.show();
            }
        });
        addAction(closeAction);

        cancelAction = new MenuItem(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                new ReasonBox(i18n.tr("Cancel Lease")) {
                    @Override
                    public boolean onClickOk() {
                        if (CommonsStringUtils.isEmpty(getReason())) {
                            MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the reason"));
                            return false;
                        }
                        ((LeaseViewerView.Presenter) getPresenter()).cancelLease(getReason());
                        return true;
                    }
                }.show();
            }
        });
        addAction(cancelAction);

        // Renewing stuff : ---------------------------------------------------------------------------------------------------

        renewButton = new Button(i18n.tr("Renew"));
        Button.ButtonMenuBar renewMenu = renewButton.createMenu();
        renewButton.setMenu(renewMenu);
        if (VistaTODO.VISTA_1789_Renew_Lease) {
            addHeaderToolbarItem(renewButton.asWidget());
        }

        offerAction = new MenuItem(i18n.tr("Create Offer"), new Command() {
            @Override
            public void execute() {
                new SelectEnumDialog<LeaseTerm.Type>(i18n.tr("Select Term Type"), LeaseTerm.Type.renew()) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) getPresenter()).createOffer(getSelectedType());
                        return true;
                    }
                }.show();
            }
        });
        renewMenu.addItem(offerAction);

        viewOfferedTerms = new MenuItem(i18n.tr("View Offers..."), new Command() {
            @Override
            public void execute() {
                new LeaseTermSelectorDialog() {
                    {
                        setParentFiltering(getForm().getValue().getPrimaryKey());
                        addFilter(PropertyCriterion.eq(proto().status(), LeaseTerm.Status.Offer));
                    }

                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            ((LeaseViewerViewBase.Presenter) getPresenter()).viewTerm(getSelectedItems().get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                }.show();
            }
        });
        renewMenu.addItem(viewOfferedTerms);
    }

    @Override
    public void reset() {
        if (VistaTODO.VISTA_2108_View_Lease_Application) {
            setActionVisible(viewApplication, false);
        }
        setActionVisible(sendMailAction, false);
        setActionVisible(runBillAction, false);
        setActionVisible(noticeAction, false);
        setActionVisible(cancelNoticeAction, false);
        setActionVisible(evictAction, false);
        setActionVisible(cancelEvictAction, false);
        setActionVisible(terminateAction, false);
        setActionVisible(cancelTerminateAction, false);
        setActionVisible(activateAction, false);
        setActionVisible(moveOutAction, false);
        setActionVisible(closeAction, false);
        setActionVisible(cancelAction, false);

        super.reset();
    }

    @Override
    public void populate(LeaseDTO value) {
        super.populate(value);

        Lease.Status status = value.status().getValue();

        // set buttons state:
        CompletionType completion = value.completion().getValue();

        if (VistaTODO.VISTA_2108_View_Lease_Application) {
            setActionVisible(viewApplication, !value.leaseApplication().isNull());
        }
        setActionVisible(sendMailAction, status.isCurrent());
        setActionVisible(runBillAction, status.isCurrent());

        setActionVisible(noticeAction, status == Status.Active && completion == null);
        setActionVisible(cancelNoticeAction, completion == CompletionType.Notice && value.actualMoveOut().isNull() && !status.isFormer());

        setActionVisible(evictAction, status == Status.Active && completion == null);
        setActionVisible(cancelEvictAction, completion == CompletionType.Eviction && value.actualMoveOut().isNull() && !status.isFormer());

        setActionVisible(terminateAction, status == Status.Active && completion == null);
        setActionVisible(cancelTerminateAction, completion == CompletionType.Termination && value.actualMoveOut().isNull() && !status.isFormer());

        setActionVisible(moveOutAction, status == Status.Active && completion != null && value.actualMoveOut().isNull() && !status.isFormer());
        setActionHighlighted(moveOutAction, status == Status.Active && completion != null && value.actualMoveOut().isNull() && !status.isFormer());

        setActionVisible(activateAction, status == Status.ExistingLease);
        setActionHighlighted(activateAction, activateAction.isVisible());

        setActionVisible(closeAction, status == Status.Completed);
        setActionVisible(cancelAction, status.isDraft() || status == Status.Approved);

        if (VistaTODO.VISTA_1789_Renew_Lease) {
            renewButton.setVisible(status == Status.Active && completion == null && value.nextTerm().isNull());
        }
    }

    @Override
    public IListerView<DepositLifecycleDTO> getDepositListerView() {
        return depositLister;
    }

    @Override
    public IListerView<BillDataDTO> getBillListerView() {
        return billLister;
    }

    @Override
    public IListerView<PaymentRecordDTO> getPaymentListerView() {
        return paymentLister;
    }

    @Override
    public IListerView<LeaseAdjustment> getLeaseAdjustmentListerView() {
        return adjustmentLister;
    }

    @Override
    public void reportSendMailActionResult(String message) {
        MessageDialog.info(message);
    }

    @Override
    public void reportCancelNoticeFailed(UserRuntimeException caught) {
        MessageDialog.error(i18n.tr("Cancel Notice Failed"), caught.getMessage());
    }

    private abstract class TermLeaseBox extends OkCancelDialog {

        private final CompletionType action;

        private final boolean showTermination;

        private CEntityDecoratableForm<LeaseDTO> content;

        public TermLeaseBox(CompletionType action) {
            super(i18n.tr("Please fill"));

            this.action = action;
            this.showTermination = (action == CompletionType.Termination);

            setBody(createBody());
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            content = new CEntityDecoratableForm<LeaseDTO>(LeaseDTO.class) {
                @Override
                public IsWidget createContent() {
                    FormFlexPanel main = new FormFlexPanel();

                    main.setWidget(0, 0, new DecoratorBuilder(inject(proto().moveOutNotice()), 9).customLabel(action.toString() + i18n.tr(" Submission Date"))
                            .build());
                    main.setWidget(1, 0, new DecoratorBuilder(inject(proto().expectedMoveOut()), 9).build());

                    if (showTermination) {
                        main.setWidget(2, 0, new DecoratorBuilder(inject(proto().terminationLeaseTo()), 9).customLabel(i18n.tr("Lease Termination Date"))
                                .build());
                    }

                    // just for validation purpose:
                    inject(proto().currentTerm().termFrom());
                    inject(proto().currentTerm().termTo());

                    return main;
                }

                @Override
                protected void onValueSet(boolean populate) {
                    super.onValueSet(populate);

                    if (getValue().moveOutNotice().isNull()) {
                        get(proto().moveOutNotice()).setValue(new LogicalDate(ClientContext.getServerDate()));
                    }
                    if (getValue().expectedMoveOut().isNull()) {
                        get(proto().expectedMoveOut()).setValue(new LogicalDate(ClientContext.getServerDate()));
                    }
                    if (showTermination) {
                        if (getValue().terminationLeaseTo().isNull()) {
                            get(proto().terminationLeaseTo()).setValue(getValue().currentTerm().termTo().getValue());
                        }
                    }
                }

                @Override
                public void addValidations() {
                    super.addValidations();

                    new DateInPeriodValidation(get(proto().currentTerm().termFrom()), get(proto().moveOutNotice()), get(proto().currentTerm().termTo()),
                            i18n.tr("The Date Should Be Within The Lease Period"));

                    new DateInPeriodValidation(get(proto().currentTerm().termFrom()), get(proto().expectedMoveOut()), get(proto().currentTerm().termTo()),
                            i18n.tr("The Date Should Be Within The Lease Period"));

                    new StartEndDateValidation(get(proto().moveOutNotice()), get(proto().expectedMoveOut()),
                            i18n.tr("The Notice Date Must Be Earlier Than The Expected Move Out date"));

                    get(proto().moveOutNotice()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expectedMoveOut())));
                    get(proto().expectedMoveOut()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().moveOutNotice())));

                    if (showTermination) {
                        new DateInPeriodValidation(get(proto().currentTerm().termFrom()), get(proto().terminationLeaseTo()),
                                get(proto().currentTerm().termTo()), i18n.tr("The Date Should Be Within The Lease Period"));

                        new StartEndDateValidation(get(proto().expectedMoveOut()), get(proto().terminationLeaseTo()),
                                i18n.tr("The Terminationi Date Can't Be Earlier Than The Expected Move Out date"));

                        get(proto().terminationLeaseTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expectedMoveOut())));
                    }
                }
            };

            content.initContent();
            content.populate((LeaseDTO) getForm().getValue().duplicate());
            return content.asWidget();
        }

        public boolean isValid() {
            return content.isValid();
        }

        public LeaseDTO getValue() {
            return content.getValue();
        }
    }

    private abstract class SendMailBox extends EntitySelectorListDialog<LeaseTermParticipant<?>> {

        private CComboBox<EmailTemplateType> emailType;

        public SendMailBox(List<LeaseTermParticipant<?>> applicationUsers) {
            super(i18n.tr("Send Mail"), true, applicationUsers);
            getOkButton().setText(i18n.tr("Send"));
        }

        @Override
        protected Widget initBody(boolean isMultiselectAllowed, List<LeaseTermParticipant<?>> data) {
            VerticalPanel body = new VerticalPanel();
            body.add(new HTML(i18n.tr("Select Tenants:")));
            body.add(super.initBody(isMultiselectAllowed, data));
            body.add(new HTML(i18n.tr("Email Type:")));
            body.add(initEmailTypes());
            body.setWidth("100%");
            body.setSpacing(3);
            return body;
        }

        private Widget initEmailTypes() {
            emailType = new CComboBox<EmailTemplateType>();
            emailType.setOptions(Arrays.asList(EmailTemplateType.TenantInvitation));
            emailType.setValue(EmailTemplateType.TenantInvitation, false);
            emailType.setMandatory(true);
            return emailType.asWidget();
        }

        protected EmailTemplateType getEmailType() {
            return emailType.getValue();
        }
    }

    public void cancelCmpletion(final Lease.CompletionType completionType) {
        ((LeaseViewerView.Presenter) getPresenter()).isCancelCompletionEventAvailable(new DefaultAsyncCallback<CancelMoveOutConstraintsDTO>() {
            @Override
            public void onSuccess(final CancelMoveOutConstraintsDTO result) {
                if (result.reason().isNull()) {
                    new ReasonBox(i18n.tr("Cancel Notice")) {
                        @Override
                        public boolean onClickOk() {
                            if (CommonsStringUtils.isEmpty(getReason())) {
                                MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the reason"));
                                return false;
                            }
                            ((LeaseViewerView.Presenter) getPresenter()).cancelCompletionEvent(getReason());
                            return true;
                        }
                    }.show();
                } else {
                    String caption = i18n.tr("Error");
                    String message = i18n.tr("Can't cancel the ") + completionType.toString();
                    switch (result.reason().getValue()) {
                    case LeasedOrReserved:
                        message += i18n.tr(" the unit is reserved/leased by another Application/Lease already!") + "<br/><br/>"
                                + i18n.tr("Do you want to view it?");
                        MessageDialog.confirm(caption, message, new Command() {
                            @Override
                            public void execute() {
                                CrudAppPlace place;
                                switch (result.leaseStub().status().getValue()) {
                                case Application:
                                    place = new CrmSiteMap.Tenants.LeaseApplication();
                                    break;
                                case Active:
                                case Approved:
                                case Cancelled:
                                case Closed:
                                case Completed:
                                case ExistingLease:
                                    place = new CrmSiteMap.Tenants.Lease();
                                    break;
                                default:
                                    throw new UserRuntimeException("Unsupported lease status.");
                                }

                                AppSite.getPlaceController().goTo(place.formViewerPlace(result.leaseStub().getPrimaryKey()));
                            }
                        });
                        break;
                    case RenovatedOrOffMarket:
                        message += i18n.tr(" - the unit is Renovated/Off-Market!");
                        MessageDialog.error(caption, message);
                        break;
                    case MoveOutNotExpected:
                        message += i18n.tr(" - move out is not expected!");
                        MessageDialog.error(caption, message);
                        break;
                    default:
                        MessageDialog.error(caption, i18n.tr("Unspecified reason!?!"));
                        break;
                    }
                }
            }
        });
    }
}