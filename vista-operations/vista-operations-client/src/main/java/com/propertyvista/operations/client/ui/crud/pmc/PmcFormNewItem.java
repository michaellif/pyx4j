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
package com.propertyvista.operations.client.ui.crud.pmc;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.domain.DemoData;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.PmcDTO;

public class PmcFormNewItem extends OperationsEntityForm<PmcDTO> {

    private static final I18n i18n = I18n.get(PmcFormNewItem.class);

    public PmcFormNewItem(IForm<PmcDTO> view) {
        super(PmcDTO.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dnsName()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().onboardingUser().firstName()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().onboardingUser().lastName()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().onboardingUser().email()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().onboardingUser().password()), 15).build());

        content.setH1(++row, 0, 2, proto().features().getMeta().getCaption());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().countryOfOperation()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().occupancyModel()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().productCatalog()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().leases()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().onlineApplication()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().defaultProductCatalog()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().yardiIntegration()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().yardiMaintenance()), 5).build());

        final CComponent<Boolean> yardiIntegrationSwitch = get(proto().features().yardiIntegration());
        final CComponent<Boolean> yardiMaintenanceSwitch = get(proto().features().yardiMaintenance());
        yardiIntegrationSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                yardiMaintenanceSwitch.setEnabled(Boolean.TRUE.equals(event.getValue()));
            }
        });

        selectTab(addTab(content));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().features().yardiMaintenance()).setEnabled(getValue() != null && getValue().features().yardiIntegration().isBooleanTrue());
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
        get(proto().name()).setValue("P" + System.currentTimeMillis() % 1000000);
        get(proto().dnsName()).setValue(get(proto().name()).getValue());

        get(proto().onboardingUser().firstName()).setValue("F" + get(proto().name()).getValue());
        get(proto().onboardingUser().lastName()).setValue("L" + get(proto().name()).getValue());

        get(proto().onboardingUser().email()).setValue(get(proto().name()).getValue() + DemoData.USERS_DOMAIN);
        get(proto().onboardingUser().password()).setValue(get(proto().onboardingUser().email()).getValue());
    }
}