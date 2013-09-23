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
package com.propertyvista.portal.web.client.ui.maintenance;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.ApplicationDocumentFileUploaderFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.media.InsuranceCertificateDocument;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.web.client.ui.CPortalEntityWizard;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class MaintenanceRequestWizard extends CPortalEntityWizard<MaintenanceRequestDTO> {

    private final static I18n i18n = I18n.get(MaintenanceRequestWizard.class);

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
    public MaintenanceRequestWizard(MaintenanceRequestWizardView view, boolean displayTenantOwner, TenantOwnerClickHandler tenantOwnerClickHandler) {
        super(MaintenanceRequestDTO.class, view, i18n.tr("Insurance Certificated"), i18n.tr("Submit"), ThemeColor.contrast3);
        this.minRequiredLiability = null;
        this.displayTenantOwner = displayTenantOwner;
        this.tenantOwnerClickHandler = tenantOwnerClickHandler;
    }

    public MaintenanceRequestWizard(MaintenanceRequestWizardView view) {
        this(view, false, null);
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
                        //TODO     UploadSertificateWizard.this.tenantOwnerClickHandler.onTenantOwnerClicked(getValue().tenant().<Tenant> createIdentityStub());
                    }
                });
            }
            //TODO    contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().tenant(), comp), "150px").build());
        }

        insuranceCeritificatesHeaderRow = ++row;
        //TODO  contentPanel.setWidget(++row, 0, 2, inject(proto().documents(), new InsuranceCertificateDocumentFolder()));
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
        //TODO      setViewable(getValue().isPropertyVistaIntegratedProvider().isBooleanTrue()); // TODO this should not be controlled by the form itstelf
        //TODO    scannedInsuranceCertificateNotAvailable.setVisible(isViewable() & getValue().documents().isEmpty());
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

    private static class InsuranceCertificateDocumentEditor extends CEntityForm<InsuranceCertificateDocument> {

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