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
package com.propertyvista.common.client.ui.components.folders;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.charges.ChargeLine;

public class ChargeLineFolder extends VistaTableFolder<ChargeLine> {

    public ChargeLineFolder(boolean modifyable) {
        super(ChargeLine.class, modifyable);
        setOrderable(false);
        setModifiable(false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().type(), "30em"));
        columns.add(new EntityFolderColumnDescriptor(proto().label(), "15em"));
        return columns;
    }
}