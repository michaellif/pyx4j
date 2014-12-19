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
package com.propertyvista.crm.client.ui.crud.maintenance;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectionDialog;
import com.propertyvista.crm.rpc.services.maintenance.MaintenanceCrudService;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestLister extends SiteDataTablePanel<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestLister.class);

    public MaintenanceRequestLister() {
        super(MaintenanceRequestDTO.class, GWT.<AbstractCrudService<MaintenanceRequestDTO>> create(MaintenanceCrudService.class), true);

        setColumnDescriptors(createColumnDescriptors());
        setDataTableModel(new DataTableModel<MaintenanceRequestDTO>());
    }

    public static ColumnDescriptor[] createColumnDescriptors() {
        MaintenanceRequestDTO proto = EntityFactory.getEntityPrototype(MaintenanceRequestDTO.class);

        return new ColumnDescriptor[] {
                new ColumnDescriptor.Builder(proto.requestId()).build(),
                new ColumnDescriptor.Builder(proto.building().propertyCode()).columnTitle(i18n.tr("Building")).build(),
                new ColumnDescriptor.Builder(proto.building().info().address()).formatter(new IFormatter<IEntity, SafeHtml>() {

                    @Override
                    public SafeHtml format(IEntity value) {
                        SafeHtmlBuilder builder = new SafeHtmlBuilder();
                        if (value instanceof MaintenanceRequestDTO) {
                            InternationalAddress addr = ((MaintenanceRequestDTO) value).building().info().address();
                            builder.appendHtmlConstant(SimpleMessageFormat.format("{0,choice,null#|!null#{0} }{1}, {2}", //
                                    addr.streetNumber().getValue(), addr.streetName().getValue(), addr.city().getValue()));
                        }
                        return builder.toSafeHtml();
                    }
                }).searchable(false).build(),
                new ColumnDescriptor.Builder(proto.building().info().address().city()).searchableOnly().build(),
                new ColumnDescriptor.Builder(proto.unit()).build(),
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
                }).searchable(false).build(),
                new ColumnDescriptor.Builder(proto.priority()).build(),
                new ColumnDescriptor.Builder(proto.summary()).build(),
                new ColumnDescriptor.Builder(proto.reporterName()).columnTitle(i18n.tr("Tenant")).searchable(false).build(),
                new ColumnDescriptor.Builder(proto.reporterPhone(), false).build(),
                new ColumnDescriptor.Builder(proto.permissionToEnter()).columnTitle(i18n.tr("Entry Allowed")).build(),
                new ColumnDescriptor.Builder(proto.petInstructions()).visible(false).build(),
                new ColumnDescriptor.Builder(proto.submitted()).build(),
                new ColumnDescriptor.Builder(proto.status()).build(),
                new ColumnDescriptor.Builder(proto.updated()).build(),
                new ColumnDescriptor.Builder(proto.surveyResponse().rating()).build(),
                new ColumnDescriptor.Builder(proto.surveyResponse().description()).visible(false)
                        .columnTitle(proto.surveyResponse().getMeta().getCaption()).build() };
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().submitted(), true), new Sort(proto().updated(), false));
    }

    @Override
    protected void onItemNew() {
        new BuildingSelectionDialog() {
            @Override
            public boolean onClickOk() {
                MaintenanceCrudService.MaintenanceInitializationData id = EntityFactory.create(MaintenanceCrudService.MaintenanceInitializationData.class);
                id.building().set(getSelectedItem());
                editNew(getItemOpenPlaceClass(), id);
                return true;
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList( //
                        new ColumnDescriptor.Builder(proto().propertyCode()).build(), //
                        new ColumnDescriptor.Builder(proto().info().name()).build(), //
                        new ColumnDescriptor.Builder(proto().info().address()).width("50%").build(), //
                        new ColumnDescriptor.Builder(proto().info().address().streetNumber()).searchableOnly().build(), //
                        new ColumnDescriptor.Builder(proto().info().address().streetName()).searchableOnly().build(), //
                        new ColumnDescriptor.Builder(proto().info().address().city()).searchableOnly().build(), //
                        new ColumnDescriptor.Builder(proto().info().address().province()).searchableOnly().build(), //
                        new ColumnDescriptor.Builder(proto().info().address().country()).searchableOnly().build() //
                        );
            }
        }.show();
    }
}
