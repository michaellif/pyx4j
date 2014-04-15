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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.DemoData;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.PmcDTO;

public class PmcFormNewItem extends OperationsEntityForm<PmcDTO> {

    private static final I18n i18n = I18n.get(PmcFormNewItem.class);

    public PmcFormNewItem(IForm<PmcDTO> view) {
        super(PmcDTO.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, inject(proto().name(), new FormDecoratorBuilder(15).build()));
        content.setWidget(++row, 0, inject(proto().dnsName(), new FormDecoratorBuilder(15).build()));
        content.setWidget(++row, 0, inject(proto().onboardingUser().firstName(), new FormDecoratorBuilder(15).build()));
        content.setWidget(++row, 0, inject(proto().onboardingUser().lastName(), new FormDecoratorBuilder(15).build()));
        content.setWidget(++row, 0, inject(proto().onboardingUser().email(), new FormDecoratorBuilder(15).build()));
        content.setWidget(++row, 0, inject(proto().onboardingUser().password(), new FormDecoratorBuilder(15).build()));

        content.setH1(++row, 0, 2, proto().features().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().features(), new PmcFeaturesForm()));

        selectTab(addTab(content));
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