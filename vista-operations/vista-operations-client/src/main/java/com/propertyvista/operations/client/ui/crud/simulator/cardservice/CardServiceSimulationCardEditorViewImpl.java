/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.cardservice;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.operations.client.ui.components.OperationsEditorsComponentFactory;
import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.dev.CardServiceSimulationCard;
import com.propertyvista.operations.domain.dev.CardServiceSimulationToken;
import com.propertyvista.operations.domain.dev.CardServiceSimulationTransaction;

public class CardServiceSimulationCardEditorViewImpl extends OperationsEditorViewImplBase<CardServiceSimulationCard> implements
        CardServiceSimulationCardEditorView {

    private static class CardServiceSimulationTokenTableFolder extends VistaTableFolder<CardServiceSimulationToken> {

        private static List<EntityFolderColumnDescriptor> COLUMNS;
        static {
            CardServiceSimulationToken p = EntityFactory.getEntityPrototype(CardServiceSimulationToken.class);
            COLUMNS = Arrays.asList(new EntityFolderColumnDescriptor(p.token(), "25em"));
        }

        public CardServiceSimulationTokenTableFolder() {
            super(CardServiceSimulationToken.class);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return COLUMNS;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof CardServiceSimulationToken) {
                return new CEntityFolderRowEditor<CardServiceSimulationToken>(CardServiceSimulationToken.class, COLUMNS) {
                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        if (column == proto().token()) {
                            return inject(proto().token());
                        } else {
                            return super.createCell(column);
                        }
                    }
                };
            }
            return super.create(member);
        }

    }

    private static class CardServiceSimulationTransactionsViewer extends CViewer<ISet<CardServiceSimulationTransaction>> {

        @Override
        public IsWidget createContent(ISet<CardServiceSimulationTransaction> transactions) {
            FlowPanel panel = new FlowPanel();
            if (transactions != null) {
                for (CardServiceSimulationTransaction t : transactions) {
                    CEntityCrudHyperlink<CardServiceSimulationTransaction> hyperLink = OperationsEditorsComponentFactory
                            .createEntityHyperlink(CardServiceSimulationTransaction.class);
                    hyperLink.populate(t);
                    panel.add(hyperLink);
                }
            }
            return panel;
        }
    }

    private class CardServiceSimulationForm extends OperationsEntityForm<CardServiceSimulationCard> {

        public CardServiceSimulationForm(IForm<CardServiceSimulationCard> view) {
            super(CardServiceSimulationCard.class, view);

            TwoColumnFlexFormPanel contentPanel = new TwoColumnFlexFormPanel();

            int row = -1;
            contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().merchant())).build());
            contentPanel.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().created())).build());
            contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().cardType())).build());
            contentPanel.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().updated())).build());
            contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().number())).build());
            contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().expiryDate())).build());
            contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().balance())).build());
            contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reserved())).build());
            contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().responseCode())).build());

            contentPanel.setH2(++row, 0, 2, "Tokens");
            contentPanel.setWidget(++row, 0, 2, inject(proto().tokens(), new CardServiceSimulationTokenTableFolder()));

            contentPanel.setH2(++row, 0, 2, "Transactions");

            if (false) {
                // TODO transactions are detached
                contentPanel.setWidget(++row, 0, 2, inject(proto().transactions(), new CardServiceSimulationTransactionsViewer()));
            }
            contentPanel.setWidget(++row, 0, 2, new Button("Add New Transaction...", new Command() {
                @Override
                public void execute() {
                    if (getValue().getPrimaryKey() == null) {
                        MessageDialog.info("Save the Card Simulation First");
                        return;
                    }
                    ((CardServiceSimulationCardEditorView.Presenter) CardServiceSimulationCardEditorViewImpl.this.getPresenter()).addTransaction();
                }
            }));

            setTabBarVisible(false);
            selectTab(addTab(contentPanel));

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

    public CardServiceSimulationCardEditorViewImpl() {
        setForm(new CardServiceSimulationForm(this));
    }

}
