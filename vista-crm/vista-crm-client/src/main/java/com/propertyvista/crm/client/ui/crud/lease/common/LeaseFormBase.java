/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.editors.dto.bill.BillForm;
import com.propertyvista.common.client.ui.components.tenantinsurance.TenantInsuranceCertificateForm.TenantOwnerClickHandler;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.lease.TenantInsuranceCertificateFolder;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.misc.VistaTODO;

public abstract class LeaseFormBase<DTO extends LeaseDTO> extends CrmEntityForm<DTO> {

    protected static final I18n i18n = I18n.get(LeaseFormBase.class);

    private Tab chargesTab;

    protected LeaseFormBase(Class<DTO> clazz, IFormView<DTO> view) {
        super(clazz, view);
    }

    protected void createCommonContent() {
        selectTab(addTab(createDetailsTab(i18n.tr("Details"))));
        chargesTab = addTab(createChargesTab(i18n.tr("Charges")));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        setTabVisible(chargesTab, getValue().status().getValue().isDraft());

        get(proto().approvalDate()).setVisible(!getValue().approvalDate().isNull());

        get(proto().completion()).setVisible(!getValue().completion().isNull());
        get(proto().moveOutSubmissionDate()).setVisible(!getValue().moveOutSubmissionDate().isNull());

        get(proto().expectedMoveIn()).setVisible(!getValue().expectedMoveIn().isNull());
        get(proto().expectedMoveOut()).setVisible(!getValue().expectedMoveOut().isNull());

        get(proto().actualMoveIn()).setVisible(!getValue().actualMoveIn().isNull());
        get(proto().actualMoveOut()).setVisible(!getValue().actualMoveOut().isNull());

        get(proto().terminationLeaseTo()).setVisible(!getValue().terminationLeaseTo().isNull());

        get(proto().unit()).setNote(getValue().unitMoveOutNote().getValue());
    }

    public void onTenantInsuranceOwnerClicked(Tenant tenantId) {

    }

    private FormFlexPanel createDetailsTab(String title) {
        // Lease details: ---------------------------------------------------------------------------------------------------------------------------
        FormFlexPanel detailsLeft = new FormFlexPanel();

        int detailsRow = -1; // first column:
        detailsLeft.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().leaseId()), 10).build());
        detailsLeft.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().type(), new CEnumLabel()), 15).build());
        detailsLeft.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().paymentFrequency(), new CEnumLabel()), 15).build());
        detailsLeft.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel()), 15).build());
        detailsLeft.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().completion(), new CEnumLabel()), 15).build());
        detailsLeft.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().billingAccount().accountNumber()), 15).build());

        FormFlexPanel detailsRight = new FormFlexPanel();
        detailsRow = -1; // second column:
        detailsRight.setBR(++detailsRow, 0, 1);
        detailsRight.setWidget(++detailsRow, 0,
                new DecoratorBuilder(inject(proto().unit().building(), new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class))),
                        25).build());
        detailsRight.setWidget(++detailsRow, 0,
                new DecoratorBuilder(inject(proto().unit(), new CEntityCrudHyperlink<AptUnit>(AppPlaceEntityMapper.resolvePlace(AptUnit.class))), 25).build());

        detailsRight
                .setWidget(
                        ++detailsRow,
                        0,
                        new DecoratorBuilder(inject(proto().currentTerm(),
                                new CEntityCrudHyperlink<LeaseTerm>(AppPlaceEntityMapper.resolvePlace(LeaseTerm.class))), 25).build());

        FormFlexPanel detailsPanel = new FormFlexPanel();

        detailsPanel.setWidget(0, 0, detailsLeft);
        detailsPanel.setWidget(0, 1, detailsRight);

        detailsPanel.getColumnFormatter().setWidth(0, "40%");
        detailsPanel.getColumnFormatter().setWidth(1, "60%");

        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setWidget(++row, 0, detailsPanel);

        // Lease dates: -----------------------------------------------------------------------------------------------------------------------------
        main.setBR(++row, 0, 1);
        FormFlexPanel datesPanel = new FormFlexPanel();

        int datesRow = -1; // first column:
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().leaseFrom()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().leaseTo()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().expectedMoveIn()), 9).build());
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().expectedMoveOut()), 9).build());

        datesPanel.getColumnFormatter().setWidth(0, "40%");
        datesPanel.getColumnFormatter().setWidth(1, "60%");
        main.setWidget(++row, 0, datesPanel);

        // Move dates: ------------------------------------------------------------------------------------------------------------------------------
        main.setBR(++row, 0, 1);
        datesPanel = new FormFlexPanel();

        datesRow = -1; // first column:
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().terminationLeaseTo()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().moveOutSubmissionDate()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().actualMoveIn()), 9).build());
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().actualMoveOut()), 9).build());

        datesPanel.getColumnFormatter().setWidth(0, "40%");
        datesPanel.getColumnFormatter().setWidth(1, "60%");
        main.setWidget(++row, 0, datesPanel);

        // Other dates: -----------------------------------------------------------------------------------------------------------------------------
        main.setBR(++row, 0, 1);
        datesPanel = new FormFlexPanel();

        datesPanel.setWidget(0, 0, new DecoratorBuilder(inject(proto().creationDate()), 9).build());
        datesPanel.setWidget(0, 1, new DecoratorBuilder(inject(proto().approvalDate()), 9).build());

        datesPanel.getColumnFormatter().setWidth(0, "40%");
        datesPanel.getColumnFormatter().setWidth(1, "60%");
        main.setWidget(++row, 0, datesPanel);

        // Tenants/Guarantors: ----------------------------------------------------------------------------------------------------------------------
        main.setH1(++row, 0, 2, proto().currentTerm().version().tenants().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().currentTerm().version().tenants(), new TenantInLeaseFolder()));

        main.setH1(++row, 0, 2, proto().currentTerm().version().guarantors().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().currentTerm().version().guarantors(), new GuarantorInLeaseFolder()));

        // Products: --------------------------------------------------------------------------------------------------------------------------------
        main.setH1(++row, 0, 2, i18n.tr("Service"));
        main.setWidget(++row, 0, inject(proto().currentTerm().version().leaseProducts().serviceItem(), new BillableItemViewer()));

        main.setH1(++row, 0, 2, proto().currentTerm().version().leaseProducts().featureItems().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().currentTerm().version().leaseProducts().featureItems(), new BillableItemFolder()));

        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            main.setH1(++row, 0, 2, proto().currentTerm().version().leaseProducts().concessions().getMeta().getCaption());
            main.setWidget(++row, 0, inject(proto().currentTerm().version().leaseProducts().concessions(), new ConcessionFolder()));
        }

        main.setH1(++row, 0, 2, i18n.tr("Tenant Insurance"));
        main.setWidget(++row, 0, inject(proto().tenantInsuranceCertificates(), new TenantInsuranceCertificateFolder(new TenantOwnerClickHandler() {
            @Override
            public void onTenantOwnerClicked(Tenant tenantId) {
                LeaseFormBase.this.onTenantInsuranceOwnerClicked(tenantId);
            }
        })));

        return main;
    }

    private FormFlexPanel createChargesTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, inject(proto().billingPreview(), new BillForm(true)));

        return main;
    }
}