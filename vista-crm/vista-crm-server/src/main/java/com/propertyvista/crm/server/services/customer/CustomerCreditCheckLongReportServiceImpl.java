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
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;
import com.propertyvista.crm.rpc.services.customer.CustomerCreditCheckLongReportService;
import com.propertyvista.domain.tenant.Customer;

public class CustomerCreditCheckLongReportServiceImpl implements CustomerCreditCheckLongReportService {

    @Override
    public void retrieve(AsyncCallback<CustomerCreditCheckLongReportDTO> callback, Key entityId, RetrieveTarget retrieveTarget) {
        if (retrieveTarget == RetrieveTarget.View) {
            callback.onSuccess(ServerSideFactory.create(ScreeningFacade.class).retriveLongReport(Persistence.service().retrieve(Customer.class, entityId)));
        } else {
            throw new Error("Not intended for Edit!");
        }
    }

    @Override
    public void init(AsyncCallback<CustomerCreditCheckLongReportDTO> callback, InitializationData initializationData) {
        throw new Error("Not intended for use!");
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
