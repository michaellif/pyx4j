/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.media.IdentificationDocument;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.dto.ApplicantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public class ApplicationWizardServiceImpl implements ApplicationWizardService {

    public ApplicationWizardServiceImpl() {

    }

    @Override
    public void init(AsyncCallback<OnlineApplicationDTO> callback) {
        OnlineApplication bo = ProspectPortalContext.getOnlineApplication();

        Persistence.service().retrieve(bo.masterOnlineApplication());
        Persistence.service().retrieve(bo.masterOnlineApplication().leaseApplication().lease());
        Persistence.service().retrieve(bo.masterOnlineApplication().leaseApplication().lease().unit().building());
        Persistence.service().retrieve(bo.masterOnlineApplication().leaseApplication().lease().unit().floorplan());

        assert bo.masterOnlineApplication().leaseApplication().lease().currentTerm().version().isNull();
        LeaseTerm term = Persistence.retrieveDraftForEdit(LeaseTerm.class, bo.masterOnlineApplication().leaseApplication().lease().currentTerm()
                .getPrimaryKey());

        OnlineApplicationDTO to = EntityFactory.create(OnlineApplicationDTO.class);
        to.unit().set(createUnitTO(bo.masterOnlineApplication().leaseApplication().lease().unit()));
        to.leaseFrom().setValue(bo.masterOnlineApplication().leaseApplication().lease().leaseFrom().getValue());
        to.leaseTo().setValue(bo.masterOnlineApplication().leaseApplication().lease().leaseTo().getValue());
        to.leasePrice().set(term.version().leaseProducts().serviceItem().agreedPrice());

        fillApplicantData(bo, to);

        callback.onSuccess(to);
    }

    //TODO use binders
    private AptUnit createUnitTO(AptUnit unit) {
        AptUnit to = EntityFactory.create(AptUnit.class);
        to.id().set(unit.id());
        to.info().number().set(unit.info().number());

        to.building().id().set(unit.building().id());
        to.building().info().address().set(unit.building().info().address().duplicate());

        to.floorplan().id().set(unit.floorplan().id());
        to.floorplan().name().set(unit.floorplan().name());
        to.floorplan().marketingName().set(unit.floorplan().marketingName());
        return to;
    }

    @Override
    public void save(AsyncCallback<Key> callback, OnlineApplicationDTO editableEntity) {
        callback.onSuccess(null);
    }

    @Override
    public void submit(AsyncCallback<Key> callback, OnlineApplicationDTO editableEntity) {
        callback.onSuccess(null);
    }

    // internals:
    private void fillApplicantData(OnlineApplication bo, OnlineApplicationDTO to) {
        to.applicant().set(EntityFactory.create(ApplicantDTO.class));

        switch (bo.role().getValue()) {
        case Applicant:
        case CoApplicant:
            LeaseTermTenant tenant = ProspectPortalContext.getLeaseTermTenant();
            Persistence.service().retrieve(tenant.leaseParticipant().customer().emergencyContacts());
            LeaseParticipantUtils.retrieveLeaseTermEffectiveScreening(bo.masterOnlineApplication().leaseApplication().lease(), tenant, AttachLevel.Attached);

            to.applicant().person().set(tenant.leaseParticipant().customer().person());

            to.applicant().picture().set(tenant.leaseParticipant().customer().picture());

            to.applicant().emergencyContacts().set(tenant.leaseParticipant().customer().emergencyContacts());

            to.applicant().currentAddress().set(tenant.effectiveScreening().version().currentAddress());
            to.applicant().previousAddress().set(tenant.effectiveScreening().version().previousAddress());
            to.applicant().legalQuestions().set(tenant.effectiveScreening().version().legalQuestions());

            to.applicant().incomes().set(tenant.effectiveScreening().version().incomes());
            to.applicant().assets().set(tenant.effectiveScreening().version().assets());

            to.applicant().documents().set(tenant.effectiveScreening().documents());
            break;

        case Guarantor:
            break;
        }

        // TO optimizations
        to.applicant().picture().customer().setAttachLevel(AttachLevel.IdOnly);
        for (IdentificationDocument i : to.applicant().documents()) {
            i.owner().setAttachLevel(AttachLevel.IdOnly);
        }
        for (EmergencyContact i : to.applicant().emergencyContacts()) {
            i.customer().setAttachLevel(AttachLevel.IdOnly);
        }
        for (CustomerScreeningIncome i : to.applicant().incomes()) {
            i.owner().setAttachLevel(AttachLevel.IdOnly);
        }
        for (CustomerScreeningPersonalAsset i : to.applicant().assets()) {
            i.owner().setAttachLevel(AttachLevel.IdOnly);
        }
    }
}
