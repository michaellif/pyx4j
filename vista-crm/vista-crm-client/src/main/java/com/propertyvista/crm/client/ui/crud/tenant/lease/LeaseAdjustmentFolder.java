/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 13, 2012
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class LeaseAdjustmentFolder extends VistaTableFolder<LeaseAdjustment> {

    public LeaseAdjustmentFolder(boolean modifyable) {
        super(LeaseAdjustment.class, modifyable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().reason(), "15em"));
        columns.add(new EntityFolderColumnDescriptor(proto().amount(), "8em"));
        columns.add(new EntityFolderColumnDescriptor(proto().description(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().effectiveDate(), "9em"));
        columns.add(new EntityFolderColumnDescriptor(proto().expirationDate(), "9em"));
        return columns;
    }

    @Override
    protected void addItem(LeaseAdjustment newEntity) {
        if (newEntity.isEmpty()) {
            newEntity.effectiveDate().setValue(new LogicalDate());
        }
        super.addItem(newEntity);
    }
}