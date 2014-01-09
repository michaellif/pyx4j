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
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;

import com.propertyvista.crm.client.ui.components.boxes.LeaseTermSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerViewImpl;
import com.propertyvista.crm.client.visor.paps.PreauthorizedPaymentsVisorController;
import com.propertyvista.domain.payment.AutopayAgreement;
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

    private MenuItem downloadAgreementItem;

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

        Button downloadAgreementButton = new Button(i18n.tr("Download"));

        ButtonMenuBar downloadAgreementMenu = downloadAgreementButton.createMenu();
        MenuItem downloadBlankAgreementItem = new MenuItem(i18n.tr("Download Bank Agreement"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).downloadBlankAgreement();
            }
        });
        downloadAgreementMenu.addItem(downloadBlankAgreementItem);

        downloadAgreementItem = new MenuItem(i18n.tr("Download Signed Agreement"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).downloadSignedAgreement(getForm().getValue().currentTerm().version().agreementDocument());
            }
        });
        downloadAgreementMenu.addItem(downloadAgreementItem);

        downloadAgreementButton.setMenu(downloadAgreementMenu);
        addHeaderToolbarItem(downloadAgreementButton);

    }

    @Override
    public void reset() {
        viewFutureTerm.setVisible(false);

        papsButton.setVisible(false);

        super.reset();
    }

    @Override
    public void populate(DTO value) {
        super.populate(value);

        viewFutureTerm.setVisible(!value.nextTerm().isNull());
        viewHistoricTerms.setVisible(value.historyPresent().isBooleanTrue());

        downloadAgreementItem.setVisible(!value.currentTerm().version().agreementDocument().isNull());

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
}
