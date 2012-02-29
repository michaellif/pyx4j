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
package com.propertyvista.crm.client.ui.crud.settings.financial.glcode;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.GlCodeCategory;

public class GlCodeCategoryEditorForm extends CrmEntityForm<GlCodeCategory> {

    public GlCodeCategoryEditorForm() {
        this(false);
    }

    public GlCodeCategoryEditorForm(boolean viewMode) {
        super(GlCodeCategory.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = 0;
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().categoryId()), 7).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().description()), 25).build());

        main.setH3(row++, 0, 1, proto().glCodes().getMeta().getCaption());
        main.setWidget(row++, 0, inject(proto().glCodes(), new GlCodeFolder(isEditable())));

        return new CrmScrollPanel(main);
    }
}