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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
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
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Payment;
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

        billLister = new ListerInternalViewImplBase<Bill>(new BillLister());

        paymentLister = new ListerInternalViewImplBase<Payment>(new PaymentLister());

        //set main form here:
        setForm(new LeaseEditorForm(true));

        createApplication = new Button(i18n.tr("Create Application"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).createMasterApplication();
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
                new ActionBox(CompletionType.Notice) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) presenter).notice(getValue().moveOutNotice().getValue(), getValue().expectedMoveOut().getValue());
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
                new ActionBox(CompletionType.Eviction) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) presenter).evict(getValue().moveOutNotice().getValue(), getValue().expectedMoveOut().getValue());
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
        Status status = value.status().getValue();
        CompletionType completion = value.completion().getValue();

        // disable editing for signed leases:
        getEditButton().setVisible(value.approvalDate().isNull());

        createApplication.setVisible(status == Status.Draft);

        runBill.setVisible(status == Status.Active);

        notice.setVisible(status == Status.Active && completion == null);
        cancelNotice.setVisible(completion == CompletionType.Notice);
        evict.setVisible(status == Status.Active && completion == null);
        cancelEvict.setVisible(completion == CompletionType.Eviction);
    }

    @Override
    public IListerView<Bill> getBillListerView() {
        return billLister;
    }

    @Override
    public IListerView<Payment> getPaymentListerView() {
        return paymentLister;
    }

    private abstract class ActionBox extends OkCancelDialog {

        private final CompletionType action;

        private CEntityDecoratableEditor<LeaseDTO> content;

        public ActionBox(CompletionType action) {
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
                        main.setWidget(0, 0, new DecoratorBuilder(inject(proto().moveOutNotice()), 9).customLabel(i18n.tr("Notice Date")).build());
                        break;
                    case Eviction:
                        main.setWidget(0, 0, new DecoratorBuilder(inject(proto().moveOutNotice()), 9).customLabel(i18n.tr("Evict Date")).build());
                        break;
                    }
                    main.setWidget(1, 0, new DecoratorBuilder(inject(proto().expectedMoveOut()), 9).build());

                    // just for validation purpose:
                    inject(proto().leaseFrom());
                    inject(proto().leaseTo());

                    return main;
                }

                @Override
                protected void onPopulate() {
                    super.onPopulate();

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

                    new DateInPeriodValidation(get(proto().leaseFrom()), get(proto().moveOutNotice()), get(proto().leaseTo()),
                            i18n.tr("The Date Should Be Within The Lease Period"));
                    new DateInPeriodValidation(get(proto().leaseFrom()), get(proto().expectedMoveOut()), get(proto().leaseTo()),
                            i18n.tr("The Date Should Be Within The Lease Period"));

                    new StartEndDateValidation(get(proto().moveOutNotice()), get(proto().expectedMoveOut()),
                            i18n.tr("The Notice Date Must Be Earlier Than The Expected Move Out date"));
                    get(proto().moveOutNotice()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expectedMoveOut())));
                    get(proto().expectedMoveOut()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().moveOutNotice())));
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
}