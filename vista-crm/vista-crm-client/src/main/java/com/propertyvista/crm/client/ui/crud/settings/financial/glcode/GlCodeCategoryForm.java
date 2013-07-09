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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.GlCodeCategory;

public class GlCodeCategoryForm extends CrmEntityForm<GlCodeCategory> {

    private static final I18n i18n = I18n.get(GlCodeCategoryForm.class);

    public GlCodeCategoryForm(IForm<GlCodeCategory> view) {
        super(GlCodeCategory.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));
        int row = 0;
        content.setWidget(row++, 0, new FormDecoratorBuilder(inject(proto().categoryId()), 7).build());
        content.setWidget(row++, 0, new FormDecoratorBuilder(inject(proto().description()), 25).build());

        content.setH3(row++, 0, 1, proto().glCodes().getMeta().getCaption());
        content.setWidget(row++, 0, inject(proto().glCodes(), new GlCodeFolder(isEditable())));

        selectTab(addTab(content));
    }
}