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
package com.propertyvista.crm.client.ui.crud.administration.website.content.gadgets;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.site.News;

public class NewsFolder extends VistaBoxFolder<News> {

    public NewsFolder(boolean modifyable) {
        super(News.class, modifyable);
    }

    @Override
    protected CForm<News> createItemForm(IObject<?> member) {
        return new NewsEditor();
    }

    class NewsEditor extends CForm<News> {

        public NewsEditor() {
            super(News.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().locale()).decorate().componentWidth(120);
            formPanel.append(Location.Left, proto().caption()).decorate();
            formPanel.append(Location.Left, proto().content()).decorate();
            formPanel.append(Location.Left, proto().date()).decorate().componentWidth(120);

            return formPanel;
        }
    }
}