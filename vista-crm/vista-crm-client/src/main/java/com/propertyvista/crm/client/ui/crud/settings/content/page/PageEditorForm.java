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

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
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
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(inject(proto().name()), 15);

        main.add(new CrmSectionSeparator(proto().content().getMeta().getCaption()));
        main.add(inject(proto().content(), new PageContentFolder(this)));

        main.add(new CrmSectionSeparator(proto().childPages().getMeta().getCaption()));
        main.add(inject(proto().childPages(), new PageDescriptorFolder(this, (PageViewer) getParentView())));

        return new CrmScrollPanel(main);
    }
}