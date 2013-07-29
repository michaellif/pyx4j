/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.PmcDTO;
import com.propertyvista.operations.rpc.PmcMerchantAccountDTO;

public class MerchantAccountForm extends OperationsEntityForm<PmcMerchantAccountDTO> {

    private static final I18n i18n = I18n.get(MerchantAccountForm.class);

    public static class AssignedBuildingsFolder extends VistaTableFolder<Building> {

        private static List<EntityFolderColumnDescriptor> COLUMNS;
        static {
            Building proto = EntityFactory.getEntityPrototype(Building.class);
            COLUMNS = Arrays.asList(//@formatter:off
                    new EntityFolderColumnDescriptor(proto.propertyCode(), "10em")
            );//@formatter:on
        }

        public AssignedBuildingsFolder() {
            super(Building.class);
            setViewable(true);
            setAddable(false);
            setEditable(false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return COLUMNS;
        }

    }

    public MerchantAccountForm(IForm<PmcMerchantAccountDTO> view) {
        super(PmcMerchantAccountDTO.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;
        content.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().pmc(), new CEntityCrudHyperlink<Pmc>(AppPlaceEntityMapper.resolvePlace(PmcDTO.class))), 10).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().merchantAccount().status()), 25).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().merchantAccount().paymentsStatus()), 25).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().merchantAccount().invalid()), 25).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().merchantTerminalId()), 25).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().merchantAccount().bankId()), 5).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().merchantAccount().branchTransitNumber()), 5).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().merchantAccount().accountNumber()), 15).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().merchantAccount().chargeDescription()), 30).build());

        content.setH2(++row, 0, 1, i18n.tr("Assigned Buildings"));
        content.setWidget(++row, 0, inject(proto().assignedBuildings(), new AssignedBuildingsFolder()));
        selectTab(addTab(content));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().merchantAccount().paymentsStatus()).setVisible(!getValue().id().isNull());
    }

    @Override
    public void addValidations() {
        super.addValidations();
        if (ApplicationMode.isDevelopment()) {
            this.addDevShortcutHandler(new DevShortcutHandler() {
                @Override
                public void onDevShortcut(DevShortcutEvent event) {
                    if (event.getKeyCode() == 'Q') {
                        event.consume();
                        devGenerateAccount();
                    }
                }

            });
        }
    }

    private void devGenerateAccount() {
        get(proto().merchantAccount().status()).setValue(MerchantAccountActivationStatus.Active);
        get(proto().merchantTerminalId()).setValue("T" + System.currentTimeMillis() % 1000000);
        get(proto().merchantAccount().bankId()).setValue("123");
        get(proto().merchantAccount().branchTransitNumber()).setValue("12345");
        get(proto().merchantAccount().accountNumber()).setValue(String.valueOf(System.currentTimeMillis() % 10000000));
    }

}
