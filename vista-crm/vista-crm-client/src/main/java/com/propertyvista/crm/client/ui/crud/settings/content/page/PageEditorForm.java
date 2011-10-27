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
package com.propertyvista.crm.client.ui.crud.settings.content.page;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.site.PageDescriptor;

public class PageEditorForm extends CrmEntityForm<PageDescriptor> {

    public PageEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public PageEditorForm(IEditableComponentFactory factory) {
        super(PageDescriptor.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());

        main.setH1(++row, 0, 1, proto().content().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().content(), new PageContentFolder(this)));

        main.setH1(++row, 0, 1, proto().childPages().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().childPages(), new PageDescriptorFolder(this)));

        return new CrmScrollPanel(main);
    }
}