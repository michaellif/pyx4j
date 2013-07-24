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
package com.propertyvista.operations.client.ui.crud.scheduler.run;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.domain.scheduler.Run;

public class RunLister extends AbstractLister<Run> {

    private static List<ColumnDescriptor> INLINE_VIEW_COLUMN_DESCRIPTORS = createInlineViewColumnDescriptors();

    private static List<ColumnDescriptor> VIEW_COLUMN_DESCRIPTORS = createViewColumnDescriptors();

    static List<ColumnDescriptor> createInlineViewColumnDescriptors() {
        Run proto = EntityFactory.getEntityPrototype(Run.class);
        List<ColumnDescriptor> c = Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto.status()).build(),
                new MemberColumnDescriptor.Builder(proto.started()).build(),
                new MemberColumnDescriptor.Builder(proto.completed()).build(),
                new MemberColumnDescriptor.Builder(proto.forDate()).build(),
                new MemberColumnDescriptor.Builder(proto.executionReport().total()).build(),
                new MemberColumnDescriptor.Builder(proto.executionReport().processed()).build(),
                new MemberColumnDescriptor.Builder(proto.executionReport().failed()).build(),
                new MemberColumnDescriptor.Builder(proto.executionReport().erred()).build(),
                new MemberColumnDescriptor.Builder(proto.executionReport().averageDuration()).build(),
                new MemberColumnDescriptor.Builder(proto.executionReport().totalDuration()).build(),
                new MemberColumnDescriptor.Builder(proto.created()).build(),
                new MemberColumnDescriptor.Builder(proto.updated()).build()
        );//@formatter:on
        return c;
    }

    static List<ColumnDescriptor> createViewColumnDescriptors() {
        Run proto = EntityFactory.getEntityPrototype(Run.class);
        List<ColumnDescriptor> c = new Vector<ColumnDescriptor>(Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto.trigger().name()).title("Trigger Name").build(),
                new MemberColumnDescriptor.Builder(proto.trigger().triggerType()).build(),
                new MemberColumnDescriptor.Builder(proto.trigger()).searchableOnly().build()
        ));//@formatter:on
        c.addAll(createInlineViewColumnDescriptors());
        return c;
    }

    public RunLister() {
        this(false);
    }

    public RunLister(boolean isInlineMode) {
        super(Run.class, false);
        setColumnDescriptors(isInlineMode ? INLINE_VIEW_COLUMN_DESCRIPTORS : VIEW_COLUMN_DESCRIPTORS);
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().updated(), true));
    }
}
