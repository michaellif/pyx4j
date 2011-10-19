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
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.ui.editors;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.charges.ChargeLine;

public class ChargeLineFolder extends VistaTableFolder<ChargeLine> {

    public ChargeLineFolder() {
        super(ChargeLine.class);
        setOrderable(false);
        setModifiable(false);
    }

    @Override
    protected List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().type(), "30em"));
        columns.add(new EntityFolderColumnDescriptor(proto().label(), "15em"));
        return columns;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ChargeLine) {
            return new ChargeLineEditor();
        } else {
            return super.create(member);
        }
    }

    class ChargeLineEditor extends CEntityFolderRowEditor<ChargeLine> {

        public ChargeLineEditor() {
            super(ChargeLine.class, columns());
        }

    }

}