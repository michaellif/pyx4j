/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.movein;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.policies.domain.AgreementLegalTerm;
import com.propertyvista.domain.tenant.lease.AgreementDigitalSignatures;
import com.propertyvista.domain.tenant.lease.AgreementLegalTermSignature;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementDTO;
import com.propertyvista.portal.rpc.portal.resident.services.movein.LeaseSigningCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class LeaseSigningCrudServiceImpl implements LeaseSigningCrudService {

    @Override
    public void init(AsyncCallback<LeaseAgreementDTO> callback, InitializationData initializationData) {
        LeaseAgreementDTO to = EntityFactory.create(LeaseAgreementDTO.class);

        Lease lease = ResidentPortalContext.getLease();
        Persistence.service().retrieve(lease.unit().building());
        Persistence.service().retrieve(lease.unit().floorplan());
        Persistence.service().retrieve(lease.currentTerm().version().tenants());

        to.unit().set(lease.unit());
        to.leaseTerm().set(lease.currentTerm());

        for (AgreementLegalTerm term : lease.currentTerm().version().agreementLegalTerms()) {
            AgreementLegalTermSignature signedTerm = EntityFactory.create(AgreementLegalTermSignature.class);
            signedTerm.term().set(term);
            signedTerm.signature().signatureFormat().set(term.signatureFormat());
            to.legalTerms().add(signedTerm);
        }

        callback.onSuccess(to);
    }

    @Override
    public void create(AsyncCallback<Key> callback, LeaseAgreementDTO editableEntity) {
        AgreementDigitalSignatures agreementSignatures = EntityFactory.create(AgreementDigitalSignatures.class);
        agreementSignatures.leaseTermTenant().set(ResidentPortalContext.getLeaseTermTenant());
        agreementSignatures.legalTermsSignatures().addAll(editableEntity.legalTerms());
        Persistence.service().persist(agreementSignatures);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void save(AsyncCallback<Key> callback, LeaseAgreementDTO editableEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retrieve(AsyncCallback<LeaseAgreementDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<LeaseAgreementDTO>> callback, EntityListCriteria<LeaseAgreementDTO> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new UnsupportedOperationException();
    }

}
