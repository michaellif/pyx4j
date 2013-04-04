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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.ARCode;

public class UtilityFolder extends VistaTableFolder<ARCode> {

    public static final ArrayList<EntityFolderColumnDescriptor> COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
    static {
        ARCode proto = EntityFactory.getEntityPrototype(ARCode.class);
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.name(), "30em"));
    }

    public UtilityFolder() {
        super(ARCode.class, false);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ARCode) {
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

    private class UtilityEditor extends CEntityFolderRowEditor<ARCode> {
        public UtilityEditor() {
            super(ARCode.class, UtilityFolder.COLUMNS);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().name()) {
                return inject(column.getObject(), new CLabel<String>());
            }
            return super.createCell(column);
        }
    }

    @Override
    protected IFolderDecorator<ARCode> createFolderDecorator() {
        TableFolderDecorator<ARCode> decotator = (TableFolderDecorator<ARCode>) super.createFolderDecorator();
        decotator.setShowHeader(false);
        return decotator;
    }
}