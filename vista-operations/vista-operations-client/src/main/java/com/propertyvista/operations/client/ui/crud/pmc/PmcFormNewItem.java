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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.PmcDTO;

public class PmcFormNewItem extends OperationsEntityForm<PmcDTO> {

    private static final I18n i18n = I18n.get(PmcFormNewItem.class);

    public PmcFormNewItem(IForm<PmcDTO> view) {
        super(PmcDTO.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dnsName()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().onboardingAccountId()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().firstName()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().lastName()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().password()), 15).build());

        content.setH1(++row, 0, 2, proto().features().getMeta().getCaption());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().countryOfOperation()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().occupancyModel()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().productCatalog()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().leases()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().onlineApplication()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().xmlSiteExport()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().defaultProductCatalog()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().yardiIntegration()), 5).build());

        selectTab(addTab(content));

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        boolean existingOnboardingUser = !getValue().createPmcForExistingOnboardingUserRequest().isNull();
        get(proto().person().name().firstName()).setViewable(existingOnboardingUser);
        get(proto().person().name().lastName()).setViewable(existingOnboardingUser);
        get(proto().email()).setViewable(existingOnboardingUser);
        get(proto().password()).setVisible(!existingOnboardingUser);
    }

}