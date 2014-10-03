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

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction;

public class CardServiceSimulationTransactionEditorViewImpl extends OperationsEditorViewImplBase<CardServiceSimulationTransaction> implements
        CardServiceSimulationTransactionEditorView {

    private static final I18n i18n = I18n.get(CardServiceSimulationTransactionEditorViewImpl.class);

    Button createReturn;

    public CardServiceSimulationTransactionEditorViewImpl() {
        setForm(new CardServiceSimulationTransactionForm(this));

        createReturn = new Button(i18n.tr("Create Return"), new Command() {
            @Override
            public void execute() {
                ((CardServiceSimulationTransactionEditorView.Presenter) getPresenter()).createReturn();
            }
        });
        addHeaderToolbarItem(createReturn);
    }

}
