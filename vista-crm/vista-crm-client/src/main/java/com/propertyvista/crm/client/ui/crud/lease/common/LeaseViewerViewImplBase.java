/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.site.client.ui.backoffice.prime.lister.ILister;
import com.pyx4j.site.client.ui.backoffice.prime.lister.ListerInternalViewImplBase;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.Button.SecureMenuItem;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerViewImpl;
import com.propertyvista.crm.client.visor.paps.PreauthorizedPaymentsVisorController;
import com.propertyvista.crm.rpc.VistaCrmDebugId;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public class LeaseViewerViewImplBase<DTO extends LeaseDTO> extends CrmViewerViewImplBase<DTO> implements LeaseViewerViewBase<DTO> {

    private static final I18n i18n = I18n.get(LeaseViewerViewImpl.class);

    private final MenuItem viewParticipants;

    protected final ILister<PaymentRecordDTO> paymentLister;

    protected final Button termsButton;

    protected final Button.ButtonMenuBar termsMenu = new ButtonMenuBar();

    private final Button papsButton;

    private final Button.ButtonMenuBar papsMenu;

    protected final SecureMenuItem reserveUnit;

    protected final SecureMenuItem unreserveUnit;

    protected final SecureMenuItem newPaymentAction;

    public LeaseViewerViewImplBase() {
        super(true);

        paymentLister = new ListerInternalViewImplBase<PaymentRecordDTO>(new PaymentLister());

        // Buttons:

        termsButton = new Button(i18n.tr("Terms"));
        termsMenu.addItem(new MenuItem(i18n.tr("Effective"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).viewTerm(getForm().getValue().currentTerm());
            }
        }));
        termsButton.setMenu(termsMenu);
        addHeaderToolbarItem(termsButton.asWidget());

        // ----------------------------------------------------------------------------------------

        papsButton = new Button(i18n.tr("Auto Payments"), DataModelPermission.permissionRead(PreauthorizedPaymentsDTO.class));
        papsButton.ensureDebugId(VistaCrmDebugId.Payments.ActionAutoPayments.debugId());
        papsButton.setMenu(papsMenu = new ButtonMenuBar());
        addHeaderToolbarItem(papsButton.asWidget());

        // Views:

        viewParticipants = new MenuItem(i18n.tr("Tenants/Guarantors"), new Command() {
            @Override
            public void execute() {
                tenantsViewExecuter();
            }
        });
        addView(viewParticipants);

        // Actions:

        addAction(reserveUnit = new SecureMenuItem(i18n.tr("Reserve Unit"), new Command() {
            @Override
            public void execute() {
                reserveUnitExecuter();
            }
        }));

        addAction(unreserveUnit = new SecureMenuItem(i18n.tr("Release Unit"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).releaseUnit();
            }
        }));

        newPaymentAction = new SecureMenuItem(i18n.tr("Make Payment"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).newPayment();
            }
        }, DataModelPermission.permissionCreate(PaymentRecordDTO.class));
        newPaymentAction.ensureDebugId(VistaCrmDebugId.Payments.ActionCreatePayment.debugId());
        addAction(newPaymentAction);
    }

    private void tenantsViewExecuter() {
        ((LeaseViewerViewBase.Presenter) getPresenter()).retrieveParticipants(new DefaultAsyncCallback<List<LeaseTermParticipant<?>>>() {
            @Override
            public void onSuccess(List<LeaseTermParticipant<?>> result) {
                new EntitySelectorListDialog<LeaseTermParticipant<?>>(i18n.tr("Select Tenants/Guarantors To Navigate"), false, result) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerViewBase.Presenter) getPresenter()).navigateParticipant(getSelectedItems());
                        return true;
                    }
                }.show();
            }
        }, true);
    }

    private void reserveUnitExecuter() {
        new UnitReserveBox() {
            @Override
            public boolean onClickOk() {
                if (getDuration() != null) {
                    ((LeaseViewerViewBase.Presenter) getPresenter()).reserveUnit(getDuration());
                    return true;
                }
                return false;
            }
        }.show();
    }

    protected void setUnitReservationPermission(Permission... permission) {
        reserveUnit.setPermission(permission);
        unreserveUnit.setPermission(permission);
    }

    @Override
    public void reset() {
        papsButton.setVisible(false);

        setActionVisible(reserveUnit, false);
        setActionVisible(unreserveUnit, false);
        setActionVisible(newPaymentAction, false);

        super.reset();
    }

    @Override
    public void populate(DTO value) {
        super.populate(value);

        Lease.Status status = value.status().getValue();
        boolean reservationPreconditions = (!value.unit().isNull() && status.isDraft() && status != Lease.Status.ExistingLease);
        setActionVisible(reserveUnit, reservationPreconditions && !value.isUnitReserved().getValue(false));
        setActionVisible(unreserveUnit, reservationPreconditions && value.isUnitReserved().getValue(false));

        setupPapsMenu(value);
    }

    protected boolean isPaymentAccepted(DTO value) {
        return value.billingAccount().paymentAccepted().getValue() != BillingAccount.PaymentAccepted.DoNotAccept;
    }

    private void setupPapsMenu(DTO value) {
        papsMenu.clearItems();

        if (value.status().getValue().isActive() && value.electronicPaymentsAllowed().getValue(false)
                && !value.isMoveOutWithinNextBillingCycle().getValue(false)) {

            for (final LeaseTermTenant tenant : value.currentTerm().version().tenants()) {
                papsMenu.addItem(new MenuItem(tenant.getStringView(), new Command() {
                    @Override
                    public void execute() {
                        new PreauthorizedPaymentsVisorController(LeaseViewerViewImplBase.this, tenant.leaseParticipant().getPrimaryKey()) {
                            @Override
                            public boolean onClose(List<AutopayAgreement> pads) {
                                getPresenter().populate();
                                return true;
                            }
                        }.show();
                    }
                }));
            }
        }

        papsButton.setVisible(!papsMenu.getItems().isEmpty());
    }

    @Override
    public ILister<PaymentRecordDTO> getPaymentListerView() {
        return paymentLister;
    }

    private abstract class UnitReserveBox extends OkCancelDialog {

        private final CIntegerField duration = new CIntegerField();

        public UnitReserveBox() {
            super(i18n.tr("Unit Reservation"));
            setBody(createBody());
            setDialogPixelWidth(260);
        }

        protected Widget createBody() {
            HorizontalPanel content = new HorizontalPanel();

            content.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
            content.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            content.add(new HTML(i18n.tr("Enter the duration (in hours)") + ":"));
            content.add(duration);

            duration.setMandatory(true);
            duration.asWidget().setWidth("5em");

            content.setHeight("3em");
            content.setWidth("20em");
            return content;
        }

        public Integer getDuration() {
            return duration.getValue();
        }
    }
}
