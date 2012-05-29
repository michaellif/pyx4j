/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.directory;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.crm.server.services.building.BuildingCrudServiceImpl;
import com.propertyvista.crm.server.services.reports.AbstractGadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.util.DynamicColumnWidthReportTableTemplateBuilder;
import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingLister;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerReportCreator extends AbstractGadgetReportModelCreator<BuildingLister> {

    private final static I18n i18n = I18n.get(BuildingListerReportCreator.class);

    private BuildingLister listerMetadata;

    public BuildingListerReportCreator() {
        super(BuildingLister.class);
    }

    @Override
    protected void convert(final AsyncCallback<ConvertedGadgetMetadata> callback, GadgetMetadata gadgetMetadata, List<Key> selectedBuildings) {
        listerMetadata = (BuildingLister) gadgetMetadata;

        BuildingCrudService service = new BuildingCrudServiceImpl();

        service.list(new AsyncCallback<EntitySearchResult<BuildingDTO>>() {

            @Override
            public void onSuccess(EntitySearchResult<BuildingDTO> result) {

                // Create map of column properties to names
                // for columns that appear in the report
                HashMap<String, String> columns = new HashMap<String, String>();
                for (ColumnDescriptorEntity column : listerMetadata.columnDescriptors()) {
                    if (column.isVisible().getValue())
                        columns.put(column.propertyPath().getValue(), column.title().getValue());
                }

                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("COLUMNS", columns);
                parameters.put("TITLE", i18n.tr("Buildings"));

                callback.onSuccess(new ConvertedGadgetMetadata(result.getData(), parameters));
            }

            @Override
            public void onFailure(Throwable arg0) {
                callback.onFailure(arg0);
            }
        }, convertToCriteria(gadgetMetadata.duplicate(BuildingLister.class)));
    }

    @Override
    protected String design() {
//        return new ReportTableTemplateBuilder(EntityFactory.create(BuildingDTO.class), listerMetadata).generateReportTemplate();
        return new DynamicColumnWidthReportTableTemplateBuilder(EntityFactory.getEntityPrototype(BuildingDTO.class), listerMetadata).build();
    }

    @Override
    protected String designName() {
        return "" + System.currentTimeMillis() + "-" + super.designName();
    }

    private EntityListCriteria<BuildingDTO> convertToCriteria(BuildingLister metadata) {
        EntityListCriteria<BuildingDTO> criteria = new EntityListCriteria<BuildingDTO>(BuildingDTO.class);
        String sortCoulmn = metadata.primarySortColumn().propertyPath().getValue();
        if (sortCoulmn != null) {
            criteria.sort(new Sort(sortCoulmn, !metadata.sortAscending().isBooleanTrue()));
        }
        return criteria;
    }
}
