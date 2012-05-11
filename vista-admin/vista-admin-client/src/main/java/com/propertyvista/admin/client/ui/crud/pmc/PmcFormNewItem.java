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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.rpc.PmcDTO;

public class PmcFormNewItem extends AdminEntityForm<PmcDTO> {

    public PmcFormNewItem() {
        this(false);
    }

    public PmcFormNewItem(boolean viewMode) {
        super(PmcDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dnsName()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().firstName()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().lastName()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().password()), 15).build());

        return main;
    }

}