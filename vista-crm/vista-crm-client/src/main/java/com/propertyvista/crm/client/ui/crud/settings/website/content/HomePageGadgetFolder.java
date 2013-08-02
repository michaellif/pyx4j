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
package com.propertyvista.crm.client.ui.crud.settings.website.content;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.site.gadgets.GadgetContent;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetType;

public class HomePageGadgetFolder extends VistaTableFolder<HomePageGadget> {

    public HomePageGadgetFolder(boolean editable) {
        super(HomePageGadget.class, editable);
        setAddable(false);
    }

    @Override
    protected IList<HomePageGadget> preprocessValue(IList<HomePageGadget> value, boolean fireEvent, boolean populate) {
        // set gadget type based on the gadget class
        if (value != null) {
            for (HomePageGadget gadget : value) {
                @SuppressWarnings("unchecked")
                GadgetType type = GadgetType.getGadgetType((Class<? extends GadgetContent>) gadget.content().getInstanceValueClass());
                gadget.type().setValue(type);
            }
        }
        return value;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().name(), "25em"));
        columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().status(), "10em"));
        return columns;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof HomePageGadget) {
            return new HomePageGadgetItemEditor();
        }
        return super.create(member);
    }

    private class HomePageGadgetItemEditor extends CEntityFolderRowEditor<HomePageGadget> {

        public HomePageGadgetItemEditor() {
            super(HomePageGadget.class, columns());
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?> comp = null;
            if (column.getObject().equals(proto().name())) {
                comp = new CLabel<String>();
                ((CField) comp).setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(HomePageGadget.class, getValue().getPrimaryKey()));
                    }
                });
                inject(column.getObject(), comp);
            } else {
                comp = super.createCell(column);
            }
            return comp;
        }
    }
}
