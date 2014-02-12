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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.decorators.EntityContainerCollapsableDecorator;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.editors.dto.bill.BillForm;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.activity.crud.lease.TenantInsuranceCertificateForm.TenantOwnerClickHandler;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.lease.TenantInsuranceCertificateFolder;
import com.propertyvista.crm.client.ui.crud.lease.common.term.GuarantorInLeaseFolder;
import com.propertyvista.crm.client.ui.crud.lease.common.term.TenantInLeaseFolder;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public abstract class LeaseFormBase<DTO extends LeaseDTO> extends CrmEntityForm<DTO> {

    protected static final I18n i18n = I18n.get(LeaseFormBase.class);

    protected Tab chargesTab;

    protected LeaseFormBase(Class<DTO> clazz, IForm<DTO> view) {
        super(clazz, view);
    }

    protected void createCommonContent() {
        selectTab(addTab(createDetailsTab(i18n.tr("Details"))));
    }

    @Override
    protected DTO preprocessValue(DTO value, boolean fireEvent, boolean populate) {
        CComponent<?> comp = get(proto().currentTerm().version().tenants());
        ((TenantInLeaseFolder) comp).setPadEditable(!value.status().getValue().isFormer());

        return super.preprocessValue(value, fireEvent, populate);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (chargesTab != null) {
            setTabVisible(chargesTab, getValue().status().getValue().isDraft() && !getValue().billingPreview().isNull());
        }

        get(proto().leaseId()).setVisible(false);
        get(proto().leaseApplication().applicationId()).setVisible(false);

        get(proto().carryforwardBalance()).setVisible(!getValue().carryforwardBalance().isNull());

        get(proto().carryforwardBalance()).setVisible(!getValue().carryforwardBalance().isNull());

        get(proto().approvalDate()).setVisible(!getValue().approvalDate().isNull());

        get(proto().completion()).setVisible(!getValue().completion().isNull());
        get(proto().moveOutSubmissionDate()).setVisible(!getValue().moveOutSubmissionDate().isNull());

        get(proto().expectedMoveIn()).setVisible(!getValue().expectedMoveIn().isNull());
        get(proto().expectedMoveOut()).setVisible(!getValue().expectedMoveOut().isNull());

        get(proto().actualMoveIn()).setVisible(!getValue().actualMoveIn().isNull());
        get(proto().actualMoveOut()).setVisible(!getValue().actualMoveOut().isNull());

        get(proto().terminationLeaseTo()).setVisible(!getValue().terminationLeaseTo().isNull());

        get(proto().unit()).setNote(getValue().unitMoveOutNote().getValue());

        if (VistaFeatures.instance().yardiIntegration()) {
            get(proto().terminationLeaseTo()).setVisible(false);
            get(proto().moveOutSubmissionDate()).setVisible(false);

            get(proto().approvalDate()).setVisible(false);
            get(proto().creationDate()).setVisible(false);
        }

        get(proto().currentLegalStatus()).setVisible(!(getValue().currentLegalStatus().isNull()));
    }

    public void onTenantInsuranceOwnerClicked(Tenant tenantId) {

    }

    private TwoColumnFlexFormPanel createDetailsTab(String title) {
        // Lease details: ---------------------------------------------------------------------------------------------------------------------------
        TwoColumnFlexFormPanel flexPanel = new TwoColumnFlexFormPanel(title);

        int leftRow = -1;
        int rightRow = -1;

        flexPanel.setWidget(++leftRow, 0,
                new FormDecoratorBuilder(inject(proto().unit(), new CEntityCrudHyperlink<AptUnit>(AppPlaceEntityMapper.resolvePlace(AptUnit.class))), 25)
                        .build());

        flexPanel.setWidget(
                ++leftRow,
                0,
                new FormDecoratorBuilder(inject(proto().unit().floorplan(),
                        new CEntityCrudHyperlink<Floorplan>(AppPlaceEntityMapper.resolvePlace(Floorplan.class))), 25).build());

        flexPanel.setWidget(
                ++leftRow,
                0,
                new FormDecoratorBuilder(inject(proto().unit().building(),
                        new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class))), 25).build());

        flexPanel.setWidget(++leftRow, 0,
                new FormDecoratorBuilder(
                        inject(proto().currentTerm(), new CEntityCrudHyperlink<LeaseTerm>(AppPlaceEntityMapper.resolvePlace(LeaseTerm.class))), 25).build());

        flexPanel.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().currentLegalStatus())).build());

        flexPanel.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().carryforwardBalance()), 10).build());

        flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().leaseId()), 10).build());
        flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().leaseApplication().applicationId()), 10).build());
        flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().type(), new CEnumLabel()), 15).build());
        flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().billingAccount().accountNumber()), 15).build());
        flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().status(), new CEnumLabel()), 15).build());
        flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().completion(), new CEnumLabel()), 15).build());
        flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().billingAccount().billingPeriod(), new CEnumLabel()), 15).build());
        flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().billingAccount().paymentAccepted(), new CEnumLabel()), 15).build());

        leftRow = rightRow = Math.max(leftRow, rightRow);

        // Lease dates: -----------------------------------------------------------------------------------------------------------------------------
        flexPanel.setHR(++leftRow, 0, 2);
        TwoColumnFlexFormPanel datesPanel = new TwoColumnFlexFormPanel();

        int datesRow = -1; // first column:
        datesPanel.setWidget(++datesRow, 0, new FormDecoratorBuilder(inject(proto().leaseFrom()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new FormDecoratorBuilder(inject(proto().leaseTo()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setWidget(++datesRow, 1, new FormDecoratorBuilder(inject(proto().expectedMoveIn()), 9).build());
        datesPanel.setWidget(++datesRow, 1, new FormDecoratorBuilder(inject(proto().expectedMoveOut()), 9).build());

        flexPanel.setWidget(++leftRow, 0, 2, datesPanel);

        // Move dates: ------------------------------------------------------------------------------------------------------------------------------
        datesPanel = new TwoColumnFlexFormPanel();

        datesRow = -1; // first column:
        datesPanel.setWidget(++datesRow, 0, new FormDecoratorBuilder(inject(proto().terminationLeaseTo()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new FormDecoratorBuilder(inject(proto().moveOutSubmissionDate()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setWidget(++datesRow, 1, new FormDecoratorBuilder(inject(proto().actualMoveIn()), 9).build());
        datesPanel.setWidget(++datesRow, 1, new FormDecoratorBuilder(inject(proto().actualMoveOut()), 9).build());

        flexPanel.setWidget(++leftRow, 0, 2, datesPanel);

        // Other dates: -----------------------------------------------------------------------------------------------------------------------------
        flexPanel.setBR(++leftRow, 0, 2);

        datesPanel = new TwoColumnFlexFormPanel();

        datesPanel.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().creationDate()), 9).build());
        datesPanel.setWidget(0, 1, new FormDecoratorBuilder(inject(proto().approvalDate()), 9).build());

        flexPanel.setWidget(++leftRow, 0, 2, datesPanel);

        // Products: --------------------------------------------------------------------------------------------------------------------------------
        flexPanel.setH1(++leftRow, 0, 2, proto().currentTerm().version().leaseProducts().getMeta().getCaption());
        flexPanel.setWidget(++leftRow, 0, 2, inject(proto().currentTerm().version().leaseProducts().serviceItem(), new BillableItemViewer() {
            @Override
            protected com.pyx4j.forms.client.ui.decorators.IDecorator<?> createDecorator() {
                return new EntityContainerCollapsableDecorator<BillableItem>(VistaImages.INSTANCE);
            };
        }));

        flexPanel.setH2(++leftRow, 0, 2, proto().currentTerm().version().leaseProducts().featureItems().getMeta().getCaption());
        flexPanel.setWidget(++leftRow, 0, 2, inject(proto().currentTerm().version().leaseProducts().featureItems(), new BillableItemFolder()));

        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            flexPanel.setH2(++leftRow, 0, 2, proto().currentTerm().version().leaseProducts().concessions().getMeta().getCaption());
            flexPanel.setWidget(++leftRow, 0, 2, inject(proto().currentTerm().version().leaseProducts().concessions(), new ConcessionFolder()));
        }

        // Utilities: -----------------------------------------------------------------------------------------------------------
        flexPanel.setH1(++leftRow, 0, 2, proto().currentTerm().version().utilities().getMeta().getCaption());
        flexPanel.setWidget(++leftRow, 0, 2, inject(proto().currentTerm().version().utilities(), new BuildingUtilityFolder()));

        // Tenants/Guarantors: ----------------------------------------------------------------------------------------------------------------------
        flexPanel.setH1(++leftRow, 0, 2, proto().currentTerm().version().tenants().getMeta().getCaption());
        flexPanel.setWidget(++leftRow, 0, 2, inject(proto().currentTerm().version().tenants(), new TenantInLeaseFolder(this)));

        flexPanel.setH1(++leftRow, 0, 2, proto().currentTerm().version().guarantors().getMeta().getCaption());
        flexPanel.setWidget(++leftRow, 0, 2, inject(proto().currentTerm().version().guarantors(), new GuarantorInLeaseFolder(this)));

        // Insurance: --------------------------------------------------------------------------------------------------------------------------------
        flexPanel.setH1(++leftRow, 0, 2, i18n.tr("Tenant Insurance"));
        flexPanel.setWidget(++leftRow, 0, 2, inject(proto().tenantInsuranceCertificates(), new TenantInsuranceCertificateFolder(new TenantOwnerClickHandler() {
            @Override
            public void onTenantOwnerClicked(Tenant tenantId) {
                LeaseFormBase.this.onTenantInsuranceOwnerClicked(tenantId);
            }
        })));

        return flexPanel;
    }

    protected String getChargesTabTitle() {
        return i18n.tr("Charges");
    }

    protected TwoColumnFlexFormPanel createChargesTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(getChargesTabTitle());

        main.setWidget(0, 0, inject(proto().billingPreview(), new BillForm(true)));

        return main;
    }

    private class BuildingUtilityFolder extends VistaTableFolder<BuildingUtility> {

        public BuildingUtilityFolder() {
            super(BuildingUtility.class, false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            List<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
            columns.add(new EntityFolderColumnDescriptor(proto().name(), "15em"));
            columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
            return columns;
        }
    }
}