/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueElementType;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.portal.rpc.portal.dto.MaintananceDTO;
import com.propertyvista.portal.rpc.portal.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.services.TenantMaintenanceService;

public class TenantMaintenanceServiceImpl implements TenantMaintenanceService {

    @Override
    public void listOpenIssues(AsyncCallback<Vector<MaintananceDTO>> callback) {
        Vector<MaintananceDTO> dto = new Vector<MaintananceDTO>();
        dto.addAll(TenantMaintenanceDAO.getOpenIssues());
        callback.onSuccess(dto);
    }

    @Override
    public void listHistoryIssues(AsyncCallback<Vector<MaintananceDTO>> callback) {
        Vector<MaintananceDTO> dto = new Vector<MaintananceDTO>();
        dto.addAll(TenantMaintenanceDAO.getHistoryIssues());
        callback.onSuccess(dto);
    }

    @Override
    public void createNewTicket(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO request) {
        callback.onSuccess(null);
    }

    // -------------

    @Override
    public void listIssueElements(AsyncCallback<Vector<IssueElement>> callback) {
        Vector<IssueElement> dto = new Vector<IssueElement>();

        {
            EntityQueryCriteria<IssueElement> criteria = EntityQueryCriteria.create(IssueElement.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), IssueElementType.ApartmentUnit));
            List<IssueElement> elements = Persistence.service().query(criteria);
            dto.addAll(elements);
        }

        callback.onSuccess(dto);

    }

    @Override
    public void getIssueRepairSubject(AsyncCallback<IssueElement> callback, IssueElement issueElement) {
        issueElement.subjects().clear();

        {
            EntityQueryCriteria<IssueRepairSubject> criteria = EntityQueryCriteria.create(IssueRepairSubject.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().issueElement(), issueElement));
            issueElement.subjects().addAll(Persistence.service().query(criteria));
        }

        callback.onSuccess(issueElement);
    }

    @Override
    public void getIssueSubjectDetails(AsyncCallback<IssueRepairSubject> callback, IssueRepairSubject issueRepairSubject) {
        issueRepairSubject.details().clear();

        {
            EntityQueryCriteria<IssueSubjectDetails> criteria = EntityQueryCriteria.create(IssueSubjectDetails.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().subject(), issueRepairSubject));
            issueRepairSubject.details().addAll(Persistence.service().query(criteria));
        }

        callback.onSuccess(issueRepairSubject);
    }

    @Override
    public void getIssueClassification(AsyncCallback<IssueSubjectDetails> callback, IssueSubjectDetails issueSubjectDetails) {
        issueSubjectDetails.classifications().clear();

        {
            EntityQueryCriteria<IssueClassification> criteria = EntityQueryCriteria.create(IssueClassification.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().subjectDetails(), issueSubjectDetails));
            issueSubjectDetails.classifications().addAll(Persistence.service().query(criteria));
        }

        callback.onSuccess(issueSubjectDetails);

    }

}
