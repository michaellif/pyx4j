/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.communication.MessageGroup;

public class CommunicationGroupLister extends AbstractLister<MessageGroup> {

    public CommunicationGroupLister() {
        super(MessageGroup.class, true, true);

        setColumnDescriptors(createColumnDescriptors());
    }

    public static ColumnDescriptor[] createColumnDescriptors() {
        MessageGroup proto = EntityFactory.getEntityPrototype(MessageGroup.class);

        return new ColumnDescriptor[] { new MemberColumnDescriptor.Builder(proto.topic()).build(), new MemberColumnDescriptor.Builder(proto.category()).build() };
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().topic(), false));
    }

}
