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

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.domain.DemoData;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.PmcDTO;

public class PmcFormNewItem extends OperationsEntityForm<PmcDTO> {

    private static final I18n i18n = I18n.get(PmcFormNewItem.class);

    public PmcFormNewItem(IPrimeFormView<PmcDTO, ?> view) {
        super(PmcDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().dnsName()).decorate();
        formPanel.append(Location.Left, proto().onboardingUser().firstName()).decorate();
        formPanel.append(Location.Left, proto().onboardingUser().lastName()).decorate();
        formPanel.append(Location.Left, proto().onboardingUser().email()).decorate();
        formPanel.append(Location.Left, proto().onboardingUser().password()).decorate();

        formPanel.h1(proto().features().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().features(), new PmcFeaturesForm());

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
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