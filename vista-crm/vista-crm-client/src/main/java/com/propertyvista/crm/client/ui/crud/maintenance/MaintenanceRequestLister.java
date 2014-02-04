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

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestLister extends AbstractLister<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestLister.class);

    public MaintenanceRequestLister() {
        super(MaintenanceRequestDTO.class, true);

        setColumnDescriptors(createColumnDescriptors());
    }

    public static ColumnDescriptor[] createColumnDescriptors() {
        MaintenanceRequestDTO proto = EntityFactory.getEntityPrototype(MaintenanceRequestDTO.class);

        return new ColumnDescriptor[] {
                new MemberColumnDescriptor.Builder(proto.requestId()).build(),
                new MemberColumnDescriptor.Builder(proto.building().propertyCode()).columnTitle(i18n.tr("Building")).build(),
                createAddressColumn(proto),
                new MemberColumnDescriptor.Builder(proto.building().info().address().city()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.unit()).build(),
                createCategoryColumn(proto),
                new MemberColumnDescriptor.Builder(proto.priority()).build(),
                new MemberColumnDescriptor.Builder(proto.summary()).build(),
                new MemberColumnDescriptor.Builder(proto.reporterName()).columnTitle(i18n.tr("Tenant")).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.reporterPhone(), false).build(),
                new MemberColumnDescriptor.Builder(proto.permissionToEnter()).columnTitle(i18n.tr("Entry Allowed")).build(),
                new MemberColumnDescriptor.Builder(proto.petInstructions()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.submitted()).build(),
                new MemberColumnDescriptor.Builder(proto.status()).build(),
                new MemberColumnDescriptor.Builder(proto.updated()).build(),
                new MemberColumnDescriptor.Builder(proto.surveyResponse().rating()).build(),
                new MemberColumnDescriptor.Builder(proto.surveyResponse().description()).visible(false)
                        .columnTitle(proto.surveyResponse().getMeta().getCaption()).build() };
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().submitted(), true), new Sort(proto().updated(), false));
    }

    private static ColumnDescriptor createCategoryColumn(MaintenanceRequestDTO proto) {
        ColumnDescriptor desc = new ColumnDescriptor(proto.category().getPath().toString(), proto.category().getMeta().getCaption()) {
            @Override
            public String convert(IEntity entity) {
                if (entity instanceof MaintenanceRequestDTO) {
                    // return slash-separated name list
                    StringBuilder result = new StringBuilder();
                    MaintenanceRequestCategory category = ((MaintenanceRequestDTO) entity).category();
                    while (!category.parent().isNull()) {
                        if (!category.name().isNull()) {
                            result.insert(0, result.length() > 0 ? "/" : "").insert(0, category.name().getValue());
                        }
                        category = category.parent();
                    }
                    return result.toString();
                }
                return super.convert(entity);
            }
        };
        desc.setSearchable(false); // do not use if for filtering!..
        return desc;
    }

    private static ColumnDescriptor createAddressColumn(MaintenanceRequestDTO proto) {
        ColumnDescriptor desc = new ColumnDescriptor(proto.building().info().address().getPath().toString(), proto.building().info().address().getMeta()
                .getCaption()) {
            @Override
            public String convert(IEntity entity) {
                if (entity instanceof MaintenanceRequestDTO) {
                    AddressStructured addr = ((MaintenanceRequestDTO) entity).building().info().address();
                    return SimpleMessageFormat.format(
                            "{0,choice,null#|!null#{0}-}{1} {2} {3}{4,choice,other#|null#|!null# {4}}{5,choice,null#|!null# {5}}, {6}", //
                            addr.suiteNumber().getValue(), addr.streetNumber().getValue(), addr.streetNumberSuffix().getValue(), addr.streetName().getValue(),
                            addr.streetType().getValue(), addr.streetDirection().getValue(), addr.city().getValue());
                }
                return super.convert(entity);
            }
        };
        desc.setSearchable(false); // do not use if for filtering!..
        return desc;
    }
}
