/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.floorplan;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.FloorplanDTO;

public class FloorplanLister extends ListerBase<FloorplanDTO> {

    public FloorplanLister() {
        super(FloorplanDTO.class, CrmSiteMap.Properties.Floorplan.class, false, true);
        getDataTablePanel().setFilterEnabled(false);

        setColumnDescriptors(

        new MemberColumnDescriptor.Builder(proto().name()).build(),

        new MemberColumnDescriptor.Builder(proto().marketingName()).build(),

        new MemberColumnDescriptor.Builder(proto().floorCount()).build(),

        new MemberColumnDescriptor.Builder(proto().bedrooms()).build(),

        new MemberColumnDescriptor.Builder(proto().dens()).build(),

        new MemberColumnDescriptor.Builder(proto().bathrooms()).build(),

        new MemberColumnDescriptor.Builder(proto().counters()._unitCount()).build(),

        new MemberColumnDescriptor.Builder(proto().counters()._marketingUnitCount(), false).build()

        );
    }
}
