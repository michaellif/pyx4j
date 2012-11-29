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

import java.math.BigDecimal;
import java.util.Date;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.ApplicationDocumentFileUploaderFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.media.InsuranceCertificateDocument;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;

public class TenantInsuranceByOtherProviderDetailsForm extends CEntityDecoratableForm<InsuranceCertificate> {

    private final static I18n i18n = I18n.get(TenantInsuranceByOtherProviderDetailsForm.class);

    private static class InsuranceCertificateDocumentFolder extends VistaBoxFolder<InsuranceCertificateDocument> {

        public InsuranceCertificateDocumentFolder() {
            super(InsuranceCertificateDocument.class);
            setAddable(false);
            setRemovable(false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof InsuranceCertificateDocument) {
                return new InsuranceCertificateDocumentEditor();
            } else {
                return super.create(member);
            }
        }
    }

    private static class InsuranceCertificateDocumentEditor extends CEntityDecoratableForm<InsuranceCertificateDocument> {

        public InsuranceCertificateDocumentEditor() {
            super(InsuranceCertificateDocument.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel panel = new FormFlexPanel();
            panel.setWidget(0, 0, inject(proto().documentPages(), new ApplicationDocumentFileUploaderFolder()));
            addValueValidator(new EditableValueValidator<InsuranceCertificateDocument>() {
                @Override
                public ValidationError isValid(CComponent<InsuranceCertificateDocument, ?> component, InsuranceCertificateDocument value) {
                    if (value != null && value.documentPages().isEmpty()) {
                        return new ValidationError(component, i18n.tr("Please upload your insurance cerificate"));
                    } else {
                        return null;
                    }
                }
            });
            return panel;
        }
    }

    private BigDecimal minRequiredLiability;

    public TenantInsuranceByOtherProviderDetailsForm() {
        super(InsuranceCertificate.class);
        this.minRequiredLiability = null;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceProvider()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceCertificateNumber()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().liabilityCoverage()), 10).build());
        get(proto().liabilityCoverage()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal, ?> component, BigDecimal value) {
                if (TenantInsuranceByOtherProviderDetailsForm.this.minRequiredLiability != null && value != null && value.compareTo(minRequiredLiability) < 0) {
                    return new ValidationError(component, i18n.tr("The minimum required liability is {0,number,#,##0.00}", minRequiredLiability));
                }
                return null;
            }
        });
        get(proto().liabilityCoverage()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal, ?> component, BigDecimal value) {
                if (value != null && value.compareTo(BigDecimal.ZERO) <= 0) {
                    return new ValidationError(component, i18n.tr("Please enter a positive value"));
                }
                return null;
            }
        });
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().inceptionDate()), 10).build());

        get(proto().inceptionDate()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public ValidationError isValid(CComponent<Date, ?> component, Date value) {
                if (value != null && value.compareTo(new LogicalDate()) > 0) {
                    return new ValidationError(component, i18n.tr("Please provide a date less than or equal of today"));
                }
                return null;
            }
        });
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().expiryDate()), 10).build());
        get(proto().expiryDate()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public ValidationError isValid(CComponent<Date, ?> component, Date value) {
                if (value != null && value.compareTo(new LogicalDate()) < 0) {
                    return new ValidationError(component, i18n.tr("Please provide a date greater than or equal of today"));
                }
                return null;
            }
        });
        content.setH2(++row, 0, 1, i18n.tr("Attach Scanned Insurance Certificate"));
        content.setWidget(++row, 0, inject(proto().documents(), new InsuranceCertificateDocumentFolder()));

        return content;
    }

    public void setMinRequiredLiability(BigDecimal minRequiredLiability) {
        this.minRequiredLiability = minRequiredLiability;
    }
}