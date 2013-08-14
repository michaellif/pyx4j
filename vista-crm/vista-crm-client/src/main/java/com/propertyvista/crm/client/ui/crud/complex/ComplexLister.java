/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.complex;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.dto.ComplexDTO;

public class ComplexLister extends AbstractLister<ComplexDTO> {

    public ComplexLister() {
        super(ComplexDTO.class, true);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().name()).build(), 
            new MemberColumnDescriptor.Builder(proto().address().city()).sortable(false).searchable(false).build(),
            new MemberColumnDescriptor.Builder(proto().address().province()).sortable(false).searchable(false).build(),
            new MemberColumnDescriptor.Builder(proto().address().country()).sortable(false).searchable(false).build(),
            new MemberColumnDescriptor.Builder(proto().primaryBuilding()).sortable(false).searchable(false).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }
}
