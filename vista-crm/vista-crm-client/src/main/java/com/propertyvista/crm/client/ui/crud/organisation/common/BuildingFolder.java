/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.IPane;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.domain.property.asset.building.Building;

public class BuildingFolder extends VistaTableFolder<Building> {

    private final IPane parentView;

    public BuildingFolder(IPane parentView, boolean modifiable) {
        super(Building.class, modifiable);
        this.parentView = parentView;
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto().propertyCode(), "10em"),
                new FolderColumnDescriptor(proto().info().name(), "20em"),
                new FolderColumnDescriptor(proto().info().type(), "20em")
        );//@formatter:on
    }

    @Override
    protected CForm<Building> createItemForm(IObject<?> member) {
        return new CFolderRowEditor<Building>(Building.class, columns()) {
            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                CLabel<?> comp = inject(column.getObject(), new CLabel<String>());

                if (proto().propertyCode() == column.getObject()) {
                    comp.setNavigationCommand(new Command() {
                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(Building.class).formViewerPlace(getValue().getPrimaryKey()));
                        }
                    });
                }

                return comp;
            }
        };
    }

    @Override
    protected IFolderDecorator<Building> createFolderDecorator() {
        return new VistaTableFolderDecorator<Building>(this, this.isEditable());
    }

    @Override
    protected void addItem() {
        new BuildingSelectorDialog(parentView, new HashSet<>(getValue())) {
            @Override
            public void onClickOk() {
                for (Building selected : getSelectedItems()) {
                    addItem(selected);
                }
            }
        }.show();
    }
}