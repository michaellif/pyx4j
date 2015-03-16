/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-20
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.decorators.EntityContainerCollapsableDecorator;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.editors.dto.bill.BillForm;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.lease.common.term.GuarantorInLeaseFolder;
import com.propertyvista.crm.client.ui.crud.lease.common.term.TenantInLeaseFolder;
import com.propertyvista.crm.client.ui.crud.lease.insurance.TenantInsuranceCertificateFolder;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public abstract class LeaseFormBase<DTO extends LeaseDTO> extends CrmEntityForm<DTO> {

    protected static final I18n i18n = I18n.get(LeaseFormBase.class);

    private TenantInLeaseFolder tenantInLeaseFolder;

    protected LeaseFormBase(Class<DTO> clazz, IPrimeFormView<DTO, ?> view) {
        super(clazz, view);
        setEditable(false);
    }

    protected void createCommonContent() {
        selectTab(addTab(createDetailsTab(), i18n.tr("Details")));
    }

    @Override
    protected DTO preprocessValue(DTO value, boolean fireEvent, boolean populate) {
        CComponent<?, ?, ?, ?> comp = get(proto().currentTerm().version().tenants());
        ((TenantInLeaseFolder) comp).setPadEditable(!value.status().getValue().isFormer());

        return super.preprocessValue(value, fireEvent, populate);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().leaseId()).setVisible(false);
        get(proto().leaseApplication().applicationId()).setVisible(false);
        get(proto().leaseApplication().yardiApplicationId()).setVisible(false);

        get(proto().isUnitReserved()).setVisible(
                !getValue().unit().isNull() && getValue().status().getValue().isDraft() && getValue().status().getValue() != Lease.Status.ExistingLease);
        get(proto().reservedUntil()).setVisible(!getValue().reservedUntil().isNull());

        get(proto().carryforwardBalance()).setVisible(!getValue().carryforwardBalance().isNull());

        get(proto().approvalDate()).setVisible(!getValue().approvalDate().isNull());

        get(proto().completion()).setVisible(!getValue().completion().isNull());
        get(proto().moveOutSubmissionDate()).setVisible(!getValue().moveOutSubmissionDate().isNull());

        get(proto().expectedMoveIn()).setVisible(!getValue().expectedMoveIn().isNull());
        get(proto().expectedMoveOut()).setVisible(!getValue().expectedMoveOut().isNull());

        get(proto().actualMoveIn()).setVisible(!getValue().actualMoveIn().isNull());
        get(proto().actualMoveOut()).setVisible(!getValue().actualMoveOut().isNull());

        get(proto().terminationLeaseTo()).setVisible(!getValue().terminationLeaseTo().isNull());

        get(proto().unit()).setNote(getValue().unitMoveOutNote().getValue(), NoteStyle.Warn);

        if (VistaFeatures.instance().yardiIntegration()) {
            get(proto().terminationLeaseTo()).setVisible(false);
            get(proto().moveOutSubmissionDate()).setVisible(false);

            get(proto().approvalDate()).setVisible(false);
            get(proto().creationDate()).setVisible(false);
        }

        get(proto().currentLegalStatus()).setVisible(!(getValue().currentLegalStatus().isNull()));

        tenantInLeaseFolder.setNextAutopayApplicabilityMessage(getValue().nextAutopayApplicabilityMessage().getValue());
    }

    @Override
    public void onReset() {
        super.onReset();
        // disable any Notes
        get(proto().unit()).setNote(null);
    }

    protected IsWidget createDetailsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().unit().building(), new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class)))
                .decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().unit(), new CEntityCrudHyperlink<AptUnit>(AppPlaceEntityMapper.resolvePlace(AptUnit.class))).decorate()
                .componentWidth(200);
        formPanel.append(Location.Left, proto().unit().floorplan(), new CEntityCrudHyperlink<Floorplan>(AppPlaceEntityMapper.resolvePlace(Floorplan.class)))
                .decorate().componentWidth(200);

        formPanel.append(Location.Left, proto().currentTerm(), new CEntityCrudHyperlink<LeaseTerm>(AppPlaceEntityMapper.resolvePlace(LeaseTerm.class)))
                .decorate().componentWidth(200);

        formPanel.append(Location.Left, proto().isUnitReserved()).decorate().componentWidth(80);
        formPanel.append(Location.Left, proto().reservedUntil()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().currentLegalStatus()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().carryforwardBalance()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().leaseId()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().leaseApplication().applicationId()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().leaseApplication().yardiApplicationId()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().type(), new CEnumLabel()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().billingAccount().accountNumber()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().status(), new CEnumLabel()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().completion(), new CEnumLabel()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().billingAccount().billingPeriod(), new CEnumLabel()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().billingAccount().paymentAccepted(), new CEnumLabel()).decorate().componentWidth(180);

        // Lease dates: -----------------------------------------------------------------------------------------------------------------------------
        formPanel.hr();

        formPanel.append(Location.Left, proto().leaseFrom()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().leaseTo()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().expectedMoveIn()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().expectedMoveOut()).decorate().componentWidth(120);

        // Move dates: ------------------------------------------------------------------------------------------------------------------------------

        formPanel.append(Location.Left, proto().terminationLeaseTo()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().moveOutSubmissionDate()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().actualMoveIn()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().actualMoveOut()).decorate().componentWidth(120);

        // Other dates: -----------------------------------------------------------------------------------------------------------------------------
        formPanel.br();

        formPanel.append(Location.Left, proto().creationDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().approvalDate()).decorate().componentWidth(120);

        // Products: --------------------------------------------------------------------------------------------------------------------------------
        formPanel.h1(proto().currentTerm().version().leaseProducts().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().currentTerm().version().leaseProducts().serviceItem(), new BillableItemViewer() {
            @Override
            protected EntityContainerCollapsableDecorator<BillableItem> createDecorator() {
                return new EntityContainerCollapsableDecorator<BillableItem>(VistaImages.INSTANCE);
            };
        });

        formPanel.h1(proto().currentTerm().version().leaseProducts().featureItems().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().currentTerm().version().leaseProducts().featureItems(), new BillableItemFolder());

        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            formPanel.h1(proto().currentTerm().version().leaseProducts().concessions().getMeta().getCaption());
            formPanel.append(Location.Dual, proto().currentTerm().version().leaseProducts().concessions(), new ConcessionFolder());
        }

        // Utilities: -----------------------------------------------------------------------------------------------------------
        formPanel.h1(proto().currentTerm().version().utilities().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().currentTerm().version().utilities(), new BuildingUtilityFolder());

        // Tenants/Guarantors: --------------------------------------------------------------------------------------------------
        formPanel.h1(proto().currentTerm().version().tenants().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().currentTerm().version().tenants(), tenantInLeaseFolder = new TenantInLeaseFolder(this));

        formPanel.h1(proto().currentTerm().version().guarantors().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().currentTerm().version().guarantors(), new GuarantorInLeaseFolder(this));

        // Insurance: -----------------------------------------------------------------------------------------------------------
        formPanel.h1(i18n.tr("Tenant Insurance"));
        formPanel.append(Location.Dual, proto().tenantInsuranceCertificates(), new TenantInsuranceCertificateFolder(true));

        // Misc: ----------------------------------------------------------------------------------------------------------------
        formPanel.h1(i18n.tr("Miscellaneous"));
        formPanel.append(Location.Dual, proto().leaseApplication().referenceSource()).decorate().componentWidth(180);

        return formPanel;
    }

    protected IsWidget createChargesTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().billingPreview(), new BillForm(true));
        return formPanel;
    }

    private class BuildingUtilityFolder extends VistaTableFolder<BuildingUtility> {

        public BuildingUtilityFolder() {
            super(BuildingUtility.class, false);
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            List<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
            columns.add(new FolderColumnDescriptor(proto().type(), "15em"));
            columns.add(new FolderColumnDescriptor(proto().name(), "15em"));
            columns.add(new FolderColumnDescriptor(proto().description(), "25em"));
            return columns;
        }
    }
}