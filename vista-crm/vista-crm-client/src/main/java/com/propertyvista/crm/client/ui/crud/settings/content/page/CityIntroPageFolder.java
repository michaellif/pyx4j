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
package com.propertyvista.crm.client.ui.crud.settings.content.page;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.settings.content.site.SiteForm;
import com.propertyvista.crm.client.ui.crud.settings.content.site.SiteViewer;
import com.propertyvista.crm.rpc.CrmSiteMap.Settings.Content;
import com.propertyvista.domain.site.CityIntroPage;

public class CityIntroPageFolder extends VistaTableFolder<CityIntroPage> {
    private static final I18n i18n = I18n.get(CityIntroPageFolder.class);

    private final SiteForm parent;

    private final SiteViewer viewer;

    public CityIntroPageFolder(SiteForm parent) {
        super(CityIntroPage.class, parent.isEditable());
        this.parent = parent;
        this.viewer = (!parent.isEditable() ? (SiteViewer) parent.getParentView() : null);
        setAddable(true);
    }

    @Override
    protected void createNewEntity(final CityIntroPage newEntity, final AsyncCallback<CityIntroPage> callback) {
        new CityInputDialog() {
            @Override
            void onInputComplete(String input) {
                newEntity.cityName().setValue(input);
                callback.onSuccess(newEntity);
            }
        }.show();
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().cityName(), "25em"));
        return columns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof CityIntroPage) {
            return new CityIntroPageEditor();
        }
        return super.create(member);
    }

    @Override
    protected IFolderDecorator<CityIntroPage> createDecorator() {
        TableFolderDecorator<CityIntroPage> decor = (TableFolderDecorator<CityIntroPage>) super.createDecorator();
        decor.setShowHeader(false);
        decor.setAddButtonVisible(false);
        return decor;
    }

    class CityIntroPageEditor extends CEntityFolderRowEditor<CityIntroPage> {

        public CityIntroPageEditor() {
            super(CityIntroPage.class, columns());
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject().equals(proto().cityName())) {
                CComponent<?, ?> comp = null;
                if (parent.isEditable()) {
                    comp = inject(column.getObject(), new CLabel<String>());
                } else {
                    comp = inject(column.getObject(), new CHyperlink(new Command() {
                        @Override
                        public void execute() {
                            viewer.viewChild(getValue().getPrimaryKey(), Content.CityIntroPage.class);
                        }
                    }));
                }
                return comp;
            }
            return super.createCell(column);
        }
    }

    abstract class CityInputDialog extends OkCancelDialog {
        private final CTextField city = new CTextField();

        public CityInputDialog() {
            super(i18n.tr("Enter City"));

            setBody(new WidgetDecorator.Builder(city).customLabel(i18n.tr("City")).build());
        }

        @Override
        public boolean onClickOk() {
            onInputComplete(city.getValue());
            return true;
        }

        abstract void onInputComplete(String input);
    }
}