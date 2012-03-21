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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.crm.server.services.dashboard.gadgets.AvailabilityReportServiceImpl;
import com.propertyvista.crm.server.services.reports.AbstractGadgetReportModelCreator;
import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingLister;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability;
import com.propertyvista.dto.BuildingDTO;

public class UnitAvailabilityStatusReportCreator extends AbstractGadgetReportModelCreator<UnitAvailability> {

    private static final SimpleDateFormat REPORT_FORMAT = new SimpleDateFormat("yyyy-MMM-dd");

    public UnitAvailabilityStatusReportCreator() {
        super(UnitAvailability.class);
    }

    @Override
    protected void convert(final AsyncCallback<ConvertedGadgetMetadata> callback, GadgetMetadata gadgetMetadata, List<Key> selectedBuildings) {
        final UnitAvailability metadata = (UnitAvailability) gadgetMetadata;
        final LogicalDate asOf = metadata.customizeDate().isBooleanTrue() ? metadata.asOf().getValue() : new LogicalDate();

        AvailabilityReportService service = new AvailabilityReportServiceImpl();

        service.unitStatusList(new AsyncCallback<EntitySearchResult<UnitAvailabilityStatus>>() {
            @Override
            public void onSuccess(EntitySearchResult<UnitAvailabilityStatus> result) {

                // Create map of column properties to names
                // for columns that appear in the report
                HashMap<String, String> columns = new HashMap<String, String>();
                Persistence.service().retrieve(metadata.columnDescriptors());
                for (ColumnDescriptorEntity column : metadata.columnDescriptors()) {
                    if (column.visiblily().getValue())
                        columns.put(column.propertyPath().getValue(), column.title().getValue());
                }

                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("COLUMNS", columns);
                parameters.put("AS_OF", REPORT_FORMAT.format(asOf));

                callback.onSuccess(new ConvertedGadgetMetadata(result.getData(), parameters));
            }

            @Override
            public void onFailure(Throwable arg0) {
                callback.onFailure(arg0);
            }
        }, new Vector<Key>(selectedBuildings), metadata.defaultFilteringPreset().getValue(), asOf, new Vector<Sort>(), 0, Integer.MAX_VALUE);
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
