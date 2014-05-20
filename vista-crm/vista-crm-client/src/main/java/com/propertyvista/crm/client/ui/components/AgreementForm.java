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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.dto.vista2pmc.AgreementDTO;

public class AgreementForm extends CForm<AgreementDTO> {

    private static final I18n i18n = I18n.get(AgreementForm.class);

    private final String signatureTextHtml;

    private PmcSignatureForm pmcSigatureForm;

    public AgreementForm(String signatureTextHtml) {
        super(AgreementDTO.class);
        this.signatureTextHtml = signatureTextHtml;
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().terms());
        get(proto().terms()).setViewable(true);

        formPanel.append(Location.Left, new WidgetDecoratorRightLabel(inject(proto().isAgreed()), 2, 40));
        get(proto().isAgreed()).addComponentValidator(new AbstractComponentValidator<Boolean>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null && !getComponent().getValue()) {
                    return new BasicValidationError(getComponent(), i18n.tr("You must agree with the above terms to continue."));
                } else {
                    return null;
                }
            }
        });

        Label caledonSignatureText = new Label();
        caledonSignatureText.setHTML(signatureTextHtml);
        formPanel.append(Location.Left, caledonSignatureText);

        pmcSigatureForm = new PmcSignatureForm();
        formPanel.append(Location.Left, proto().agreementSignature(), pmcSigatureForm);

        return formPanel;
    }

    public void setIsAgreedTitle(String label) {
        get(proto().isAgreed()).setTitle(label);
    }

    public void setSignature(String ownerName, String pmcLegalName) {
        pmcSigatureForm.setFullName(ownerName);
        pmcSigatureForm.setPmcLegalName(pmcLegalName);
    }

}
