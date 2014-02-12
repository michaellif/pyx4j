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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.components.boxes.LeaseTermSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerViewImpl;
import com.propertyvista.crm.client.visor.paps.PreauthorizedPaymentsVisorController;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerViewImplBase<DTO extends LeaseDTO> extends CrmViewerViewImplBase<DTO> implements LeaseViewerViewBase<DTO> {

    private static final I18n i18n = I18n.get(LeaseViewerViewImpl.class);

    protected final Button termsButton;

    protected final Button papsButton;

    protected final Button.ButtonMenuBar papsMenu;

    protected final MenuItem viewFutureTerm;

    protected final MenuItem viewHistoricTerms;

    private final MenuItem reserveUnit;

    private final MenuItem unreserveUnit;

    public LeaseViewerViewImplBase() {
        super(true);

        termsButton = new Button(i18n.tr("Terms"));
        Button.ButtonMenuBar termsMenu = termsButton.createMenu();
        termsButton.setMenu(termsMenu);
        addHeaderToolbarItem(termsButton.asWidget());

        MenuItem viewCurrentTerm = new MenuItem(i18n.tr("Current"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).viewTerm(getForm().getValue().currentTerm());
            }
        });
        termsMenu.addItem(viewCurrentTerm);

        viewFutureTerm = new MenuItem(i18n.tr("Future"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).viewTerm(getForm().getValue().nextTerm());
            }
        });
        termsMenu.addItem(viewFutureTerm);

        viewHistoricTerms = new MenuItem(i18n.tr("Historic..."), new Command() {
            @Override
            public void execute() {
                new LeaseTermSelectorDialog() {
                    {
                        setParentFiltering(getForm().getValue().getPrimaryKey());
                        addFilter(PropertyCriterion.ne(proto().status(), LeaseTerm.Status.Offer));
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
        termsMenu.addItem(viewHistoricTerms);

        papsButton = new Button(i18n.tr("Auto Payments"));
        papsButton.setMenu(papsMenu = papsButton.createMenu());
        addHeaderToolbarItem(papsButton.asWidget());

        Button legalStatusButton = new Button(i18n.tr("Legal"));
        ButtonMenuBar legalStatusMenu = legalStatusButton.createMenu();
        legalStatusButton.setMenu(legalStatusMenu);
        MenuItem setLegalStatus = new MenuItem(i18n.tr("Set Legal Status"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).setLegalStatus();
            }
        });
        legalStatusMenu.addItem(setLegalStatus);
        MenuItem clearLegalStatus = new MenuItem(i18n.tr("Clear Legal Status"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).clearLegalStatus();
            }
        });
        legalStatusMenu.addItem(clearLegalStatus);
        addHeaderToolbarItem(legalStatusButton);

        reserveUnit = new MenuItem(i18n.tr("Reserve Unit"), new Command() {
            @Override
            public void execute() {
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
        });
        addAction(reserveUnit);

        unreserveUnit = new MenuItem(i18n.tr("Unreserve Unit"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).unreserveUnit();
            }
        });
        addAction(unreserveUnit);
        addActionSeparator();
    }

    @Override
    public void reset() {
        viewFutureTerm.setVisible(false);

        papsButton.setVisible(false);

        setActionVisible(reserveUnit, false);
        setActionVisible(unreserveUnit, false);

        super.reset();
    }

    @Override
    public void populate(DTO value) {
        super.populate(value);

        viewFutureTerm.setVisible(!value.nextTerm().isNull());
        viewHistoricTerms.setVisible(value.historyPresent().isBooleanTrue());

        Lease.Status status = value.status().getValue();
        boolean reservationPreconditions = (!value.unit().isNull() && status.isDraft() && status != Lease.Status.ExistingLease);
        setActionVisible(reserveUnit, reservationPreconditions && !value.isUnitReserved().isBooleanTrue());
        setActionVisible(unreserveUnit, reservationPreconditions && value.isUnitReserved().isBooleanTrue());

        setupPapsMenu(value);
    }

    private void setupPapsMenu(DTO value) {
        papsMenu.clearItems();
        papsButton.setVisible(value.status().getValue().isCurrent() && !value.isMoveOutWithinNextBillingCycle().getValue(false));

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

    private abstract class UnitReserveBox extends OkCancelDialog {

        private final CIntegerField duration = new CIntegerField();

        public UnitReserveBox() {
            super(i18n.tr("Unit Reservation"));
            setBody(createBody());
            setDialogPixelWidth(300);
        }

        protected Widget createBody() {
            HorizontalPanel content = new HorizontalPanel();

            content.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
            content.add(new HTML(i18n.tr("Enter the duration (in hours)") + ":"));
            content.add(new HTML("&nbsp"));
            content.add(duration);

            duration.setMandatory(true);

            // styling:
            duration.asWidget().setWidth("5em");
            content.asWidget().getElement().getStyle().setMargin(1, Unit.EM);

            return new SimplePanel(content);
        }

        public Integer getDuration() {
            return duration.getValue();
        }
    }
}
