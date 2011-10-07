/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.lister.EntityLister;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitVacancyReportService;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport.VacancyStatus;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportSummaryDTO;

public class UnitVacancyReportServiceImpl implements UnitVacancyReportService {

    @Override
    public void list(AsyncCallback<EntitySearchResult<UnitVacancyReport>> callback, EntityListCriteria<UnitVacancyReport> criteria) {
        // TODO fetch and calculate summary/change interface/add another RPC method for that

        EntitySearchResult<UnitVacancyReport> unitResult = EntityLister.secureQuery(criteria);

        // fetch and calculate rentDeltaAbsolute, rentDeltaRelative, daysVacant,  revenueLost
        for (UnitVacancyReport unit : unitResult.getData()) {
            unit.rentDeltaAbsolute().setValue(unit.unitRent().getValue() - unit.marketRent().getValue());
            unit.rentDeltaRelative().setValue(100 * (unit.unitRent().getValue() - unit.marketRent().getValue()) / unit.marketRent().getValue());

            if (VacancyStatus.Vacant.equals(unit.vacancyStatus().getValue()) & unit.moveOutDay().getValue() != null) {
                long availableFrom = 1 + unit.moveOutDay().getValue().getTime();
                long now = (new Date()).getTime();
                long millisecondsVacant = now - availableFrom;
                int daysVacant = (int) (millisecondsVacant / (1000 * 60 * 60 * 24)); // some really heavy math :)
                unit.daysVacant().setValue(daysVacant);

                double revenueLost = daysVacant * unit.marketRent().getValue() / 30.0;
                unit.revenueLost().setValue(revenueLost);
            }
        }

        callback.onSuccess(unitResult);
    }

    @Override
    public void getSummary(AsyncCallback<UnitVacancyReportSummaryDTO> callback, EntityListCriteria<UnitVacancyReport> criteria) {
        EntitySearchResult<UnitVacancyReport> unitResult = EntityLister.secureQuery(criteria);

    }

    @Override
    public void create(AsyncCallback<UnitVacancyReport> callback, UnitVacancyReport editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retrieve(AsyncCallback<UnitVacancyReport> callback, Key entityId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void save(AsyncCallback<UnitVacancyReport> callback, UnitVacancyReport editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // TODO Auto-generated method stub

    }
}
