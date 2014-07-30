/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComboBoxBoolean;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;

public class PaymentFeesForm<E extends AbstractPaymentFees> extends CForm<E> {

    public PaymentFeesForm(Class<E> clazz) {
        super(clazz);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().ccVisaFee()).decorate();
        formPanel.append(Location.Left, proto().ccMasterCardFee()).decorate();

        formPanel.append(Location.Left, proto().visaDebitFee()).decorate();

        formPanel.append(Location.Left, proto().eChequeFee()).decorate();
        formPanel.append(Location.Left, proto().directBankingFee()).decorate();

        formPanel.append(Location.Left, proto().acceptedEcheck(), new CComboBoxBoolean()).decorate();
        formPanel.append(Location.Left, proto().acceptedDirectBanking(), new CComboBoxBoolean()).decorate();
        formPanel.append(Location.Left, proto().acceptedVisa(), new CComboBoxBoolean()).decorate();
        formPanel.append(Location.Left, proto().acceptedVisaConvenienceFee(), new CComboBoxBoolean()).decorate();
        formPanel.append(Location.Left, proto().acceptedVisaDebit(), new CComboBoxBoolean()).decorate();
        formPanel.append(Location.Left, proto().acceptedVisaDebitConvenienceFee(), new CComboBoxBoolean()).decorate();
        formPanel.append(Location.Left, proto().acceptedMasterCard(), new CComboBoxBoolean()).decorate();
        formPanel.append(Location.Left, proto().acceptedMasterCardConvenienceFee(), new CComboBoxBoolean()).decorate();

        return formPanel;
    }
}
