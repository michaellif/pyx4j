/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.domain.pt.ApplicationDocument.DocumentType;
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.rpc.pt.ApplicationDocumentsList;
import com.propertyvista.portal.rpc.pt.services.ApplicationDocumentsService;
import com.propertyvista.portal.server.pt.PtAppContext;
import com.pyx4j.entity.server.PersistenceServicesFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationDocumentsServiceImpl extends ApplicationEntityServicesImpl implements ApplicationDocumentsService {
    private final static Logger log = LoggerFactory.getLogger(ApplicationDocumentsServiceImpl.class);

    @Override
    public void retrieveAttachments(AsyncCallback<ApplicationDocumentsList> callback, Long tenantId, DocumentType documentType) {
        log.info("Retrieving attachments for tenant {} and docType {}", tenantId, documentType);
        EntityQueryCriteria<ApplicationDocument> criteria = EntityQueryCriteria.create(ApplicationDocument.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenantId));
        criteria.add(PropertyCriterion.eq(criteria.proto().type(), documentType));
        List<ApplicationDocument> applicationDocuments = secureQuery(criteria);
        ApplicationDocumentsList applicationDocumentsList = EntityFactory.create(ApplicationDocumentsList.class);
        if (applicationDocuments != null) {
            applicationDocumentsList.documents().addAll(applicationDocuments);
        }
        callback.onSuccess(applicationDocumentsList);
    }

    @Override
    public void removeAttachment(AsyncCallback<VoidSerializable> callback, Long applicationDocumentId) {
        log.info("Remove attachments for applicationDoc {}", applicationDocumentId);
        //EntityQueryCriteria<ApplicationDocument> criteria = EntityQueryCriteria.create(ApplicationDocument.class);
        //criteria.add(PropertyCriterion.eq(criteria.proto().id(), applicationDocumentId));
        //EntityQueryCriteria<ApplicationDocument> criteria = EntityQueryCriteria.create(ApplicationDocument.class);
        //criteria.add(PropertyCriterion.eq(criteria.proto().id(), applicationDocumentId));
        ApplicationDocument applicationDocument = PersistenceServicesFactory.getPersistenceService().retrieve(ApplicationDocument.class, applicationDocumentId);
        if (DocumentType.income.equals(applicationDocument.type().getValue())) {
            TenantIncome income = PersistenceServicesFactory.getPersistenceService().retrieve(TenantIncome.class, applicationDocument.tenant().id().getValue());
            income.documents().remove(applicationDocument);
            PersistenceServicesFactory.getPersistenceService().merge(income);
        }
        PersistenceServicesFactory.getPersistenceService().delete(ApplicationDocument.class, applicationDocumentId);
        callback.onSuccess(null);
    }

}
