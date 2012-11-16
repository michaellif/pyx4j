/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.otherprovider.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.otherprovider.OtherProvidersTenantInsuranceDetailsDTO;

public class OtherProvidersTenantInsuranceDetailsForm extends CEntityDecoratableForm<OtherProvidersTenantInsuranceDetailsDTO> {

    private final static I18n i18n = I18n.get(OtherProvidersTenantInsuranceDetailsForm.class);

    public OtherProvidersTenantInsuranceDetailsForm() {
        super(OtherProvidersTenantInsuranceDetailsDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceProvider()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceCertificateNumber()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().personalLiability()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().startDate()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().expirationDate()), 10).build());
        content.setH2(++row, 0, 1, i18n.tr("Attach Scanned Insurance Certificate"));
        //content.setWidget(++row, 0, inject(proto().documentPages(), new ApplicationDocumentFileUploaderFolder()));

        addValueValidator(new EditableValueValidator<OtherProvidersTenantInsuranceDetailsDTO>() {

            @Override
            public ValidationError isValid(CComponent<OtherProvidersTenantInsuranceDetailsDTO, ?> component, OtherProvidersTenantInsuranceDetailsDTO value) {
                if (!component.isValid()) {
                    return new ValidationError(OtherProvidersTenantInsuranceDetailsForm.this, i18n.tr("Valid Proof of Insurance is Required"));
                } else {
                    return null;
                }
            }
        });
        return content;
    }

}