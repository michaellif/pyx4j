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
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationStatus;

public class ApplicationStatusFolder extends VistaTableFolder<OnlineApplicationStatus> {

    public ApplicationStatusFolder() {
        super(OnlineApplicationStatus.class, false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().customer(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().role(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().status(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().progress(), "7em"));
        return columns;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member.getObjectClass().equals(Name.class)) {
            return new CEntityLabel<Name>();
        }
        return super.create(member);
    }
}