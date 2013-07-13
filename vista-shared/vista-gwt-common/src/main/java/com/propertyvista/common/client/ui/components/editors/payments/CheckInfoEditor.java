/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.payments;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.payment.AccountType;
import com.propertyvista.domain.payment.CheckInfo;

public class CheckInfoEditor extends CEntityDecoratableForm<CheckInfo> {

    private static final I18n i18n = I18n.get(CheckInfoEditor.class);

    public CheckInfoEditor() {
        super(CheckInfo.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();

        int row = -1;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().nameOn()), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().bankName()), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().accountType()), 10).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().checkNo()), 5).build());

        row = -1;
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().transitNo()), 10).build());
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().institutionNo()), 5).build());
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().accountNo()), 15).build());

        return panel;
    }

    @Override
    public void addValidations() {
        if (ApplicationMode.isDevelopment()) {
            this.addDevShortcutHandler(new DevShortcutHandler() {
                @Override
                public void onDevShortcut(DevShortcutEvent event) {
                    if (event.getKeyCode() == 'Q') {
                        event.consume();
                        devGenerateEcheck();
                    }
                }
            });
        }
    }

    private void devGenerateEcheck() {
        get(proto().nameOn()).setValue("Dev");
        get(proto().bankName()).setValue("Nowhere Bank");
        get(proto().accountType()).setValue(AccountType.Chequing);
        get(proto().checkNo()).setValue("1");
        get(proto().institutionNo()).setValue("123");
        get(proto().transitNo()).setValue("12345");
        ((CTextFieldBase<?, ?>) get(proto().accountNo())).setValueByString(String.valueOf(System.currentTimeMillis() % 10000000));
    }
}
