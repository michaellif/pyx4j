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
package com.propertyvista.operations.client.ui.crud.pmc.mercantaccount;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.validators.EcheckAccountNumberStringValidator;
import com.propertyvista.common.client.ui.validators.EcheckBankIdValidator;
import com.propertyvista.common.client.ui.validators.EcheckBranchTransitValidator;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.VistaOperationsBehavior;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.dto.PmcMerchantAccountDTO;

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

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, inject(proto().merchantAccount().created(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().merchantAccount().updated(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0,
                inject(proto().pmc(), new CEntityCrudHyperlink<Pmc>(AppPlaceEntityMapper.resolvePlace(PmcDTO.class)), new FormDecoratorBuilder().build()));
        content.setWidget(row, 1, inject(proto().merchantAccount().status(), new FormDecoratorBuilder().build()));

        content.setWidget(++row, 0, inject(proto().merchantTerminalId(), new FormDecoratorBuilder().build()));
        content.setWidget(row, 1, inject(proto().merchantAccount().paymentsStatus(), new FormDecoratorBuilder().build()));

        content.setWidget(++row, 0, inject(proto().merchantTerminalIdConvenienceFee(), new FormDecoratorBuilder().build()));

        content.setWidget(++row, 0, inject(proto().merchantAccount().bankId(), new FormDecoratorBuilder(5).build()));
        content.setWidget(row, 1, inject(proto().merchantAccount().invalid(), new FormDecoratorBuilder().build()));

        content.setWidget(++row, 0, inject(proto().merchantAccount().branchTransitNumber(), new FormDecoratorBuilder(5).build()));
        content.setWidget(row, 1, inject(proto().merchantAccount().chargeDescription(), new FormDecoratorBuilder().build()));

        content.setWidget(++row, 0, inject(proto().merchantAccount().accountNumber(), new FormDecoratorBuilder(15).build()));
        content.setWidget(row, 1, inject(proto().merchantAccount().accountName(), new FormDecoratorBuilder().build()));

        content.setWidget(++row, 0, 2, inject(proto().merchantAccount().operationsNotes(), new FormDecoratorBuilder(15, true).build()));

        content.setH2(++row, 0, 2, i18n.tr("Payment Types Activation"));

        content.setWidget(++row, 0, inject(proto().merchantAccount().setup().acceptedEcheck(), new FormDecoratorBuilder(5).build()));
        content.setWidget(row, 1, inject(proto().merchantAccount().setup().acceptedDirectBanking(), new FormDecoratorBuilder().build()));

        content.setWidget(++row, 0, inject(proto().merchantAccount().setup().acceptedCreditCard(), new FormDecoratorBuilder(5).build()));
        content.setWidget(row, 1, inject(proto().merchantAccount().setup().acceptedCreditCardConvenienceFee(), new FormDecoratorBuilder().build()));

        content.setWidget(++row, 0, inject(proto().merchantAccount().setup().acceptedInterac(), new FormDecoratorBuilder(5).build()));

        content.setH2(++row, 0, 2, i18n.tr("Assigned Buildings"));
        content.setWidget(++row, 0, 2, inject(proto().assignedBuildings(), new AssignedBuildingsFolder()));

        setTabBarVisible(false);
        selectTab(addTab(content));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().merchantAccount().paymentsStatus()).setVisible(!getValue().id().isNull());
        updateConvenienceFeeValability();

        get(proto().merchantAccount().bankId()).setEditable(SecurityController.checkAnyBehavior(VistaOperationsBehavior.SystemAdmin));
        get(proto().merchantAccount().branchTransitNumber()).setEditable(SecurityController.checkAnyBehavior(VistaOperationsBehavior.SystemAdmin));
        get(proto().merchantAccount().accountNumber()).setEditable(SecurityController.checkAnyBehavior(VistaOperationsBehavior.SystemAdmin));
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().merchantAccount().accountNumber()).addComponentValidator(new EcheckAccountNumberStringValidator());
        get(proto().merchantAccount().branchTransitNumber()).addComponentValidator(new EcheckBranchTransitValidator());
        get(proto().merchantAccount().bankId()).addComponentValidator(new EcheckBankIdValidator());

        get(proto().merchantTerminalIdConvenienceFee()).addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                updateConvenienceFeeValability();
            }
        });

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

    private void updateConvenienceFeeValability() {
        get(proto().merchantAccount().setup().acceptedCreditCardConvenienceFee()).setEnabled(!getValue().merchantTerminalIdConvenienceFee().isNull());
    }

    private void devGenerateAccount() {
        get(proto().merchantAccount().status()).setValue(MerchantAccountActivationStatus.Active);
        get(proto().merchantTerminalId()).setValue("T" + System.currentTimeMillis() % 1000000);
        get(proto().merchantAccount().bankId()).setValue("123");
        get(proto().merchantAccount().branchTransitNumber()).setValue("12345");
        get(proto().merchantAccount().accountNumber()).setValue(String.valueOf(System.currentTimeMillis() % 10000000));
    }

}
