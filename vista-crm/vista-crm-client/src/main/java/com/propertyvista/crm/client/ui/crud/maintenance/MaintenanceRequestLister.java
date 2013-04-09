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
package com.propertyvista.crm.client.ui.crud.maintenance;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestLister extends AbstractLister<MaintenanceRequestDTO> {

    public MaintenanceRequestLister() {
        super(MaintenanceRequestDTO.class, true);

        setColumnDescriptors(createColumnDescriptors());
    }

    public static ColumnDescriptor[] createColumnDescriptors() {
        MaintenanceRequestDTO proto = EntityFactory.getEntityPrototype(MaintenanceRequestDTO.class);

        return new ColumnDescriptor[] {// @formatter:off
                new ColumnDescriptor(proto.issueClassification().issue().getPath().toString(), proto.issueClassification().issue().getMeta().getCaption()) {
                    @Override
                    public String convert(IEntity entity) {
                        if (entity instanceof MaintenanceRequestDTO) {
                            // return the first available value
                            MaintenanceRequestCategory issue = ((MaintenanceRequestDTO)entity).category();
                            if (!issue.name().isNull()) {
                                return issue.name().getValue();
                            } else if (!issue.parent().name().isNull()) {
                                return issue.parent().name().getValue();
                            } else if (!issue.parent().parent().name().isNull()) {
                                return issue.parent().parent().name().getValue();
                            } else if (!issue.parent().parent().parent().name().isNull()) {
                                return issue.parent().parent().parent().name().getValue();
                            }
                        }
                        return super.convert(entity);
                    }
                },
                new MemberColumnDescriptor.Builder(proto.issueClassification().priority()).build(),
                new MemberColumnDescriptor.Builder(proto.description()).build(),
                new MemberColumnDescriptor.Builder(proto.leaseParticipant().customer().person().name()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.leaseParticipant().customer().person().name().firstName(), false).build(),
                new MemberColumnDescriptor.Builder(proto.leaseParticipant().customer().person().name().lastName(), false).build(),
                new MemberColumnDescriptor.Builder(proto.permissionToEnter()).build(),
                new MemberColumnDescriptor.Builder(proto.petInstructions()).build(),
                new MemberColumnDescriptor.Builder(proto.submitted()).build(),
                new MemberColumnDescriptor.Builder(proto.status()).build(),
                new MemberColumnDescriptor.Builder(proto.updated()).build(),
                new MemberColumnDescriptor.Builder(proto.surveyResponse().rating()).build(),
                new MemberColumnDescriptor.Builder(proto.surveyResponse().description(), false).columnTitle(proto.surveyResponse().getMeta().getCaption()).build()
        }; // @formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().submitted().getPath().toString(), false), new Sort(proto().updated().getPath().toString(), false));
    }
}
