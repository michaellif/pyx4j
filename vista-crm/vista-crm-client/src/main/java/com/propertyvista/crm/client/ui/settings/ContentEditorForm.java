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
package com.propertyvista.crm.client.ui.settings;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RichTextArea;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.widgets.client.richtext.RichTextEditorDecorator;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmHeader2Decorator;
import com.propertyvista.portal.domain.site.PageContent;
import com.propertyvista.portal.domain.site.PageDescriptor;

public class ContentEditorForm extends CrmEntityForm<PageContent> {

    public ContentEditorForm() {
        super(PageContent.class, new CrmEditorsComponentFactory());
    }

    public ContentEditorForm(IEditableComponentFactory factory) {
        super(PageContent.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(inject(proto().descriptor().caption()), 15);

        RichTextArea textArea = new RichTextArea();
        textArea.setWidth("700px");
        main.add(new RichTextEditorDecorator(textArea));

        main.add(new CrmHeader2Decorator(proto().descriptor().childPages().getMeta().getCaption()));
        main.add(inject(proto().descriptor().childPages(), createChildPagesList()));

        main.setWidth("100%");
        return main;
    }

    private CEntityFolderEditor<PageDescriptor> createChildPagesList() {
        return new CrmEntityFolder<PageDescriptor>(PageDescriptor.class, i18n.tr("Item"), isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().caption(), "15em"));
                return columns;
            }
        };
    }

}