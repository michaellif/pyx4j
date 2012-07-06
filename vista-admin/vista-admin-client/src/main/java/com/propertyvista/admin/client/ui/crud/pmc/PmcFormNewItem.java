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
package com.propertyvista.admin.client.ui.crud.pmc;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.rpc.PmcDTO;

public class PmcFormNewItem extends AdminEntityForm<PmcDTO> {

    private static final I18n i18n = I18n.get(PmcFormNewItem.class);

    public PmcFormNewItem() {
        this(false);
    }

    public PmcFormNewItem(boolean viewMode) {
        super(PmcDTO.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dnsName()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().firstName()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().lastName()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().password()), 15).build());

        selectTab(addTab(content, i18n.tr("General")));

        content.setH1(++row, 0, 2, proto().features().getMeta().getCaption());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().occupancyModel()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().productCatalog()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().leases()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().xmlSiteExport()), 15).build());
    }

}