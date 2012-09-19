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
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.validators.DateInPeriodValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.components.boxes.LeaseTermSelectorDialog;
import com.propertyvista.crm.client.ui.crud.billing.adjustments.LeaseAdjustmentLister;
import com.propertyvista.crm.client.ui.crud.billing.bill.BillLister;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentLister;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public class LeaseViewerViewImpl extends LeaseViewerViewImplBase<LeaseDTO> implements LeaseViewerView {

    private static final I18n i18n = I18n.get(LeaseViewerViewImpl.class);

    private final IListerView<BillDataDTO> billLister;

    private final IListerView<PaymentRecordDTO> paymentLister;

    private final IListerView<LeaseAdjustment> adjustmentLister;

    private final MenuItem sendMailAction;

    private final MenuItem runBillAction;

    private final MenuItem noticeAction;

    private final MenuItem cancelNoticeAction;

    private final MenuItem evictAction;

    private final MenuItem cancelEvictAction;

    private final MenuItem activateAction;

    private final MenuItem cancelAction;

    private final Button renewButton;

    private final MenuItem offerAction;

    private final MenuItem viewOfferedTerms;

    public LeaseViewerViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);

        billLister = new ListerInternalViewImplBase<BillDataDTO>(new BillLister());

        paymentLister = new ListerInternalViewImplBase<PaymentRecordDTO>(new PaymentLister());

        adjustmentLister = new ListerInternalViewImplBase<LeaseAdjustment>(new LeaseAdjustmentLister());

        // set main form here:
        setForm(new LeaseForm());

        // Actions: -----------------------------------------------------------------------------------------------------------

        sendMailAction = new MenuItem(i18n.tr("Send Mail..."), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseParticipant>>() {
                    @Override
                    public void onSuccess(List<LeaseParticipant> result) {
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

        noticeAction = new MenuItem(i18n.tr("Notice..."), new Command() {
            @Override
            public void execute() {
                new TermLeaseBox(CompletionType.Notice) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) getPresenter()).notice(getValue().moveOutNotice().getValue(), getValue().expectedMoveOut().getValue());
                        return true;
                    }
                }.show();
            }
        });
        addAction(noticeAction);

        cancelNoticeAction = new MenuItem(i18n.tr("Cancel Notice"), new Command() {
            @Override
            public void execute() {
                new ReasonBox(i18n.tr("Do you really want to cancel the Notice?")) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) getPresenter()).cancelNotice(getReason());
                        return true;
                    }
                }.show();
            }
        });
        addAction(cancelNoticeAction);

        evictAction = new MenuItem(i18n.tr("Evict..."), new Command() {
            @Override
            public void execute() {
                new TermLeaseBox(CompletionType.Eviction) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) getPresenter()).evict(getValue().moveOutNotice().getValue(), getValue().expectedMoveOut().getValue());
                        return true;
                    }
                }.show();
            }
        });
        addAction(evictAction);

        cancelEvictAction = new MenuItem(i18n.tr("Cancel Evict"), new Command() {
            @Override
            public void execute() {
                new ReasonBox(i18n.tr("Do you really want to cancel Evict?")) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) getPresenter()).cancelEvict(getReason());
                        return true;
                    }
                }.show();
            }
        });
        addAction(cancelEvictAction);

        activateAction = new MenuItem(i18n.tr("Activate"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).activate();
            }
        });
        addAction(activateAction);

        cancelAction = new MenuItem(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                new ReasonBox(i18n.tr("Do you really want to cancel the Lease?")) {
                    @Override
                    public boolean onClickOk() {
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
        addHeaderToolbarItem(renewButton.asWidget());

        offerAction = new MenuItem(i18n.tr("Create Offer"), new Command() {
            @Override
            public void execute() {
                new SelectEnumDialog<LeaseTerm.Type>(i18n.tr("Select Term Type"), EnumSet.allOf(LeaseTerm.Type.class)) {
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
        setActionVisible(sendMailAction, false);
        setActionVisible(runBillAction, false);
        setActionVisible(noticeAction, false);
        setActionVisible(cancelNoticeAction, false);
        setActionVisible(evictAction, false);
        setActionVisible(cancelEvictAction, false);
        setActionVisible(activateAction, false);
        setActionVisible(cancelAction, false);
        super.reset();
    }

    @Override
    public void populate(LeaseDTO value) {
        super.populate(value);

        Status status = value.status().getValue();

        // set buttons state:
        if (!value.unit().isNull()) {
            CompletionType completion = value.completion().getValue();

            setActionVisible(sendMailAction, true);
            setActionVisible(runBillAction, status.isActive());
            setActionVisible(noticeAction, status == Status.Active && completion == null);
            setActionVisible(cancelNoticeAction, completion == CompletionType.Notice && status != Status.Closed);
            setActionVisible(evictAction, status == Status.Active && completion == null);
            setActionVisible(cancelEvictAction, completion == CompletionType.Eviction && status != Status.Closed);
            setActionVisible(activateAction, status == Status.ExistingLease);
            setActionVisible(cancelAction, !status.isFormer());

            renewButton.setVisible(status == Status.Active && completion == null && value.futureTerm().isNull());
        }
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

    private abstract class TermLeaseBox extends OkCancelDialog {

        private final CompletionType action;

        private CEntityDecoratableForm<LeaseDTO> content;

        public TermLeaseBox(CompletionType action) {
            super(i18n.tr("Please select"));
            this.action = action;
            setBody(createBody());
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            content = new CEntityDecoratableForm<LeaseDTO>(LeaseDTO.class) {
                @Override
                public IsWidget createContent() {
                    FormFlexPanel main = new FormFlexPanel();
                    switch (action) {
                    case Notice:
                        main.setWidget(0, 0, new DecoratorBuilder(inject(proto().moveOutNotice()), 9).customLabel(i18n.tr("Notice Date")).build());
                        break;
                    case Eviction:
                        main.setWidget(0, 0, new DecoratorBuilder(inject(proto().moveOutNotice()), 9).customLabel(i18n.tr("Evict Date")).build());
                        break;
                    case LegalVacate:
                        break;
                    case Skip:
                        break;
                    default:
                        break;
                    }
                    main.setWidget(1, 0, new DecoratorBuilder(inject(proto().expectedMoveOut()), 9).build());

                    // just for validation purpose:
                    inject(proto().currentTerm().termFrom());
                    inject(proto().currentTerm().termTo());

                    return main;
                }

                @Override
                protected void onValueSet(boolean populate) {
                    super.onValueSet(populate);

                    if (getValue().moveOutNotice().isNull()) {
                        get(proto().moveOutNotice()).setValue(new LogicalDate());
                    }

                    if (getValue().expectedMoveOut().isNull()) {
                        get(proto().expectedMoveOut()).setValue(new LogicalDate());
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
                }
            };

            content.initContent();
            content.populate(getForm().getValue());
            return content.asWidget();
        }

        public LeaseDTO getValue() {
            return content.getValue();
        }
    }

    private abstract class SendMailBox extends EntitySelectorListDialog<LeaseParticipant> {

        private CComboBox<EmailTemplateType> emailType;

        public SendMailBox(List<LeaseParticipant> applicationUsers) {
            super(i18n.tr("Send Mail"), true, applicationUsers);
            getOkButton().setText(i18n.tr("Send"));
        }

        @Override
        protected Widget initBody(boolean isMultiselectAllowed, List<LeaseParticipant> data) {
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

    @Override
    public void reportSendMailActionResult(String message) {
        MessageDialog.info(message);
    }

    private abstract class ReasonBox extends OkCancelDialog {

        private final CTextArea reason = new CTextArea();

        public ReasonBox(String title) {
            super(title);
            setBody(createBody());
            setSize("350px", "100px");
        }

        protected Widget createBody() {
            getOkButton().setEnabled(false);

            VerticalPanel content = new VerticalPanel();
            content.add(new HTML(i18n.tr("Please fill the reason") + ":"));
            content.add(reason);
            reason.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    getOkButton().setEnabled(!event.getValue().isEmpty());
                }
            });

            reason.setWidth("100%");
            content.setWidth("100%");
            return content.asWidget();
        }

        public String getReason() {
            return reason.getValue();
        }
    }
}