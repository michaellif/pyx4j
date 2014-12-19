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
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.pmc.PmcDnsName;

public class PmcDnsNameFolder extends VistaTableFolder<PmcDnsName> {

    public PmcDnsNameFolder(boolean modifiable) {
        super(PmcDnsName.class, modifiable);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        List<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
        columns.add(new FolderColumnDescriptor(proto().enabled(), "6em"));
        columns.add(new FolderColumnDescriptor(proto().dnsName(), "25em"));
        columns.add(new FolderColumnDescriptor(proto().target(), "13em"));
        columns.add(new FolderColumnDescriptor(proto().httpsEnabled(), "6em"));
        columns.add(new FolderColumnDescriptor(proto().googleAPIKey(), "16em"));
        columns.add(new FolderColumnDescriptor(proto().googleAnalyticsId(), "16em"));
        return columns;
    }

}
