/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitAvailabilitySummaryGadgetService;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatusSummaryLineDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatusSummaryLineDTO.AvailabilityCategory;
import com.propertyvista.domain.property.asset.building.Building;

public class UnitAvailabilitySummaryGadgetServiceImpl implements UnitAvailabilitySummaryGadgetService {

    @Override
    public void summary(AsyncCallback<Vector<UnitAvailabilityStatusSummaryLineDTO>> callback, Vector<Building> buildingsFilter, LogicalDate asOf) {
        Vector<UnitAvailabilityStatusSummaryLineDTO> summary = new Vector<UnitAvailabilityStatusSummaryLineDTO>();

        EntityQueryCriteria<UnitAvailabilityStatus> totalCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        Persistence.applyDatasetAccessRule(totalCriteria);
        int total = Persistence.service().count(totalCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> occupiedCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        occupiedCriteria.add(PropertyCriterion.eq(occupiedCriteria.proto().vacancyStatus(), (Vacancy) null));
        Persistence.applyDatasetAccessRule(occupiedCriteria);
        int occupied = Persistence.service().count(occupiedCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> vacantCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        vacantCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Vacant));
        Persistence.applyDatasetAccessRule(vacantCriteria);
        int vacant = Persistence.service().count(vacantCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> vacantRentedCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        vacantRentedCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Vacant));
        vacantRentedCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().rentedStatus(), RentedStatus.Rented));
        Persistence.applyDatasetAccessRule(vacantRentedCriteria);
        int vacantRented = Persistence.service().count(vacantRentedCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> noticeCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        noticeCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Notice));
        Persistence.applyDatasetAccessRule(noticeCriteria);
        int notice = Persistence.service().count(noticeCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> noticeRentedCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        noticeRentedCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Notice));
        noticeRentedCriteria.add(PropertyCriterion.eq(vacantCriteria.proto().rentedStatus(), RentedStatus.Rented));
        Persistence.applyDatasetAccessRule(noticeRentedCriteria);
        int noticeRented = Persistence.service().count(noticeRentedCriteria);

        EntityQueryCriteria<UnitAvailabilityStatus> netExposureCriteria = makeAvaiabilityStatusCriteria(buildingsFilter, asOf);
        netExposureCriteria.or(PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Vacant),
                PropertyCriterion.eq(vacantCriteria.proto().vacancyStatus(), Vacancy.Notice));
        netExposureCriteria.add(PropertyCriterion.ne(vacantCriteria.proto().rentedStatus(), RentedStatus.Rented));
        Persistence.applyDatasetAccessRule(netExposureCriteria);
        int netExposure = Persistence.service().count(netExposureCriteria);

        summary.add(makeSummaryRecord(AvailabilityCategory.total, total, 1d));
        summary.add(makeSummaryRecord(AvailabilityCategory.occupied, occupied, percentile(occupied, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.vacant, vacant, percentile(vacant, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.vacantRented, vacantRented, percentile(vacantRented, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.notice, notice, percentile(notice, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.noticeRented, noticeRented, percentile(noticeRented, total)));
        summary.add(makeSummaryRecord(AvailabilityCategory.netExposure, netExposure, percentile(netExposure, total)));

        callback.onSuccess(summary);
    }

    private static UnitAvailabilityStatusSummaryLineDTO makeSummaryRecord(UnitAvailabilityStatusSummaryLineDTO.AvailabilityCategory category, int units,
            double percentile) {
        UnitAvailabilityStatusSummaryLineDTO summaryRecord = EntityFactory.create(UnitAvailabilityStatusSummaryLineDTO.class);
        summaryRecord.category().setValue(category);
        summaryRecord.units().setValue(units);
        summaryRecord.percentage().setValue(percentile);
        return summaryRecord;
    }

    private static double percentile(int part, int total) {
        return total != 0 ? ((double) part) / ((double) total) : 0d;
    }

    private static EntityQueryCriteria<UnitAvailabilityStatus> makeAvaiabilityStatusCriteria(Vector<Building> buildingsFilter, LogicalDate asOf) {
        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        if (!buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildingsFilter));
        }
        criteria.add(PropertyCriterion.le(criteria.proto().statusFrom(), asOf));
        criteria.add(PropertyCriterion.ge(criteria.proto().statusUntil(), asOf));
        return criteria;
    }

}
