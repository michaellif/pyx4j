/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.pmc;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.PmcDTO;

public class PmcLister extends ListerBase<PmcDTO> {

    public PmcLister() {
        super(PmcDTO.class, AdminSiteMap.Management.PMC.class, false, true);

        List<ColumnDescriptor<PmcDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<PmcDTO>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().name(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().dnsName(), true));
        setColumnDescriptors(columnDescriptors);

    }

}
