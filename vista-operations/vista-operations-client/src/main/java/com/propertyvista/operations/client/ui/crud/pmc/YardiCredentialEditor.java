/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.PasswordIdentityFormat;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.security.PasswordIdentity;
import com.propertyvista.domain.settings.PmcYardiCredential;

class YardiCredentialEditor extends CEntityDecoratableForm<PmcYardiCredential> {

    private static final I18n i18n = I18n.get(YardiCredentialEditor.class);

    YardiCredentialEditor() {
        super(PmcYardiCredential.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        int row = -1;

        content.setH1(++row, 0, 2, i18n.tr("Yardi Credentials"));
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().serviceURLBase()), true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().residentTransactionsServiceURL()), true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().sysBatchServiceURL()), true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().maintenanceRequestsServiceURL()), true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().propertyCode()), true).build());

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().username()), 30, true).build());

        CPersonalIdentityField<PasswordIdentity> password = new CPersonalIdentityField<PasswordIdentity>(PasswordIdentity.class);
        password.setFormat(new PasswordIdentityFormat(password));
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().password(), password), 30, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().serverName()), 30, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().database()), 30, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().platform()), 15, true).build());

        return content;
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
                        devGenerateTestCredencials();
                    }
                }
            });
        }
    }

    private int q = 0;

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        q = 0;
    }

    private void devGenerateTestCredencials() {
        // See http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Yardi

        get(proto().residentTransactionsServiceURL()).setValue(null);
        get(proto().sysBatchServiceURL()).setValue(null);
        get(proto().maintenanceRequestsServiceURL()).setValue(null);
        get(proto().platform()).setValue(PmcYardiCredential.Platform.SQL);
        switch (q) {
        case 0:
            get(proto().serviceURLBase()).setValue("http://yardi.birchwoodsoftwaregroup.com/Voyager60");
            get(proto().username()).setValue("vista_dev");
            ((CTextFieldBase<?, ?>) get(proto().password())).setValueByString("vista_dev");
            get(proto().serverName()).setValue("WIN-CO5DPAKNUA4\\YARDI");
            get(proto().database()).setValue("vista_dev");
            q++;
            break;
        case 1:
            get(proto().serviceURLBase()).setValue("https://www.iyardiasp.com/8223thirddev");
            get(proto().username()).setValue("propertyvistaws");
            ((CTextFieldBase<?, ?>) get(proto().password())).setValueByString("52673");
            get(proto().serverName()).setValue("aspdb04");
            get(proto().database()).setValue("afqoml_live");
            q++;
            break;
        case 2:
            get(proto().serviceURLBase()).setValue("https://www.iyardiasp.com/8223thirddev");
            get(proto().username()).setValue("propertyvista-srws");
            ((CTextFieldBase<?, ?>) get(proto().password())).setValueByString("55548");
            get(proto().serverName()).setValue("aspdb04");
            get(proto().database()).setValue("afqoml_live");
            q++;
            break;
        default:
            q = 0;
            devGenerateTestCredencials();
            break;
        }

    }
}
