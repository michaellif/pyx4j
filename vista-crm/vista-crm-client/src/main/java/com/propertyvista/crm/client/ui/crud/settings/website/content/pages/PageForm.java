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
package com.propertyvista.crm.client.ui.crud.settings.website.content.pages;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.site.PageDescriptor;

public class PageForm extends CrmEntityForm<PageDescriptor> {

    private static final I18n i18n = I18n.get(PageForm.class);

    public PageForm(IForm<PageDescriptor> view) {
        super(PageDescriptor.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name()), 15).build());

        content.setH1(++row, 0, 1, proto().content().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().content(), new PageContentFolder(this)));

        content.setH1(++row, 0, 1, proto().childPages().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().childPages(), new PageDescriptorFolder(this)));

        selectTab(addTab(content));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().childPages()).setVisible(getValue().type().getValue() == PageDescriptor.Type.staticContent);
    }
}