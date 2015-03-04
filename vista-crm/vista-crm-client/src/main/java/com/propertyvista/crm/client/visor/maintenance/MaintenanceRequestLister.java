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
 */
package com.propertyvista.crm.client.visor.maintenance;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.maintenance.MaintenanceCrudService;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestLister extends SiteDataTablePanel<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestLister.class);

    private final MaintenanceRequestVisorView view;

    public MaintenanceRequestLister(MaintenanceRequestVisorView view) {
        super(MaintenanceRequestDTO.class, GWT.<MaintenanceCrudService> create(MaintenanceCrudService.class), true);
        this.view = view;
        MaintenanceRequestDTO proto = EntityFactory.getEntityPrototype(MaintenanceRequestDTO.class);

        setColumnDescriptors(
        //
                new ColumnDescriptor.Builder(proto.requestId()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto.unit()).build(), //
                new ColumnDescriptor.Builder(proto.category()).formatter(new IFormatter<IEntity, SafeHtml>() {

                    @Override
                    public SafeHtml format(IEntity value) {
                        SafeHtmlBuilder builder = new SafeHtmlBuilder();
                        if (value instanceof MaintenanceRequestDTO) {
                            // return slash-separated name list
                            StringBuilder result = new StringBuilder();
                            MaintenanceRequestCategory category = ((MaintenanceRequestDTO) value).category();
                            while (!category.parent().isNull()) {
                                if (!category.name().isNull()) {
                                    result.insert(0, result.length() > 0 ? "/" : "").insert(0, category.name().getValue());
                                }
                                category = category.parent();
                            }
                            builder.appendHtmlConstant(result.toString());
                        }
                        return builder.toSafeHtml();
                    }
                }).searchable(false).build(), new ColumnDescriptor.Builder(proto.priority()).build(), //
                new ColumnDescriptor.Builder(proto.summary()).build(), //
                new ColumnDescriptor.Builder(proto.reporterName()).columnTitle(i18n.tr("Tenant")).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto.reporterPhone(), false).build(), //
                new ColumnDescriptor.Builder(proto.permissionToEnter()).columnTitle(i18n.tr("Entry Allowed")).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto.petInstructions()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto.submitted()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto.status()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto.updated()).build(), //
                new ColumnDescriptor.Builder(proto.surveyResponse().rating()).build(), //
                new ColumnDescriptor.Builder(proto.surveyResponse().description()).visible(false).columnTitle(proto.surveyResponse().getMeta().getCaption())
                        .build());

        setDataTableModel(new DataTableModel<MaintenanceRequestDTO>());
    }

    @Override
    protected void onItemNew() {
        MaintenanceCrudService.MaintenanceInitializationData id = EntityFactory.create(MaintenanceCrudService.MaintenanceInitializationData.class);
        if (view.getTenantId() != null) {
            id.tenant().set(EntityFactory.createIdentityStub(Tenant.class, view.getTenantId()));
        } else if (view.getUnitId() != null) {
            id.unit().set(EntityFactory.createIdentityStub(AptUnit.class, view.getUnitId()));
        } else if (view.getBuildingId() != null) {
            id.building().set(EntityFactory.createIdentityStub(Building.class, view.getBuildingId()));
        }
        super.editNew(getItemOpenPlaceClass(), id);
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().submitted(), true), new Sort(proto().updated(), false));
    }

}
