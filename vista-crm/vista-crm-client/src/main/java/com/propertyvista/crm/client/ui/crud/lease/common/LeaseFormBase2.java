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
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.editors.dto.bill.BillForm;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.LeaseDTO2;
import com.propertyvista.misc.VistaTODO;

public abstract class LeaseFormBase2<DTO extends LeaseDTO2> extends CrmEntityForm<DTO> {

    protected static final I18n i18n = I18n.get(LeaseFormBase2.class);

    private Tab chargesTab;

    protected LeaseFormBase2(Class<DTO> clazz) {
        super(clazz, true);
    }

    @SuppressWarnings("unchecked")
    protected void createCommonContent() {

        Tab tab = addTab(createDetailsTab(i18n.tr("Details")));
        selectTab(tab);

        addTab(createTenantsTab(i18n.tr("Tenants")));
        addTab(createGuarantorsTab(i18n.tr("Guarantors")));
        addTab(createProductsTab(i18n.tr("Products")));
        addTab(((LeaseViewerViewBase2<DTO>) getParentView()).getDepositListerView().asWidget(), i18n.tr("Deposits"));

        chargesTab = addTab(createChargesTab(i18n.tr("Charges")));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        setTabVisible(chargesTab, getValue().status().getValue().isDraft());

        get(proto().completion()).setVisible(!getValue().completion().isNull());

        get(proto().moveOutNotice()).setVisible(!getValue().moveOutNotice().isNull());
        get(proto().expectedMoveOut()).setVisible(!getValue().expectedMoveOut().isNull());

        get(proto().approvalDate()).setVisible(!getValue().approvalDate().isNull());

        get(proto().actualLeaseTo()).setVisible(!getValue().actualLeaseTo().isNull());
        get(proto().actualMoveIn()).setVisible(!getValue().actualMoveIn().isNull());
        get(proto().actualMoveOut()).setVisible(!getValue().actualMoveOut().isNull());
    }

    private FormFlexPanel createDetailsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseId()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().paymentFrequency(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().completion(), new CEnumLabel())).build());
//        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingAccount().accountNumber())).build());

        // Lease dates:
        main.setBR(++row, 0, 1);
        FormFlexPanel datesPanel = new FormFlexPanel();

        int datesRow = -1; // first column:
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().leaseFrom()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().leaseTo()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setBR(++datesRow, 1, 1);
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().actualLeaseTo()), 9).build());

        datesPanel.getColumnFormatter().setWidth(0, "40%");
        datesPanel.getColumnFormatter().setWidth(1, "60%");
        main.setWidget(++row, 0, datesPanel);

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().unit().building(), new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class))),
                        20).build());
        main.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().unit(), new CEntityCrudHyperlink<AptUnit>(AppPlaceEntityMapper.resolvePlace(AptUnit.class))), 20).build());

        // Move dates:
        main.setBR(++row, 0, 1);
        datesPanel = new FormFlexPanel();

        datesRow = -1; // first column:
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().expectedMoveIn()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().moveOutNotice()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().expectedMoveOut()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().actualMoveIn()), 9).build());
        datesPanel.setBR(++datesRow, 1, 1);
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().actualMoveOut()), 9).build());

        get(proto().moveOutNotice()).setViewable(true);
        get(proto().expectedMoveOut()).setViewable(true);

        datesPanel.getColumnFormatter().setWidth(0, "40%");
        datesPanel.getColumnFormatter().setWidth(1, "60%");
        main.setWidget(++row, 0, datesPanel);

        // other dates:
        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creationDate()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().approvalDate()), 9).build());

        get(proto().creationDate()).setViewable(true);
        get(proto().approvalDate()).setViewable(true);

        return main;
    }

    private FormFlexPanel createTenantsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, inject(proto().currentLeaseTerm().version().tenants(), new TenantInLeaseFolder2()));

        return main;
    }

    private FormFlexPanel createGuarantorsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, inject(proto().currentLeaseTerm().version().guarantors(), new GuarantorInLeaseFolder2()));

        return main;
    }

    private FormFlexPanel createProductsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Service"));
        main.setWidget(++row, 0, inject(proto().currentLeaseTerm().version().leaseProducts().serviceItem(), new BillableItemViewer()));

        main.setH1(++row, 0, 2, proto().currentLeaseTerm().version().leaseProducts().featureItems().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().currentLeaseTerm().version().leaseProducts().featureItems(), new BillableItemFolder2()));

        if (!VistaTODO.removedForProduction) {
            main.setH1(++row, 0, 2, proto().currentLeaseTerm().version().leaseProducts().concessions().getMeta().getCaption());
            main.setWidget(++row, 0, inject(proto().currentLeaseTerm().version().leaseProducts().concessions(), new ConcessionFolder2()));
        }

        return main;
    }

    private FormFlexPanel createChargesTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, inject(proto().billingPreview(), new BillForm(true)));

        return main;
    }
}