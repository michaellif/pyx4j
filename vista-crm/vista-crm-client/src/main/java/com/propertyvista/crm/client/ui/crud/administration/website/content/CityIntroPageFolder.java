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
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.website.content;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.ContentManagement;
import com.propertyvista.domain.site.CityIntroPage;

public class CityIntroPageFolder extends VistaTableFolder<CityIntroPage> {
    private static final I18n i18n = I18n.get(CityIntroPageFolder.class);

    private final SiteForm parent;

    private final SiteViewer viewer;

    public CityIntroPageFolder(SiteForm parent) {
        super(CityIntroPage.class, parent.isEditable());
        this.parent = parent;
        this.viewer = (!parent.isEditable() ? (SiteViewer) parent.getParentView() : null);
        setAddable(false);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        ArrayList<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
        columns.add(new FolderColumnDescriptor(proto().cityName(), "25em"));
        return columns;
    }

    @Override
    protected CForm<CityIntroPage> createItemForm(IObject<?> member) {
        return new CityIntroPageEditor();
    }

    @Override
    protected IFolderDecorator<CityIntroPage> createFolderDecorator() {
        TableFolderDecorator<CityIntroPage> decor = (TableFolderDecorator<CityIntroPage>) super.createFolderDecorator();
        decor.setShowHeader(false);
        decor.setAddButtonVisible(false);
        return decor;
    }

    class CityIntroPageEditor extends CFolderRowEditor<CityIntroPage> {

        public CityIntroPageEditor() {
            super(CityIntroPage.class, columns());
        }

        @Override
        protected CField<?, ?> createCell(FolderColumnDescriptor column) {
            if (column.getObject().equals(proto().cityName())) {
                CField<?, ?> comp = null;
                comp = inject(column.getObject(), new CLabel<String>());
                comp.setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        viewer.viewChild(getValue().getPrimaryKey(), ContentManagement.Website.CityIntroPage.class);
                    }
                });
                return comp;
            }
            return super.createCell(column);
        }
    }
}