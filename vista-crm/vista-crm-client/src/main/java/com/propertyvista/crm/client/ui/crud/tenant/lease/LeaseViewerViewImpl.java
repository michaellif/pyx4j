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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.validators.DateInPeriodValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.tenant.lease.bill.BillLister;
import com.propertyvista.crm.client.ui.crud.tenant.lease.payment.PaymentLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.version.LeaseVersionService;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Payment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerViewImpl extends CrmViewerViewImplBase<LeaseDTO> implements LeaseViewerView {

    private static final I18n i18n = I18n.get(LeaseViewerViewImpl.class);

    private final IListerView<Bill> billLister;

    private final IListerView<Payment> paymentLister;

    private final Button createApplication;

    private final Button runBill;

    private final Button notice;

    private final Button cancelNotice;

    private final Button evict;

    private final Button cancelEvict;

    public LeaseViewerViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);
        enableVersioning(Lease.LeaseV.class, GWT.<LeaseVersionService> create(LeaseVersionService.class));

        billLister = new ListerInternalViewImplBase<Bill>(new BillLister());

        paymentLister = new ListerInternalViewImplBase<Payment>(new PaymentLister());

        //set main form here:
        setForm(new LeaseEditorForm(true));

        // Add actions:
        createApplication = new Button(i18n.tr("Create Application"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ApplicationBox() {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) presenter).createMasterApplication(isInvite());
                        return true;
                    }
                }.show();
            }
        });
        addToolbarItem(createApplication.asWidget());

        runBill = new Button(i18n.tr("Run Bill"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).startBilling();
            }
        });
        addToolbarItem(runBill.asWidget());

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
        addToolbarItem(notice.asWidget());

        cancelNotice = new Button(i18n.tr("Cancel Notice"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).cancelNotice();
            }
        });
        addToolbarItem(cancelNotice.asWidget());

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
        addToolbarItem(evict.asWidget());

        cancelEvict = new Button(i18n.tr("Cancel Evict"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).cancelEvict();
            }
        });
        addToolbarItem(cancelEvict.asWidget());
    }

    @Override
    public void populate(LeaseDTO value) {
        super.populate(value);

        // set buttons state:
        Status status = value.version().status().getValue();
        CompletionType completion = value.version().completion().getValue();

        // disable editing for completed/closed leases:
        getEditButton().setVisible(status != Status.Closed);

        createApplication.setVisible(status == Status.Created);

        runBill.setVisible(status == Status.Active);

        notice.setVisible(status == Status.Active && completion == null);
        cancelNotice.setVisible(completion == CompletionType.Notice && status != Status.Closed);
        evict.setVisible(status == Status.Active && completion == null);
        cancelEvict.setVisible(completion == CompletionType.Eviction && status != Status.Closed);
    }

    @Override
    public IListerView<Bill> getBillListerView() {
        return billLister;
    }

    @Override
    public IListerView<Payment> getPaymentListerView() {
        return paymentLister;
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

    private abstract class ApplicationBox extends OkCancelDialog {

        private final CCheckBox invite = new CCheckBox();

        public ApplicationBox() {
            super(i18n.tr("Please select"));
            invite.setValue(true);
            setBody(createBody());
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            HorizontalPanel content = new HorizontalPanel();
            content.add(new Label(i18n.tr("Invite Applicant") + ": "));
            content.add(invite.asWidget());
            content.setSpacing(4);
            return content;
        }

        protected boolean isInvite() {
            return invite.getValue();
        }
    }
}