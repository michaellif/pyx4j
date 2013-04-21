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
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.settings.PmcYardiCredential;

class YardiCredentialEditor extends CEntityDecoratableForm<PmcYardiCredential> {

    private static final I18n i18n = I18n.get(YardiCredentialEditor.class);

    YardiCredentialEditor() {
        super(PmcYardiCredential.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;

        content.setH1(++row, 0, 2, i18n.tr("Yardi Credentials"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().serviceURLBase()), 50).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().residentTransactionsServiceURL()), 50).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sysBatchServiceURL()), 50).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().maintenanceRequestsServiceURL()), 50).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().propertyCode()), 30).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().username()), 30).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().credential()), 30).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().serverName()), 30).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().database()), 30).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().platform()), 15).build());

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

    private void devGenerateTestCredencials() {
        // See http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Yardi

        get(proto().residentTransactionsServiceURL()).setValue(null);
        get(proto().sysBatchServiceURL()).setValue(null);
        get(proto().maintenanceRequestsServiceURL()).setValue(null);
        get(proto().platform()).setValue(PmcYardiCredential.Platform.SQL);
        switch (q) {
        case 0:
            get(proto().serviceURLBase()).setValue("http://yardi.birchwoodsoftwaregroup.com/Voyager60");
            get(proto().username()).setValue("sa");
            get(proto().credential()).setValue("akan1212");
            get(proto().serverName()).setValue("WIN-CO5DPAKNUA4\\YARDI");
            get(proto().database()).setValue("sl_0411");
            break;
        case 1:
            get(proto().serviceURLBase()).setValue("https://yardi.starlightinvest.com/voyager6008sp17");
            get(proto().username()).setValue("propvist");
            get(proto().credential()).setValue("access@123");
            get(proto().serverName()).setValue("SLDB02");
            get(proto().database()).setValue("PropertyVista_TEST");
            break;
        case 2:
            get(proto().serviceURLBase()).setValue("https://www.iyardiasp.com/8223thirddev");
            get(proto().username()).setValue("propertyvista-srws");
            get(proto().credential()).setValue("55548");
            get(proto().serverName()).setValue("aspdb04");
            get(proto().database()).setValue("afqoml_live");
            break;
        }
        q++;
        if (q > 1) {
            q = 0;
        }

    }

}
