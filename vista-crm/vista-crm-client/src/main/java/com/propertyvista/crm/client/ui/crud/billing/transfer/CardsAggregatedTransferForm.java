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
 */
package com.propertyvista.crm.client.ui.crud.billing.transfer;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.crm.rpc.dto.financial.CardsAggregatedTransferDTO;
import com.propertyvista.domain.security.common.VistaBasicBehavior;

public class CardsAggregatedTransferForm extends CForm<CardsAggregatedTransferDTO> {

    private static final I18n i18n = I18n.get(CardsAggregatedTransferForm.class);

    private final AggregatedTransferViewerView view;

    public CardsAggregatedTransferForm(AggregatedTransferViewerView view) {
        super(CardsAggregatedTransferDTO.class, new VistaEditorsComponentFactory());
        this.view = view;
        init();
        setViewable(true);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().visaDeposit()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().visaFee()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().mastercardDeposit()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().mastercardFee()).decorate().componentWidth(120);

        if (SecurityController.check(VistaBasicBehavior.PropertyVistaSupport)) {
            formPanel.h3("Vista Support Trace info");
            formPanel.append(Location.Dual, proto().fileMerchantTotal()).decorate();
            formPanel.append(Location.Dual, proto().fileCardTotal()).decorate();
        }

        return formPanel;

    }
}