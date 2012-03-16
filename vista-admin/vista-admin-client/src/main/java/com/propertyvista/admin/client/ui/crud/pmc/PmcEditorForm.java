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
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.rpc.PmcDTO;

public class PmcEditorForm extends AdminEntityForm<PmcDTO> {

    private static final I18n i18n = I18n.get(PmcEditorForm.class);

    public PmcEditorForm() {
        this(false);
    }

    public PmcEditorForm(boolean viewMode) {
        super(PmcDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("General"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dnsName()), 15).build());

        get(proto().dnsName()).setViewable(true);

        main.setH1(++row, 0, 2, proto().dnsNameAliases().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().dnsNameAliases(), new PmcDnsNameFolder(isEditable())));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        return main;
    }
}