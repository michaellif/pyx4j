/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.pmc;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.PmcDnsName;

public class PmcDnsNameFolder extends VistaTableFolder<PmcDnsName> {

    public PmcDnsNameFolder(boolean modifiable) {
        super(PmcDnsName.class, modifiable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        List<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().enabled(), "6em"));
        columns.add(new EntityFolderColumnDescriptor(proto().dnsName(), "25em"));
        columns.add(new EntityFolderColumnDescriptor(proto().target(), "13em"));
        columns.add(new EntityFolderColumnDescriptor(proto().googleAPIKey(), "16em"));
        return columns;
    }

}
