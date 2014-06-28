/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.transfer;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;
import com.propertyvista.domain.financial.EftAggregatedTransfer;

public class AggregatedTransferFormPolymorphic extends CrmEntityForm<AggregatedTransfer> {

    private static final I18n i18n = I18n.get(AggregatedTransferFormPolymorphic.class);

    private final FormPanel formPanel = new FormPanel(this);

    CardsAggregatedTransferForm cardsAggregatedTransferForm;

    EftAggregatedTransferForm eftAggregatedTransferForm;

    public AggregatedTransferFormPolymorphic(IForm<AggregatedTransfer> view) {
        super(AggregatedTransfer.class, view);
        selectTab(addTab(formPanel, i18n.tr("General")));
    }

    @Override
    protected void onValuePropagation(AggregatedTransfer value, boolean fireEvent, boolean populate) {
        setPolimorphicData(value, fireEvent, populate);
        super.onValuePropagation(value, fireEvent, populate);
    }

    private void setPolimorphicData(AggregatedTransfer value, boolean fireEvent, boolean populate) {

        formPanel.clear();
        cardsAggregatedTransferForm = null;
        eftAggregatedTransferForm = null;

        if (value != null) {
            if (value.getInstanceValueClass().equals(CardsAggregatedTransfer.class)) {
                cardsAggregatedTransferForm = new CardsAggregatedTransferForm((AggregatedTransferViewerView) getParentView());
                formPanel.append(Location.Dual, cardsAggregatedTransferForm.asWidget());
            } else if (value.getInstanceValueClass().equals(EftAggregatedTransfer.class)) {
                eftAggregatedTransferForm = new EftAggregatedTransferForm((AggregatedTransferViewerView) getParentView());
                formPanel.append(Location.Dual, eftAggregatedTransferForm);
            } else {
                assert false;
            }
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        if (cardsAggregatedTransferForm != null) {
            cardsAggregatedTransferForm.setValue((CardsAggregatedTransfer) getValue(), true, populate);
        } else if (eftAggregatedTransferForm != null) {
            eftAggregatedTransferForm.setValue((EftAggregatedTransfer) getValue(), true, populate);
        } else {
            assert false;
        }
    }

    @Override
    public void onReset() {
        super.onReset();

        if (cardsAggregatedTransferForm != null) {
            cardsAggregatedTransferForm.reset();
        }
        if (eftAggregatedTransferForm != null) {
            eftAggregatedTransferForm.reset();
        }
    }
}