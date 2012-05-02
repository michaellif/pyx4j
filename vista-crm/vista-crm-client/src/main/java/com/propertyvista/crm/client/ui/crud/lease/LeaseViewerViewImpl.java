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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.validators.DateInPeriodValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.billing.adjustments.LeaseAdjustmentLister;
import com.propertyvista.crm.client.ui.crud.billing.bill.BillLister;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.version.LeaseVersionService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.ApplicationUserDTO;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public class LeaseViewerViewImpl extends CrmViewerViewImplBase<LeaseDTO> implements LeaseViewerView {

    private static final I18n i18n = I18n.get(LeaseViewerViewImpl.class);

    private final IListerView<BillDTO> billLister;

    private final IListerView<PaymentRecordDTO> paymentLister;

    private final IListerView<LeaseAdjustment> adjustmentLister;

    private final Button sendMail;

    private final Button runBill;

    private final Button notice;

    private final Button cancelNotice;

    private final Button evict;

    private final Button cancelEvict;

    private final Button activate;

    public LeaseViewerViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);
        enableVersioning(Lease.LeaseV.class, GWT.<LeaseVersionService> create(LeaseVersionService.class));

        billLister = new ListerInternalViewImplBase<BillDTO>(new BillLister());

        paymentLister = new ListerInternalViewImplBase<PaymentRecordDTO>(new PaymentLister());

        adjustmentLister = new ListerInternalViewImplBase<LeaseAdjustment>(new LeaseAdjustmentLister());

        //set main form here:
        setForm(new LeaseEditorForm(true));

        // Add actions:
        sendMail = new Button(i18n.tr("Send Mail..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).retrieveUsers(new DefaultAsyncCallback<List<ApplicationUserDTO>>() {
                    @Override
                    public void onSuccess(List<ApplicationUserDTO> result) {
                        new SendMailBox(result) {
                            @Override
                            public boolean onClickOk() {
                                ((LeaseViewerView.Presenter) presenter).sendMail(getSelectedItems(), getEmailType());
                                return true;
                            }
                        }.show();
                    }
                });
            }
        });
        addHeaderToolbarTwoItem(sendMail.asWidget());

        runBill = new Button(i18n.tr("Run Bill"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).startBilling();
            }
        });
        addHeaderToolbarTwoItem(runBill.asWidget());

        notice = new Button(i18n.tr("Notice..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new TermLeaseBox(CompletionType.Notice) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) presenter).notice(getValue().version().moveOutNotice().getValue(), getValue().version().expectedMoveOut()
                                .getValue());
                        return true;
                    }
                }.show();
            }
        });
        addHeaderToolbarTwoItem(notice.asWidget());

        cancelNotice = new Button(i18n.tr("Cancel Notice"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).cancelNotice();
            }
        });
        addHeaderToolbarTwoItem(cancelNotice.asWidget());

        evict = new Button(i18n.tr("Evict..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new TermLeaseBox(CompletionType.Eviction) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) presenter).evict(getValue().version().moveOutNotice().getValue(), getValue().version().expectedMoveOut()
                                .getValue());
                        return true;
                    }
                }.show();
            }
        });
        addHeaderToolbarTwoItem(evict.asWidget());

        cancelEvict = new Button(i18n.tr("Cancel Evict"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).cancelEvict();
            }
        });
        addHeaderToolbarTwoItem(cancelEvict.asWidget());

        activate = new Button(i18n.tr("Activate"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).activate();
            }
        });
        addHeaderToolbarTwoItem(activate.asWidget());
    }

    @Override
    public void reset() {
        sendMail.setVisible(false);
        runBill.setVisible(false);
        notice.setVisible(false);
        cancelNotice.setVisible(false);
        evict.setVisible(false);
        cancelEvict.setVisible(false);
        activate.setVisible(false);
        super.reset();
    }

    @Override
    public void populate(LeaseDTO value) {
        super.populate(value);

        Status status = value.version().status().getValue();

        // set buttons state:
        if (!value.unit().isNull()) {
            CompletionType completion = value.version().completion().getValue();

            sendMail.setVisible(true);
            runBill.setVisible(status.isCurrent());
            notice.setVisible(status == Status.Active && completion == null);
            cancelNotice.setVisible(completion == CompletionType.Notice && status != Status.Closed);
            evict.setVisible(status == Status.Active && completion == null);
            cancelEvict.setVisible(completion == CompletionType.Eviction && status != Status.Closed);
            activate.setVisible(status == Status.Created);
        }

        // disable editing for completed/closed leases:
        getEditButton().setVisible(!status.isFormer());

        // tweak finalizing availability:
        if (getFinalizeButton().isVisible()) {
            getFinalizeButton().setVisible(!value.unit().isNull() && status.isCurrent());
        }
    }

    @Override
    public IListerView<BillDTO> getBillListerView() {
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

        private CEntityDecoratableEditor<LeaseDTO> content;

        public TermLeaseBox(CompletionType action) {
            super(i18n.tr("Please select"));
            this.action = action;
            setBody(createBody());
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            content = new CEntityDecoratableEditor<LeaseDTO>(LeaseDTO.class) {
                @Override
                public IsWidget createContent() {
                    FormFlexPanel main = new FormFlexPanel();
                    switch (action) {
                    case Notice:
                        main.setWidget(0, 0, new DecoratorBuilder(inject(proto().version().moveOutNotice()), 9).customLabel(i18n.tr("Notice Date")).build());
                        break;
                    case Eviction:
                        main.setWidget(0, 0, new DecoratorBuilder(inject(proto().version().moveOutNotice()), 9).customLabel(i18n.tr("Evict Date")).build());
                        break;
                    }
                    main.setWidget(1, 0, new DecoratorBuilder(inject(proto().version().expectedMoveOut()), 9).build());

                    // just for validation purpose:
                    inject(proto().leaseFrom());
                    inject(proto().leaseTo());

                    return main;
                }

                @Override
                protected void onPopulate() {
                    super.onPopulate();

                    if (getValue().version().moveOutNotice().isNull()) {
                        get(proto().version().moveOutNotice()).setValue(new LogicalDate());
                    }

                    if (getValue().version().expectedMoveOut().isNull()) {
                        get(proto().version().expectedMoveOut()).setValue(new LogicalDate());
                    }
                }

                @Override
                public void addValidations() {
                    super.addValidations();

                    new DateInPeriodValidation(get(proto().leaseFrom()), get(proto().version().moveOutNotice()), get(proto().leaseTo()),
                            i18n.tr("The Date Should Be Within The Lease Period"));
                    new DateInPeriodValidation(get(proto().leaseFrom()), get(proto().version().expectedMoveOut()), get(proto().leaseTo()),
                            i18n.tr("The Date Should Be Within The Lease Period"));

                    new StartEndDateValidation(get(proto().version().moveOutNotice()), get(proto().version().expectedMoveOut()),
                            i18n.tr("The Notice Date Must Be Earlier Than The Expected Move Out date"));
                    get(proto().version().moveOutNotice())
                            .addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().expectedMoveOut())));
                    get(proto().version().expectedMoveOut())
                            .addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().moveOutNotice())));
                }
            };

            content.initContent();
            content.populate(form.getValue());
            return content.asWidget();
        }

        public LeaseDTO getValue() {
            return content.getValue();
        }
    }

    private abstract class SendMailBox extends EntitySelectorListDialog<ApplicationUserDTO> {

        private CComboBox<EmailTemplateType> emailType;

        public SendMailBox(List<ApplicationUserDTO> applicationUsers) {
            super(i18n.tr("Send Mail"), true, applicationUsers, new EntitySelectorListDialog.Formatter<ApplicationUserDTO>() {
                @Override
                public String format(ApplicationUserDTO entity) {
                    return SimpleMessageFormat.format(//@formatter:off
                            "{0}, {1}",
                            entity.leaseParticipant().customer().person().name().getStringView(),
                            entity.role().getStringView()
                    );//@formatter:on
                }
            });

            getOkButton().setText(i18n.tr("Send"));
        }

        @Override
        protected Widget initBody(boolean isMultiselectAllowed, List<ApplicationUserDTO> data) {
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

        @Override
        public String defineWidth() {
            return "350px";
        }

        @Override
        public String defineHeight() {
            return "100px";
        }
    }

    @Override
    public void reportSendMailActionResult(String message) {
        MessageDialog.info(message);
    }

}