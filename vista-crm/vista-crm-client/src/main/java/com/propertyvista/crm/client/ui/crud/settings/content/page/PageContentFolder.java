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
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;

class PageContentFolder extends VistaBoxFolder<PageContent> {

    private final CEntityEditor<PageDescriptor> parent;

    public PageContentFolder(CEntityEditor<PageDescriptor> parent) {
        super(PageContent.class);
        this.parent = parent;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
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

    class PageContentEditor extends CEntityEditor<PageContent> {

        public PageContentEditor() {
            super(PageContent.class);
        }

        @Override
        public IsWidget createContent() {
            VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

            main.add(inject(proto().locale()), 10);

            main.add(inject(proto()._caption().caption()), 20);

            if (isEditable()) {
                main.add(inject(proto().content()), 60);
            } else {
                CLabel content = new CLabel();
                content.setAllowHtml(true);
                main.add(inject(proto().content(), content), 60);
            }

            // TODO
            // main.add(inject(proto().image(), new CFileUploader()), 60);
            return main;
        }
    }
}