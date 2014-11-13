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
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

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

        private static List<FolderColumnDescriptor> COLUMNS;
        static {
            Building proto = EntityFactory.getEntityPrototype(Building.class);
            COLUMNS = Arrays.asList(//@formatter:off
                    new FolderColumnDescriptor(proto.propertyCode(), "10em")
            );//@formatter:on
        }

        public AssignedBuildingsFolder() {
            super(Building.class);
            setViewable(true);
            setAddable(false);
            setEditable(false);
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            return COLUMNS;
        }

    }

    public MerchantAccountForm(IFormView<PmcMerchantAccountDTO, ?> view) {
        super(PmcMerchantAccountDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().merchantAccount().created()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().merchantAccount().updated()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().pmc(), new CEntityCrudHyperlink<Pmc>(AppPlaceEntityMapper.resolvePlace(PmcDTO.class))).decorate();
        formPanel.append(Location.Right, proto().merchantAccount().status()).decorate();

        formPanel.append(Location.Left, proto().terminalId()).decorate();
        formPanel.append(Location.Right, proto().merchantAccount().paymentsStatus()).decorate();

        formPanel.append(Location.Left, proto().merchantTerminalIdConvenienceFee()).decorate();

        formPanel.append(Location.Left, proto().merchantAccount().bankId()).decorate().componentWidth(90);
        formPanel.append(Location.Right, proto().merchantAccount().invalid()).decorate();

        formPanel.append(Location.Left, proto().merchantAccount().branchTransitNumber()).decorate().componentWidth(90);
        formPanel.append(Location.Right, proto().merchantAccount().chargeDescription()).decorate();

        formPanel.append(Location.Left, proto().merchantAccount().accountNumber()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().merchantAccount().accountName()).decorate();

        formPanel.append(Location.Dual, proto().merchantAccount().operationsNotes()).decorate();

        formPanel.h2(i18n.tr("Payment Types Activation"));

        formPanel.append(Location.Left, proto().merchantAccount().setup().acceptedEcheck()).decorate().componentWidth(90);
        formPanel.append(Location.Right, proto().merchantAccount().setup().acceptedDirectBanking()).decorate();

        formPanel.append(Location.Left, proto().merchantAccount().setup().acceptedCreditCard()).decorate().componentWidth(90);
        formPanel.append(Location.Right, proto().merchantAccount().setup().acceptedCreditCardConvenienceFee()).decorate();

        formPanel.append(Location.Left, proto().merchantAccount().setup().acceptedInterac()).decorate().componentWidth(90);
        formPanel.append(Location.Right, proto().merchantAccount().setup().acceptedCreditCardVisaDebit()).decorate();

        formPanel.h2(i18n.tr("Assigned Buildings"));
        formPanel.append(Location.Dual, proto().assignedBuildings(), new AssignedBuildingsFolder());

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("General")));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().merchantAccount().paymentsStatus()).setVisible(!getValue().id().isNull());
        updateConvenienceFeeValability();

        get(proto().merchantAccount().bankId()).setEditable(SecurityController.check(VistaOperationsBehavior.SystemAdmin));
        get(proto().merchantAccount().branchTransitNumber()).setEditable(SecurityController.check(VistaOperationsBehavior.SystemAdmin));
        get(proto().merchantAccount().accountNumber()).setEditable(SecurityController.check(VistaOperationsBehavior.SystemAdmin));
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
        get(proto().terminalId()).setValue("T" + System.currentTimeMillis() % 1000000);
        get(proto().merchantAccount().bankId()).setValue("123");
        get(proto().merchantAccount().branchTransitNumber()).setValue("12345");
        get(proto().merchantAccount().accountNumber()).setValue(String.valueOf(System.currentTimeMillis() % 10000000));
    }

}
