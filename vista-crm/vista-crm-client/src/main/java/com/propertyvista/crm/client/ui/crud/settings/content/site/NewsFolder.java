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
package com.propertyvista.crm.client.ui.crud.settings.content.site;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.domain.site.News;

class NewsFolder extends VistaBoxFolder<News> {

    public NewsFolder() {
        super(News.class);
        // TODO Auto-generated constructor stub
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof News) {
            return new NewsEditor();
        }
        return super.create(member);
    }

    class NewsEditor extends CEntityEditor<News> {

        public NewsEditor() {
            super(News.class);
        }

        @Override
        public IsWidget createContent() {
            VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
            main.add(inject(proto().locale()), 10);
            main.add(inject(proto().caption()), 20);
            main.add(inject(proto().content()), 50);
            main.add(inject(proto().date()), 8.2);
            return main;
        }
    }
}