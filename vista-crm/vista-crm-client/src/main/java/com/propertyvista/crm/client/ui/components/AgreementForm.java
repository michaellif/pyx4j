/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.dto.vista2pmc.AgreementDTO;

public class AgreementForm extends CEntityForm<AgreementDTO> {

    private static final I18n i18n = I18n.get(AgreementForm.class);

    private final String signatureTextHtml;

    private PmcSignatureForm pmcSigatureForm;

    public AgreementForm(String signatureTextHtml) {
        super(AgreementDTO.class);
        this.signatureTextHtml = signatureTextHtml;
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel contentPanel = new TwoColumnFlexFormPanel();
        int row = -1;
        contentPanel.setWidget(++row, 0, inject(proto().terms()));
        get(proto().terms()).setViewable(true);

        contentPanel.setWidget(++row, 0, new WidgetDecoratorRightLabel(inject(proto().isAgreed()), 2, 40));
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingTop(20, Unit.PX);
        get(proto().isAgreed()).addValueValidator(new EditableValueValidator<Boolean>() {
            @Override
            public ValidationError isValid(CComponent<Boolean> component, Boolean value) {
                if (value != null && !value) {
                    return new ValidationError(component, i18n.tr("You must agree with the above terms to continue."));
                } else {
                    return null;
                }
            }
        });

        Label caledonSignatureText = new Label();
        caledonSignatureText.setHTML(signatureTextHtml);
        contentPanel.setWidget(++row, 0, caledonSignatureText);

        pmcSigatureForm = new PmcSignatureForm();
        contentPanel.setWidget(++row, 0, inject(proto().agreementSignature(), pmcSigatureForm));
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        return contentPanel;
    }

    public void setIsAgreedTitle(String label) {
        get(proto().isAgreed()).setTitle(label);
    }

    public void setSignature(String ownerName, String pmcLegalName) {
        pmcSigatureForm.setFullName(ownerName);
        pmcSigatureForm.setPmcLegalName(pmcLegalName);
    }

}
