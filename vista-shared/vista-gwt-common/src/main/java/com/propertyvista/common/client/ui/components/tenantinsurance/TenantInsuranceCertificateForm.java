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
package com.propertyvista.common.client.ui.components.tenantinsurance;

import java.math.BigDecimal;
import java.util.Date;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.ApplicationDocumentFileUploaderFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.media.InsuranceCertificateDocument;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.insurance.InsuranceGeneric;
import com.propertyvista.domain.tenant.lease.Tenant;

public class TenantInsuranceCertificateForm extends CEntityDecoratableForm<InsuranceGeneric> {

    private final static I18n i18n = I18n.get(TenantInsuranceCertificateForm.class);

    public interface TenantOwnerClickHandler {

        void onTenantOwnerClicked(Tenant tenantId);

    }

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
                        return new ValidationError(component, i18n.tr("Please upload the insurance cerificate"));
                    } else {
                        return null;
                    }
                }
            });
            return panel;
        }
    }

    private BigDecimal minRequiredLiability;

    private final boolean displayTenantOwner;

    private final TenantOwnerClickHandler tenantOwnerClickHandler;

    private Label scannedInsuranceCertificateNotAvailable;

    /**
     * @param displayTenantOwner
     *            display the owners name (if true then populated insurance certificated entity <b>must</b> have the tenant.customer.person() name)
     * @param tenantOwnerClickHandler
     *            a handler for tenantOwner click (if not null will render tenant's name as a hyperlink that execs this handler on click)
     */
    public TenantInsuranceCertificateForm(boolean displayTenantOwner, TenantOwnerClickHandler tenantOwnerClickHandler) {
        super(InsuranceGeneric.class);
        this.minRequiredLiability = null;
        this.displayTenantOwner = displayTenantOwner;
        this.tenantOwnerClickHandler = tenantOwnerClickHandler;
    }

    public TenantInsuranceCertificateForm() {
        this(false, null);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        if (displayTenantOwner) {
            CComponent<Customer, ?> comp = null;
            if (tenantOwnerClickHandler != null) {
                comp = new CEntityHyperlink<Customer>(i18n.tr("Tenant"), new Command() {
                    @Override
                    public void execute() {
                        TenantInsuranceCertificateForm.this.tenantOwnerClickHandler.onTenantOwnerClicked(getValue().tenant().<Tenant> createIdentityStub());
                    }
                });
            } else {
                comp = new CLabel<Customer>();
            }
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().tenant(), comp), 15).build());
        }

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceProvider()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceCertificateNumber()), 20).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().liabilityCoverage()), 20).build());
        get(proto().liabilityCoverage()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal, ?> component, BigDecimal value) {
                if (TenantInsuranceCertificateForm.this.minRequiredLiability != null && value != null && value.compareTo(minRequiredLiability) < 0) {
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

        content.setH2(++row, 0, 1, isEditable() ? i18n.tr("Attach Scanned Insurance Certificate") : i18n.tr("Scanned Insurance Certificate"));
        content.setWidget(++row, 0, inject(proto().documents(), new InsuranceCertificateDocumentFolder()));
        scannedInsuranceCertificateNotAvailable = new Label(i18n.tr("N/A"));
        scannedInsuranceCertificateNotAvailable.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        scannedInsuranceCertificateNotAvailable.getElement().getStyle().setProperty("marginLeft", "auto");
        scannedInsuranceCertificateNotAvailable.getElement().getStyle().setProperty("marginRight", "auto");
        scannedInsuranceCertificateNotAvailable.setVisible(false);
        content.setWidget(++row, 0, scannedInsuranceCertificateNotAvailable);
        return content;
    }

    public void setMinRequiredLiability(BigDecimal minRequiredLiability) {
        this.minRequiredLiability = minRequiredLiability;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        scannedInsuranceCertificateNotAvailable.setVisible(!isEditable() & getValue().documents().isEmpty());
        setViewable(getValue().isPropertyVistaIntegratedProvider().isBooleanTrue());
    }
}