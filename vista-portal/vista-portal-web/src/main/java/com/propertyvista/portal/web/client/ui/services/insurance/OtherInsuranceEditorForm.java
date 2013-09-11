/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.services.insurance;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
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
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class OtherInsuranceEditorForm extends CEntityForm<InsuranceGeneric> {

    private final static I18n i18n = I18n.get(OtherInsuranceEditorForm.class);

    public interface TenantOwnerClickHandler {

        void onTenantOwnerClicked(Tenant tenantId);

    }

    private BigDecimal minRequiredLiability;

    private final boolean displayTenantOwner;

    private final TenantOwnerClickHandler tenantOwnerClickHandler;

    private Label scannedInsuranceCertificateNotAvailable;

    private int insuranceCeritificatesHeaderRow;

    private BasicFlexFormPanel contentPanel;

    /**
     * @param displayTenantOwner
     *            display the owners name (if true then populated insurance certificated entity <b>must</b> have the tenant.customer.person() name)
     * @param tenantOwnerClickHandler
     *            a handler for tenantOwner click (if not null will render tenant's name as a hyperlink that execs this handler on click)
     */
    public OtherInsuranceEditorForm(boolean displayTenantOwner, TenantOwnerClickHandler tenantOwnerClickHandler) {
        super(InsuranceGeneric.class);
        this.minRequiredLiability = null;
        this.displayTenantOwner = displayTenantOwner;
        this.tenantOwnerClickHandler = tenantOwnerClickHandler;
    }

    public OtherInsuranceEditorForm() {
        this(false, null);
    }

    @Override
    public IsWidget createContent() {
        contentPanel = new BasicFlexFormPanel(); // TODO the only reason its a field is to set a proper caption for the insurance certificate folder
        int row = -1;
        if (displayTenantOwner) {
            CEntityLabel<Customer> comp = new CEntityLabel<Customer>(i18n.tr("Tenant"));
            if (tenantOwnerClickHandler != null) {
                comp.setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        OtherInsuranceEditorForm.this.tenantOwnerClickHandler.onTenantOwnerClicked(getValue().tenant().<Tenant> createIdentityStub());
                    }
                });
            }
            contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().tenant(), comp), "150px").build());
        }

        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().insuranceProvider()), "150px").build());
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().insuranceCertificateNumber()), "150px").build());
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().liabilityCoverage()), "150px").build());
        get(proto().liabilityCoverage()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal> component, BigDecimal value) {
                if (OtherInsuranceEditorForm.this.minRequiredLiability != null && value != null && value.compareTo(minRequiredLiability) < 0) {
                    return new ValidationError(component, i18n.tr("The minimum required liability is {0,number,#,##0.00}", minRequiredLiability));
                }
                return null;
            }
        });
        get(proto().liabilityCoverage()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal> component, BigDecimal value) {
                if (value != null && value.compareTo(BigDecimal.ZERO) <= 0) {
                    return new ValidationError(component, i18n.tr("Please enter a positive value"));
                }
                return null;
            }
        });
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().inceptionDate()), "150px").build());

        get(proto().inceptionDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && value.compareTo(new LogicalDate()) > 0) {
                    return new ValidationError(component, i18n.tr("Please provide a date less than or equal of today"));
                }
                return null;
            }
        });
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().expiryDate()), "150px").build());
        get(proto().expiryDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && value.compareTo(new LogicalDate()) < 0) {
                    return new ValidationError(component, i18n.tr("Please provide a date greater than or equal of today"));
                }
                return null;
            }
        });

        insuranceCeritificatesHeaderRow = ++row;
        contentPanel.setWidget(++row, 0, 2, inject(proto().documents(), new InsuranceCertificateDocumentFolder()));
        scannedInsuranceCertificateNotAvailable = new Label(i18n.tr("N/A"));
        scannedInsuranceCertificateNotAvailable.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        scannedInsuranceCertificateNotAvailable.getElement().getStyle().setProperty("marginLeft", "auto");
        scannedInsuranceCertificateNotAvailable.getElement().getStyle().setProperty("marginRight", "auto");
        scannedInsuranceCertificateNotAvailable.setVisible(false);
        contentPanel.setWidget(++row, 0, 2, scannedInsuranceCertificateNotAvailable);
        return contentPanel;
    }

    public void setMinRequiredLiability(BigDecimal minRequiredLiability) {
        this.minRequiredLiability = minRequiredLiability;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        setViewable(getValue().isPropertyVistaIntegratedProvider().isBooleanTrue()); // TODO this should not be controlled by the form itstelf
        scannedInsuranceCertificateNotAvailable.setVisible(isViewable() & getValue().documents().isEmpty());
        contentPanel.setH2(insuranceCeritificatesHeaderRow, 0, 1,
                isEditable() & !isViewable() ? i18n.tr("Attach Scanned Insurance Certificate") : i18n.tr("Scanned Insurance Certificate"));
    }

    private static class InsuranceCertificateDocumentFolder extends VistaBoxFolder<InsuranceCertificateDocument> {

        public InsuranceCertificateDocumentFolder() {
            super(InsuranceCertificateDocument.class);
            setAddable(false);
            setRemovable(false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
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
            BasicFlexFormPanel panel = new BasicFlexFormPanel();
            panel.setWidget(0, 0, inject(proto().documentPages(), new ApplicationDocumentFileUploaderFolder()));
            addValueValidator(new EditableValueValidator<InsuranceCertificateDocument>() {
                @Override
                public ValidationError isValid(CComponent<InsuranceCertificateDocument> component, InsuranceCertificateDocument value) {
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
}