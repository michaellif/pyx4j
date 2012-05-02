/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.content.site;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.site.HomePageGadget;

public class LayoutModuleFolder extends VistaTableFolder<HomePageGadget> {

    public LayoutModuleFolder(boolean editable) {
        super(HomePageGadget.class, editable);
        setAddable(false);
    }

    @Override
    protected CEntityFolderItem<HomePageGadget> createItem(boolean first) {
        final CEntityFolderItem<HomePageGadget> item = super.createItem(first);
        item.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (item.getValue() != null) {
                    item.setRemovable(HomePageGadget.GadgetType.custom.equals(item.getValue().moduleType().getValue()));
                }
            }
        });
        return item;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().name(), "25em"));
        columns.add(new EntityFolderColumnDescriptor(proto().enabled(), "5em"));
        return columns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof HomePageGadget) {
            return new LayoutModuleItemEditor();
        }
        return super.create(member);
    }

    private class LayoutModuleItemEditor extends CEntityFolderRowEditor<HomePageGadget> {

        public LayoutModuleItemEditor() {
            super(HomePageGadget.class, columns());
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject().equals(proto().name())) {
                CComponent<?, ?> comp = null;
                if (isEditable()) {
                    comp = inject(column.getObject(), new CLabel());
                } else {
                    comp = inject(column.getObject(), new CHyperlink(new Command() {
                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(HomePageGadget.class, getValue().getPrimaryKey()));
                        }
                    }));
                }
                return comp;
            }
            return super.createCell(column);
        }
    }
}
