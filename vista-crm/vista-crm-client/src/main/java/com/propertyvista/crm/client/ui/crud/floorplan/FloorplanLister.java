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

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class FloorplanLister extends AbstractLister<FloorplanDTO> {

    public FloorplanLister() {
        super(FloorplanDTO.class, !VistaFeatures.instance().yardiIntegration() ? true : false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().name()).build(),
            new MemberColumnDescriptor.Builder(proto().marketingName()).build(),
            new MemberColumnDescriptor.Builder(proto().floorCount()).build(),
            new MemberColumnDescriptor.Builder(proto().bedrooms()).build(),
            new MemberColumnDescriptor.Builder(proto().dens()).build(),
            new MemberColumnDescriptor.Builder(proto().bathrooms()).build(),
            new MemberColumnDescriptor.Builder(proto().counters()._unitCount()).build(),
            new MemberColumnDescriptor.Builder(proto().counters()._marketingUnitCount(), false).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().bedrooms(), false), new Sort(proto().bathrooms(), false));
    }
}
