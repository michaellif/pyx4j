/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.tenantinsurance;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;

import com.propertyvista.common.client.ui.components.ApplicationDocumentFileUploaderFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.media.InsuranceCertificateDocument;

public class InsuranceCertificateDocumentEditor extends CEntityDecoratableForm<InsuranceCertificateDocument> {

    public InsuranceCertificateDocumentEditor() {
        super(InsuranceCertificateDocument.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        panel.setWidget(0, 0, inject(proto().documentPages(), new ApplicationDocumentFileUploaderFolder()));
        addValueValidator(new EditableValueValidator<InsuranceCertificateDocument>() {
            @Override
            public ValidationError isValid(CComponent<InsuranceCertificateDocument> component, InsuranceCertificateDocument value) {
                if (value != null && value.documentPages().isEmpty()) {
                    return new ValidationError(component, TenantInsuranceCertificateForm.i18n.tr("Please upload the insurance certificate"));
                } else {
                    return null;
                }
            }
        });
        return panel;
    }
}