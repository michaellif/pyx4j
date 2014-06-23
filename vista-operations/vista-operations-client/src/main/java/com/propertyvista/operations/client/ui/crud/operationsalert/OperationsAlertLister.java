/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.operationsalert;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.rpc.dto.OperationsAlertDTO;

public class OperationsAlertLister extends AbstractLister<OperationsAlertDTO> {

    public OperationsAlertLister() {
        super(OperationsAlertDTO.class, false, false);

        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().namespace()).build(),
                new MemberColumnDescriptor.Builder(proto().user()).build(),
                new MemberColumnDescriptor.Builder(proto().resolved()).build(),
                new MemberColumnDescriptor.Builder(proto().admin()).build(),
                new MemberColumnDescriptor.Builder(proto().created()).build(),
                new MemberColumnDescriptor.Builder(proto().remoteAddr()).build(),
                new MemberColumnDescriptor.Builder(proto().entityId()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto().entityClass()).build(),
                new MemberColumnDescriptor.Builder(proto().details()).build()                
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().namespace(), true), new Sort(proto().resolved(), false));
    }
}
