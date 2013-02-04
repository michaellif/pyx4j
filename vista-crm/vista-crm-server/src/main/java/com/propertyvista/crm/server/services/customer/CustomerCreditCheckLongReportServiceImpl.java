/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-1-21
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;
import com.propertyvista.crm.rpc.services.customer.CustomerCreditCheckLongReportService;

public class CustomerCreditCheckLongReportServiceImpl implements CustomerCreditCheckLongReportService {

    public CustomerCreditCheckLongReportServiceImpl() {
    }

    @Override
    public void retrieve(AsyncCallback<CustomerCreditCheckLongReportDTO> callback, Key entityId, RetrieveTraget retrieveTraget) {
        if (retrieveTraget == RetrieveTraget.View) {
//            callback.onSuccess(ServerSideFactory.create(ScreeningFacade.class).retriveLongReport(Persistence.service().retrieve(Customer.class, entityId)));
            CustomerCreditCheckLongReportDTO report = EntityFactory.create(CustomerCreditCheckLongReportDTO.class);
            report.setPrimaryKey(entityId);

            callback.onSuccess(report);
        } else {
            throw new Error("Not intended for Edit!");
        }
    }

    @Override
    public void create(AsyncCallback<Key> callback, CustomerCreditCheckLongReportDTO editableEntity) {
        throw new Error("Not intended for use!");
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, CustomerCreditCheckLongReportDTO editableEntity) {
        throw new Error("Not intended for use!");
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<CustomerCreditCheckLongReportDTO>> callback, EntityListCriteria<CustomerCreditCheckLongReportDTO> criteria) {
        throw new Error("Not intended for use!");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error("Not intended for use!");
    }
}
