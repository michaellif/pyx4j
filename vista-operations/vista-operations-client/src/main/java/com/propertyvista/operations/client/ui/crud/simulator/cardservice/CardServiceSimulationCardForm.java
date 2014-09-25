/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 25, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.cardservice;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.site.client.backoffice.ui.prime.form.AbstractEditor;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ListerDataSource;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationTransactionListerViewImpl.CardServiceSimulationTransactionLister;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationCard;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationTransactionCrudService;

class CardServiceSimulationCardForm extends OperationsEntityForm<CardServiceSimulationCard> {

    private CardServiceSimulationTransactionLister transactionLister;

    public CardServiceSimulationCardForm(IForm<CardServiceSimulationCard> view) {
        super(CardServiceSimulationCard.class, view);

        transactionLister = new CardServiceSimulationTransactionListerViewImpl.CardServiceSimulationTransactionLister();

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().cardType()).decorate();
        formPanel.append(Location.Left, proto().number()).decorate();
        formPanel.append(Location.Left, proto().expiryDate()).decorate();
        formPanel.append(Location.Left, proto().creditLimit()).decorate();
        formPanel.append(Location.Left, proto().balance()).decorate();
        formPanel.append(Location.Left, proto().reserved()).decorate();
        formPanel.append(Location.Left, proto().responseCode()).decorate();

        formPanel.append(Location.Right, proto().created()).decorate();
        formPanel.append(Location.Right, proto().updated()).decorate();

        formPanel.h2("Tokens");
        formPanel.append(Location.Dual, inject(proto().tokens(), new CardServiceSimulationCardTokenTableFolder()));

        formPanel.h2("Transactions");

        formPanel.append(Location.Dual, new Button("Add New Transaction...", new Command() {
            @Override
            public void execute() {
                if (getValue().getPrimaryKey() == null) {
                    MessageDialog.info("Save the Card Simulation First");
                    return;
                }
                getPresenter().addTransaction();
            }
        }));
        formPanel.append(Location.Dual, transactionLister);
        transactionLister.setDataSource(new ListerDataSource<CardServiceSimulationTransaction>(CardServiceSimulationTransaction.class, GWT
                .<CardServiceSimulationTransactionCrudService> create(CardServiceSimulationTransactionCrudService.class)));

        setTabBarVisible(false);
        selectTab(addTab(formPanel, "Card Service Simulation"));

    }

    @SuppressWarnings("unchecked")
    CardServiceSimulationCardEditorView.Presenter getPresenter() {
        return (CardServiceSimulationCardEditorView.Presenter) ((AbstractEditor<CardServiceSimulationCard>) getParentView()).getPresenter();
    }

    @Override
    public void addValidations() {
        this.addDevShortcutHandler(new DevShortcutHandler() {
            @Override
            public void onDevShortcut(DevShortcutEvent event) {
                if (event.getKeyCode() == 'Q') {
                    event.consume();
                    generateCreditCard();
                }
            }

        });
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        transactionLister.getDataSource().setParentFiltering(getValue().getPrimaryKey());
        transactionLister.obtain(0);
    }

    private void generateCreditCard() {
        if (get(proto().cardType()).getValue() == null) {
            get(proto().cardType()).setValue(CreditCardType.Visa);
        }
        ((CTextFieldBase<?, ?>) get(proto().number())).setValueByString(CreditCardNumberGenerator.generateCardNumber(get(proto().cardType()).getValue()));

        LogicalDate nextMonth = new LogicalDate();
        TimeUtils.addDays(nextMonth, 31);
        get(proto().expiryDate()).setValue(nextMonth);
    }
}