/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.payments;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.validators.EcheckAccountNumberValidator;
import com.propertyvista.common.client.ui.validators.EcheckBankIdValidator;
import com.propertyvista.common.client.ui.validators.EcheckBranchTransitValidator;
import com.propertyvista.domain.payment.AccountNumberIdentity;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.shared.util.EcheckFormatter;

public class EcheckInfoEditor extends CForm<EcheckInfo> {

    protected final CPersonalIdentityField<AccountNumberIdentity> accountEditor = new CPersonalIdentityField<AccountNumberIdentity>(
            AccountNumberIdentity.class, new EcheckFormatter());

    public EcheckInfoEditor() {
        super(EcheckInfo.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().nameOn()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().accountNo()).decorate().componentWidth(250);

        formPanel.append(Location.Left, proto().branchTransitNumber()).decorate().componentWidth(80);
        formPanel.append(Location.Left, proto().bankId()).decorate().componentWidth(50);

        if (!isViewable() && isEditable()) {
            Image image = new Image(VistaImages.INSTANCE.eChequeGuide().getSafeUri());
            image.getElement().getStyle().setMarginTop(1, Unit.EM);
            formPanel.append(Location.Dual, image);
        }

        return formPanel;
    }

    @Override
    public void addValidations() {
        get(proto().accountNo()).addComponentValidator(new EcheckAccountNumberValidator());
        get(proto().branchTransitNumber()).addComponentValidator(new EcheckBranchTransitValidator());
        get(proto().bankId()).addComponentValidator(new EcheckBankIdValidator());
    }

    @Override
    public void generateMockData() {
        get(proto().nameOn()).setMockValue("Dev");
        get(proto().bankId()).setMockValue("123");
        get(proto().branchTransitNumber()).setMockValue("12345");

        CTextFieldBase<?, ?> id = (CTextFieldBase<?, ?>) get(proto().accountNo());
        id.setMockValueByString(String.valueOf(System.currentTimeMillis() % 10000000));
    }
}
