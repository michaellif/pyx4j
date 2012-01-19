/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class UtilityFolder extends VistaTableFolder<ServiceItemType> {

    public static final ArrayList<EntityFolderColumnDescriptor> COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
    static {
        ServiceItemType proto = EntityFactory.getEntityPrototype(ServiceItemType.class);
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.name(), "30em"));
    }

    public UtilityFolder() {
        super(ServiceItemType.class, false);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ServiceItemType) {
            return new UtilityEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().name(), "30"));
        return columns;
    }

    private class UtilityEditor extends CEntityFolderRowEditor<ServiceItemType> {
        public UtilityEditor() {
            super(ServiceItemType.class, UtilityFolder.COLUMNS);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().name()) {
                return inject(column.getObject(), new CLabel());
            }
            return super.createCell(column);
        }
    }

    @Override
    protected IFolderDecorator<ServiceItemType> createDecorator() {
        TableFolderDecorator<ServiceItemType> decotator = (TableFolderDecorator<ServiceItemType>) super.createDecorator();
        decotator.setShowHeader(false);
        return decotator;
    }
}