/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.content.page;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.editors.CEntityDecoratableEditor;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;

class PageContentFolder extends VistaBoxFolder<PageContent> {

    private final CEntityEditor<PageDescriptor> parent;

    public PageContentFolder(CEntityEditor<PageDescriptor> parent) {
        super(PageContent.class, parent.isEditable());
        this.parent = parent;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PageContent) {
            return new PageContentEditor();
        }
        return super.create(member);
    }

    @Override
    protected void createNewEntity(PageContent newEntity, AsyncCallback<PageContent> callback) {
        newEntity.descriptor().set(parent.getValue());
        callback.onSuccess(newEntity);
    }

    class PageContentEditor extends CEntityDecoratableEditor<PageContent> {

        public PageContentEditor() {
            super(PageContent.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().locale()), 10).build());

            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto()._caption().caption()), 20).build());

            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto()._caption().secondaryCaption()), 20).build());

            if (isEditable()) {
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().content()), 60).build());
            } else {
                CLabel content = new CLabel();
                content.setAllowHtml(true);
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().content(), content), 60).build());
            }

            // TODO
            // main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().image(), new CFileUploader()), 60).build());
            return main;
        }
    }
}