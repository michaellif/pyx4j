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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.media.IdentificationDocument;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.dto.ApplicantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.CoapplicantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.GuarantorDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.OptionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public class ApplicationWizardServiceImpl implements ApplicationWizardService {

    public ApplicationWizardServiceImpl() {
    }

    @Override
    public void init(AsyncCallback<OnlineApplicationDTO> callback) {
        OnlineApplication bo = ProspectPortalContext.getOnlineApplication();

        Persistence.ensureRetrieveMember(bo.masterOnlineApplication(), AttachLevel.Attached);
        Persistence.ensureRetrieveMember(bo.masterOnlineApplication().leaseApplication().lease(), AttachLevel.Attached);
        Persistence.ensureRetrieveMember(bo.masterOnlineApplication().leaseApplication().lease().unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieveMember(bo.masterOnlineApplication().leaseApplication().lease().unit().floorplan(), AttachLevel.Attached);

        OnlineApplicationDTO to = EntityFactory.create(OnlineApplicationDTO.class);

        to.unit().set(createUnitTO(bo.masterOnlineApplication().leaseApplication().lease().unit()));
        to.utilities().setValue(retrieveUtilities(bo.masterOnlineApplication().leaseApplication().lease().unit()));

        fillLeaseData(bo, to);

        fillApplicantData(bo, to);

        fillCoApplicants(bo, to);
        fillGuarantors(bo, to);

        fillUnitSelectionData(bo, to);

        callback.onSuccess(to);
    }

    @Override
    public void save(AsyncCallback<Key> callback, OnlineApplicationDTO editableEntity) {
        callback.onSuccess(null);
    }

    @Override
    public void submit(AsyncCallback<Key> callback, OnlineApplicationDTO editableEntity) {
        callback.onSuccess(null);
    }

    @Override
    public void getAvailableUnits(AsyncCallback<Vector<AptUnit>> callback, Floorplan floorplan, LogicalDate moveIn) {
        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        criteria.eq(criteria.proto().floorplan(), floorplan);
        criteria.ge(criteria.proto()._availableForRent(), moveIn);

        callback.onSuccess(new Vector<AptUnit>(Persistence.service().query(criteria)));
    }

    @Override
    public void getAvailableUnitOptions(AsyncCallback<UnitOptionsSelectionDTO> callback, AptUnit unit) {
        callback.onSuccess(retriveAvailableUnitOptions(unit));
    }

    // internals: -----------------------------------------------------------------------------------------------------

    private AptUnit createUnitTO(AptUnit unit) {
        assert (!unit.building().isValueDetached());
        assert (!unit.floorplan().isValueDetached());

        AptUnit to = new EntityBinder<AptUnit, AptUnit>(AptUnit.class, AptUnit.class) {
            @Override
            protected void bind() {
                bind(toProto.id(), boProto.id());
                bind(toProto.info().number(), boProto.info().number());

                bind(toProto.building().id(), boProto.building().id());
                bind(toProto.building().info().address(), boProto.building().info().address());

                bind(toProto.floorplan().id(), boProto.floorplan().id());
                bind(toProto.floorplan().name(), boProto.floorplan().name());
                bind(toProto.floorplan().marketingName(), boProto.floorplan().marketingName());
            }
        }.createTO(unit);

        return to;
    }

    private String retrieveUtilities(AptUnit unit) {
        assert (!unit.building().isValueDetached());

        Persistence.ensureRetrieveMember(unit.building().utilities(), AttachLevel.ToStringMembers);

        String res = new String();
        for (BuildingUtility utility : unit.building().utilities()) {
            if (!res.isEmpty()) {
                res += ";";
            }
            res += utility.getStringView();
        }

        return res;
    }

    private void fillLeaseData(OnlineApplication bo, OnlineApplicationDTO to) {
        assert (!bo.masterOnlineApplication().leaseApplication().lease().isValueDetached());

        LeaseTerm term = Persistence.retrieveDraftForEdit(LeaseTerm.class, bo.masterOnlineApplication().leaseApplication().lease().currentTerm()
                .getPrimaryKey());

        to.leaseFrom().setValue(bo.masterOnlineApplication().leaseApplication().lease().leaseFrom().getValue());
        to.leaseTo().setValue(bo.masterOnlineApplication().leaseApplication().lease().leaseTo().getValue());

        to.leasePrice().setValue(term.version().leaseProducts().serviceItem().agreedPrice().getValue());

        for (BillableItem bi : term.version().leaseProducts().featureItems()) {
            OptionDTO oto = EntityFactory.create(OptionDTO.class);

            oto.item().set(bi.item());
            oto.price().setValue(bi.agreedPrice().getValue());

            to.options().add(oto);
        }
    }

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
            to.applicant().documents().set(tenant.effectiveScreening().documents());

            to.applicant().currentAddress().set(tenant.effectiveScreening().version().currentAddress());
            to.applicant().previousAddress().set(tenant.effectiveScreening().version().previousAddress());

            to.applicant().emergencyContacts().set(tenant.leaseParticipant().customer().emergencyContacts());
            to.applicant().legalQuestions().set(tenant.effectiveScreening().version().legalQuestions());

            to.applicant().incomes().set(tenant.effectiveScreening().version().incomes());
            to.applicant().assets().set(tenant.effectiveScreening().version().assets());
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

    private void fillCoApplicants(OnlineApplication bo, OnlineApplicationDTO to) {
        EntityQueryCriteria<LeaseTermTenant> criteria = new EntityQueryCriteria<LeaseTermTenant>(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseTermV().holder(), bo.masterOnlineApplication().leaseApplication().lease().currentTerm());
        criteria.ne(criteria.proto().leaseParticipant().customer().user(), ProspectPortalContext.getCustomerUserIdStub());

        for (LeaseTermTenant ltt : Persistence.service().query(criteria)) {
            CoapplicantDTO cant = EntityFactory.create(CoapplicantDTO.class);

            cant.dependent().setValue(ltt.role().getValue() == Role.Dependent);

            cant.firstName().setValue(ltt.leaseParticipant().customer().person().name().firstName().getValue());
            cant.lastName().setValue(ltt.leaseParticipant().customer().person().name().lastName().getValue());

            cant.email().setValue(ltt.leaseParticipant().customer().person().email().getValue());

            to.coapplicants().add(cant);
        }
    }

    private void fillGuarantors(OnlineApplication bo, OnlineApplicationDTO to) {
        EntityQueryCriteria<LeaseTermGuarantor> criteria = new EntityQueryCriteria<LeaseTermGuarantor>(LeaseTermGuarantor.class);
        criteria.eq(criteria.proto().leaseTermV().holder(), bo.masterOnlineApplication().leaseApplication().lease().currentTerm());
        criteria.ne(criteria.proto().leaseParticipant().customer().user(), ProspectPortalContext.getCustomerUserIdStub());

        for (LeaseTermGuarantor ltt : Persistence.service().query(criteria)) {
            GuarantorDTO grnt = EntityFactory.create(GuarantorDTO.class);

            grnt.firstName().setValue(ltt.leaseParticipant().customer().person().name().firstName().getValue());
            grnt.lastName().setValue(ltt.leaseParticipant().customer().person().name().lastName().getValue());

            grnt.email().setValue(ltt.leaseParticipant().customer().person().email().getValue());

            to.guarantors().add(grnt);
        }
    }

    private void fillUnitSelectionData(OnlineApplication bo, OnlineApplicationDTO to) {
        // TODO Auto-generated method stub

    }

    private UnitOptionsSelectionDTO retriveAvailableUnitOptions(AptUnit unit) {
        UnitOptionsSelectionDTO options = EntityFactory.create(UnitOptionsSelectionDTO.class);

        options.unit().set(unit);
        options.restrictions().set(retriveUnitOptionRestrictions(unit));
        retrieveAvailableCatalogItems(options);

        return options;
    }

    private UnitOptionsSelectionDTO.Restrictions retriveUnitOptionRestrictions(AptUnit unit) {
        UnitOptionsSelectionDTO.Restrictions restrictions = EntityFactory.create(UnitOptionsSelectionDTO.Restrictions.class);
        RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(unit, RestrictionsPolicy.class);

        restrictions.maxLockers().setValue(restrictionsPolicy.maxLockers().getValue());
        restrictions.maxParkingSpots().setValue(restrictionsPolicy.maxParkingSpots().getValue());
        restrictions.maxPets().setValue(restrictionsPolicy.maxPets().getValue());

        return restrictions;
    }

    private void retrieveAvailableCatalogItems(UnitOptionsSelectionDTO options) {
        assert (!options.unit().isNull());

    }
}
