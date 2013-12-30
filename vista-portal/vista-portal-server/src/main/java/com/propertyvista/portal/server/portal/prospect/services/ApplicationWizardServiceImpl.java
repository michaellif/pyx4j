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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.dto.ApplicantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.CoapplicantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.GuarantorDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.OptionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;
import com.propertyvista.server.common.util.LeaseParticipantUtils;
import com.propertyvista.shared.config.VistaFeatures;

public class ApplicationWizardServiceImpl implements ApplicationWizardService {

    public ApplicationWizardServiceImpl() {
    }

    @Override
    public void init(AsyncCallback<OnlineApplicationDTO> callback) {
        OnlineApplication bo = ProspectPortalContext.getOnlineApplication();

        Persistence.ensureRetrieve(bo.masterOnlineApplication(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.masterOnlineApplication().leaseApplication().lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.masterOnlineApplication().leaseApplication().lease().unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.masterOnlineApplication().leaseApplication().lease().unit().floorplan(), AttachLevel.Attached);

        OnlineApplicationDTO to = EntityFactory.create(OnlineApplicationDTO.class);

        fillLeaseData(bo, to);

        fillUnitSelectionData(bo, to);

        fillApplicantData(bo, to);

        fillCoApplicants(bo, to);
        fillGuarantors(bo, to);

        fillLegalTerms(bo, to);

        callback.onSuccess(to);
    }

    @Override
    public void save(AsyncCallback<Key> callback, OnlineApplicationDTO editableEntity) {
        saveApplicationData(editableEntity, false);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void submit(AsyncCallback<Key> callback, OnlineApplicationDTO editableEntity) {
        saveApplicationData(editableEntity, true);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void getAvailableUnits(AsyncCallback<Vector<AptUnit>> callback, Floorplan floorplanId, LogicalDate moveIn) {
        callback.onSuccess(new Vector<AptUnit>(retriveAvailableUnits(floorplanId, moveIn)));
    }

    @Override
    public void getAvailableUnitOptions(AsyncCallback<UnitOptionsSelectionDTO> callback, AptUnit unitId) {
        callback.onSuccess(retriveAvailableUnitOptions(Persistence.service().retrieve(AptUnit.class, unitId.getPrimaryKey())));
    }

    // internals: -----------------------------------------------------------------------------------------------------

    private void fillLeaseData(OnlineApplication bo, OnlineApplicationDTO to) {
        assert (!bo.masterOnlineApplication().leaseApplication().lease().isValueDetached());

        LeaseTerm term = Persistence.retrieveDraftForEdit(LeaseTerm.class, bo.masterOnlineApplication().leaseApplication().lease().currentTerm()
                .getPrimaryKey());

        to.unit().set(createUnitTO(bo.masterOnlineApplication().leaseApplication().lease().unit()));
        to.utilities().setValue(retrieveUtilities(bo.masterOnlineApplication().leaseApplication().lease().unit()));

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

            // customer:
            Customer customer = tenant.leaseParticipant().customer();
            Persistence.service().retrieve(customer.picture());
            Persistence.service().retrieve(customer.emergencyContacts());
            //
            to.applicant().set(to.applicant().person(), customer.person());
            to.applicant().set(to.applicant().picture(), customer.picture());
            to.applicant().set(to.applicant().emergencyContacts(), customer.emergencyContacts());

            // screening:
            LeaseParticipantUtils.retrieveLeaseTermEffectiveScreening(bo.masterOnlineApplication().leaseApplication().lease(), tenant, AttachLevel.Attached);
            CustomerScreening.CustomerScreeningV screening = tenant.effectiveScreening().version();
            //
            to.applicant().set(to.applicant().currentAddress(), screening.currentAddress());
            to.applicant().set(to.applicant().previousAddress(), screening.previousAddress());

            to.applicant().set(to.applicant().documents(), screening.documents());
            to.applicant().set(
                    to.applicant().documentsPolicy(),
                    ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(getPolicyNode(bo.masterOnlineApplication()),
                            ApplicationDocumentationPolicy.class));
            initializeRequiredDocuments(to.applicant());

            to.applicant().set(to.applicant().legalQuestions(), screening.legalQuestions());

            to.applicant().set(to.applicant().incomes(), screening.incomes());
            to.applicant().set(to.applicant().assets(), screening.assets());
            break;

        case Guarantor:
            // TODO process guarantor case here...
            break;
        }

        // TO optimizations
        to.applicant().picture().customer().setAttachLevel(AttachLevel.IdOnly);
        for (IdentificationDocumentFolder i : to.applicant().documents()) {
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

    private void saveApplicantData(OnlineApplication bo, OnlineApplicationDTO to) {
        switch (bo.role().getValue()) {
        case Applicant:
        case CoApplicant:
            LeaseTermTenant tenant = ProspectPortalContext.getLeaseTermTenant();

            // customer:
            Customer customer = tenant.leaseParticipant().customer();
            Persistence.service().retrieve(customer.picture());
            Persistence.service().retrieve(customer.emergencyContacts());
            //
            customer.set(customer.person(), to.applicant().person());
            customer.set(customer.picture(), to.applicant().picture());

            customer.emergencyContacts().clear();
            customer.emergencyContacts().addAll(to.applicant().emergencyContacts());

            Persistence.service().merge(customer);

            // screening:
            LeaseParticipantUtils.retrieveLeaseTermEffectiveScreening(bo.masterOnlineApplication().leaseApplication().lease(), tenant, AttachLevel.Attached);
            CustomerScreening.CustomerScreeningV screening = tenant.effectiveScreening().version();
            //
            screening.set(screening.currentAddress(), to.applicant().currentAddress());
            screening.set(screening.previousAddress(), to.applicant().previousAddress());
            screening.set(screening.legalQuestions(), to.applicant().legalQuestions());

            screening.documents().clear();
            screening.documents().addAll(to.applicant().documents());

            screening.incomes().clear();
            screening.incomes().addAll(to.applicant().incomes());

            screening.assets().clear();
            screening.assets().addAll(to.applicant().assets());

            Persistence.service().merge(screening);
            break;

        case Guarantor:
            // TODO process guarantor case here...
            break;
        }
    }

    private void fillCoApplicants(OnlineApplication bo, OnlineApplicationDTO to) {
        EntityQueryCriteria<LeaseTermTenant> criteria = new EntityQueryCriteria<LeaseTermTenant>(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseTermV().holder(), bo.masterOnlineApplication().leaseApplication().lease().currentTerm());
        criteria.ne(criteria.proto().leaseParticipant().customer().user(), ProspectPortalContext.getCustomerUserIdStub());

        for (LeaseTermTenant ltt : Persistence.service().query(criteria)) {
            CoapplicantDTO cap = EntityFactory.create(CoapplicantDTO.class);

            cap.dependent().setValue(ltt.role().getValue() == Role.Dependent);

            cap.firstName().setValue(ltt.leaseParticipant().customer().person().name().firstName().getValue());
            cap.lastName().setValue(ltt.leaseParticipant().customer().person().name().lastName().getValue());

            cap.email().setValue(ltt.leaseParticipant().customer().person().email().getValue());

            // remember corresponding tenant: 
            cap.set(cap.tenantId(), ltt);
            cap.tenantId().setAttachLevel(AttachLevel.IdOnly);

            to.coapplicants().add(cap);
        }
    }

    private void saveCoApplicants(OnlineApplication bo, OnlineApplicationDTO to) {
        LeaseTerm leaseTerm = bo.masterOnlineApplication().leaseApplication().lease().currentTerm();

        // clear removed:
        Iterator<LeaseTermTenant> it = leaseTerm.version().tenants().iterator();
        while (it.hasNext()) {
            Boolean present = false;
            LeaseTermTenant ltt = it.next();
            Persistence.ensureRetrieve(ltt, AttachLevel.Attached);
            if (!ltt.leaseParticipant().customer().user().equals(ProspectPortalContext.getCustomerUserIdStub())) {
                for (CoapplicantDTO cap : to.coapplicants()) {
                    if (!cap.tenantId().isNull() && cap.tenantId().getPrimaryKey().equals(ltt.getPrimaryKey())) {
                        present = true;
                        break;
                    }
                }
                if (!present) {
                    it.remove();
                }
            }
        }

        // add/update the rest:
        for (CoapplicantDTO cap : to.coapplicants()) {
            if (cap.tenantId().isNull()) {
                // create new:
                LeaseTermTenant ltt = EntityFactory.create(LeaseTermTenant.class);
                updateCoApplicant(ltt, cap);
                leaseTerm.version().tenants().add(ltt);
            } else {
                // update current:
                for (LeaseTermTenant ltt : leaseTerm.version().tenants()) {
                    if (ltt.getPrimaryKey().equals(cap.tenantId().getPrimaryKey())) {
                        Persistence.ensureRetrieve(ltt, AttachLevel.Attached);
                        updateCoApplicant(ltt, cap);
                        break;
                    }
                }
            }
        }
    }

    private void updateCoApplicant(LeaseTermTenant ltt, CoapplicantDTO cap) {
        ltt.role().setValue(cap.dependent().isBooleanTrue() ? Role.Dependent : Role.CoApplicant);

        ltt.leaseParticipant().customer().person().name().firstName().setValue(cap.firstName().getValue());
        ltt.leaseParticipant().customer().person().name().lastName().setValue(cap.lastName().getValue());

        ltt.leaseParticipant().customer().person().email().setValue(cap.email().getValue());
    }

    private void fillGuarantors(OnlineApplication bo, OnlineApplicationDTO to) {
        EntityQueryCriteria<LeaseTermGuarantor> criteria = new EntityQueryCriteria<LeaseTermGuarantor>(LeaseTermGuarantor.class);
        criteria.eq(criteria.proto().leaseTermV().holder(), bo.masterOnlineApplication().leaseApplication().lease().currentTerm());
        criteria.ne(criteria.proto().leaseParticipant().customer().user(), ProspectPortalContext.getCustomerUserIdStub());

        for (LeaseTermGuarantor ltg : Persistence.service().query(criteria)) {
            GuarantorDTO grnt = EntityFactory.create(GuarantorDTO.class);

            grnt.firstName().setValue(ltg.leaseParticipant().customer().person().name().firstName().getValue());
            grnt.lastName().setValue(ltg.leaseParticipant().customer().person().name().lastName().getValue());

            grnt.email().setValue(ltg.leaseParticipant().customer().person().email().getValue());

            // remember corresponding customer: 
            grnt.set(grnt.guarantorId(), ltg);
            grnt.guarantorId().setAttachLevel(AttachLevel.IdOnly);

            to.guarantors().add(grnt);
        }
    }

    private void saveGuarantors(OnlineApplication bo, OnlineApplicationDTO to) {
        LeaseTerm leaseTerm = bo.masterOnlineApplication().leaseApplication().lease().currentTerm();

        // clear removed:
        Iterator<LeaseTermGuarantor> it = leaseTerm.version().guarantors().iterator();
        while (it.hasNext()) {
            Boolean present = false;
            LeaseTermGuarantor ltg = it.next();
            Persistence.ensureRetrieve(ltg, AttachLevel.Attached);
            if (!ltg.leaseParticipant().customer().user().equals(ProspectPortalContext.getCustomerUserIdStub())) {
                for (GuarantorDTO grnt : to.guarantors()) {
                    if (!grnt.guarantorId().isNull() && grnt.guarantorId().getPrimaryKey().equals(ltg.getPrimaryKey())) {
                        present = true;
                        break;
                    }
                }
                if (!present) {
                    it.remove();
                }
            }
        }

        // add/update the rest:
        for (GuarantorDTO grnt : to.guarantors()) {
            if (grnt.guarantorId().isNull()) {
                // create new:
                LeaseTermGuarantor ltt = EntityFactory.create(LeaseTermGuarantor.class);
                updateGuarantor(ltt, grnt);
                leaseTerm.version().guarantors().add(ltt);
            } else {
                // update current:
                for (LeaseTermGuarantor ltg : leaseTerm.version().guarantors()) {
                    if (ltg.getPrimaryKey().equals(grnt.guarantorId().getPrimaryKey())) {
                        Persistence.ensureRetrieve(ltg, AttachLevel.Attached);
                        updateGuarantor(ltg, grnt);
                        break;
                    }
                }
            }
        }
    }

    private void updateGuarantor(LeaseTermGuarantor ltt, GuarantorDTO cap) {
        ltt.leaseParticipant().customer().person().name().firstName().setValue(cap.firstName().getValue());
        ltt.leaseParticipant().customer().person().name().lastName().setValue(cap.lastName().getValue());

        ltt.leaseParticipant().customer().person().email().setValue(cap.email().getValue());
    }

    private void fillUnitSelectionData(OnlineApplication bo, OnlineApplicationDTO to) {
        MasterOnlineApplication moa = ProspectPortalContext.getMasterOnlineApplication();
        if (!moa.building().isNull() || !moa.floorplan().isNull()) {
            UnitSelectionDTO unitSelection = EntityFactory.create(UnitSelectionDTO.class);
            Lease lease = ProspectPortalContext.getLease();

            if (lease != null && !lease.unit().isNull()) {
                Persistence.ensureRetrieve(lease.unit(), AttachLevel.Attached);

                unitSelection.unit().set(lease.unit());
                unitSelection.building().set(lease.unit().building());
                unitSelection.floorplan().set(lease.unit().floorplan());
                unitSelection.moveIn().setValue(lease.leaseFrom().getValue());

                to.unitOptionsSelection().set(retriveCurrentUnitOptions(lease));
            } else {
                unitSelection.building().set(moa.building());
                unitSelection.floorplan().set(moa.floorplan());
                unitSelection.moveIn().setValue(new LogicalDate(SystemDateManager.getDate()));
            }

            {
                EntityQueryCriteria<Floorplan> criteria = new EntityQueryCriteria<Floorplan>(Floorplan.class);
                criteria.eq(criteria.proto().building(), moa.building());
                unitSelection.availableFloorplans().addAll(Persistence.service().query(criteria));
            }

            if (!unitSelection.floorplan().isNull()) {
                unitSelection.availableUnits().addAll(retriveAvailableUnits(unitSelection.floorplan(), unitSelection.moveIn().getValue()));
            }

            to.unitSelection().set(unitSelection);

            Persistence.ensureRetrieve(unitSelection.building(), AttachLevel.ToStringMembers);
            Persistence.ensureRetrieve(unitSelection.floorplan(), AttachLevel.ToStringMembers);
        }
    }

    private void saveUnitSelectionData(OnlineApplication bo, OnlineApplicationDTO to) {
        if (!to.unitSelection().unit().isNull() && !to.unitOptionsSelection().selectedService().isNull()) {
            LeaseTerm leaseTerm = bo.masterOnlineApplication().leaseApplication().lease().currentTerm();

            List<BillableItem> featureItems = new ArrayList<BillableItem>();
            featureItems.addAll(to.unitOptionsSelection().selectedPets());
            featureItems.addAll(to.unitOptionsSelection().selectedParking());
            featureItems.addAll(to.unitOptionsSelection().selectedStorage());
            featureItems.addAll(to.unitOptionsSelection().selectedUtilities());
            featureItems.addAll(to.unitOptionsSelection().selectedOther());

            leaseTerm.termFrom().setValue(to.unitSelection().moveIn().getValue());

            ServerSideFactory.create(LeaseFacade.class).setPackage(leaseTerm, to.unitSelection().unit(), to.unitOptionsSelection().selectedService(),
                    featureItems);
        }
    }

    private void fillLegalTerms(OnlineApplication bo, OnlineApplicationDTO to) {
        to.legalTerms().addAll(ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplicationTerms(bo));
    }

    private void saveLegalTerms(OnlineApplication bo, OnlineApplicationDTO to) {
        bo.legalTerms().clear();
        bo.legalTerms().addAll(bo.legalTerms());
    }

    private void saveApplicationData(OnlineApplicationDTO to, boolean submit) {
        OnlineApplication bo = ProspectPortalContext.getOnlineApplication();

        Persistence.ensureRetrieve(bo.masterOnlineApplication(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.masterOnlineApplication().leaseApplication().lease(), AttachLevel.Attached);

        LeaseTerm leaseTerm = Persistence.retrieveDraftForEdit(LeaseTerm.class, bo.masterOnlineApplication().leaseApplication().lease().currentTerm()
                .getPrimaryKey());

        // All saveXXX methods SHOULD use this Lease and current LeaseTerm:
        bo.masterOnlineApplication().leaseApplication().lease().set(bo.masterOnlineApplication().leaseApplication().lease().currentTerm(), leaseTerm);
        bo.masterOnlineApplication().leaseApplication().lease().currentTerm().lease().set(bo.masterOnlineApplication().leaseApplication().lease());

        saveUnitSelectionData(bo, to);

        saveApplicantData(bo, to);

        saveCoApplicants(bo, to);
        saveGuarantors(bo, to);

        saveLegalTerms(bo, to);

        // do not forget to save LEASE:
        ServerSideFactory.create(LeaseFacade.class).persist(bo.masterOnlineApplication().leaseApplication().lease());

        if (submit) {
            ServerSideFactory.create(OnlineApplicationFacade.class).submitOnlineApplication(bo);
        }
    }

    // ================================================================================================================

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

        Persistence.ensureRetrieve(unit.building().utilities(), AttachLevel.ToStringMembers);

        String res = new String();
        for (BuildingUtility utility : unit.building().utilities()) {
            if (!res.isEmpty()) {
                res += ";";
            }
            res += utility.getStringView();
        }

        return res;
    }

    private void initializeRequiredDocuments(ApplicantDTO applicant) {
        for (IdentificationDocumentType docType : applicant.documentsPolicy().allowedIDs()) {
            if (docType.required().getValue(false)) {
                // Find if we already have it.
                boolean found = false;
                for (IdentificationDocumentFolder doc : applicant.documents()) {
                    if (doc.idType().equals(docType)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    IdentificationDocumentFolder doc = EntityFactory.create(IdentificationDocumentFolder.class);
                    doc.set(doc.idType(), docType);
                    applicant.documents().add(doc);
                }
            }
        }
    }

    private List<AptUnit> retriveAvailableUnits(Floorplan floorplanId, LogicalDate moveIn) {
        if (moveIn == null) {
            moveIn = new LogicalDate(SystemDateManager.getDate());
        }

        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        criteria.eq(criteria.proto().floorplan(), floorplanId);

        if (VistaFeatures.instance().yardiIntegration()) {
            criteria.le(criteria.proto()._availableForRent(), moveIn);
        } else {
            criteria.eq(criteria.proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.available);
            criteria.eq(criteria.proto().unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1));
            criteria.le(criteria.proto().unitOccupancySegments().$().dateFrom(), moveIn);
        }

        return Persistence.service().query(criteria);
    }

    private UnitOptionsSelectionDTO retriveCurrentUnitOptions(Lease lease) {
        UnitOptionsSelectionDTO options = EntityFactory.create(UnitOptionsSelectionDTO.class);
        assert (!lease.unit().isNull());

        options.unit().set(lease.unit());
        options.restrictions().set(retriveUnitOptionRestrictions(lease.unit()));
        fillCurrentProductItems(options, lease.currentTerm());
        loadDetachedProducts(options);

        return options;
    }

    private UnitOptionsSelectionDTO retriveAvailableUnitOptions(AptUnit unit) {
        UnitOptionsSelectionDTO options = EntityFactory.create(UnitOptionsSelectionDTO.class);

        options.unit().set(unit);
        options.restrictions().set(retriveUnitOptionRestrictions(unit));
        fillAvailableCatalogItems(options);
        loadDetachedProducts(options);

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

    private void fillCurrentProductItems(UnitOptionsSelectionDTO options, LeaseTerm leaseTerm) {
        leaseTerm = Persistence.retrieveDraftForEdit(LeaseTerm.class, leaseTerm.getPrimaryKey());

        options.selectedService().set(leaseTerm.version().leaseProducts().serviceItem());

        for (BillableItem feature : leaseTerm.version().leaseProducts().featureItems()) {
            switch (feature.item().product().holder().code().type().getValue()) {
            case AddOn:
            case Utility:
                options.selectedUtilities().add(feature);
                break;
            case Pet:
                options.selectedPets().add(feature);
                break;
            case Parking:
                options.selectedParking().add(feature);
                break;
            case Locker:
                options.selectedStorage().add(feature);
                break;
            default:
                options.selectedOther().add(feature);
            }
        }

        fillCatalogItems(options, (Service.ServiceV) options.selectedService().item().product().cast(), false);
    }

    private void fillAvailableCatalogItems(UnitOptionsSelectionDTO options) {
        assert (!options.unit().isNull());

        Service service;
        {
            EntityQueryCriteria<Service> criteria = new EntityQueryCriteria<Service>(Service.class);
            criteria.eq(criteria.proto().catalog().building().units(), options.unit());
            criteria.in(criteria.proto().code().type(), ARCode.Type.Residential);
            criteria.eq(criteria.proto().isDefaultCatalogItem(), Boolean.FALSE);
            criteria.isCurrent(criteria.proto().version());

            service = Persistence.service().retrieve(criteria);
        }
        if (service != null) {
            EntityQueryCriteria<ProductItem> criteria = new EntityQueryCriteria<ProductItem>(ProductItem.class);
            criteria.eq(criteria.proto().product(), service.version());
            criteria.eq(criteria.proto().element(), options.unit());

            options.selectedService().set(createBillableItem(Persistence.service().retrieve(criteria)));

            fillCatalogItems(options, service.version(), true);
        }
    }

    private void fillCatalogItems(UnitOptionsSelectionDTO options, Service.ServiceV service, Boolean fillMandatory) {
        Persistence.service().retrieveMember(service.features());

        for (Feature feature : service.features()) {
            Persistence.service().retrieveMember(feature.version().items());

            for (ProductItem item : feature.version().items()) {
                switch (feature.code().type().getValue()) {
                case AddOn:
                case Utility:
                    options.availableUtilities().add(item);
                    if (fillMandatory && feature.version().mandatory().isBooleanTrue()) {
                        options.selectedUtilities().add(createBillableItem(item));
                    }
                    break;
                case Pet:
                    options.availablePets().add(item);
                    if (fillMandatory && feature.version().mandatory().isBooleanTrue()) {
                        options.selectedPets().add(createBillableItem(item));
                    }
                    break;
                case Parking:
                    options.availableParking().add(item);
                    if (fillMandatory && feature.version().mandatory().isBooleanTrue()) {
                        options.selectedParking().add(createBillableItem(item));
                    }
                    break;
                case Locker:
                    options.availableStorage().add(item);
                    if (fillMandatory && feature.version().mandatory().isBooleanTrue()) {
                        options.selectedStorage().add(createBillableItem(item));
                    }
                    break;
                default:
                    options.availableOther().add(item);
                    if (fillMandatory && feature.version().mandatory().isBooleanTrue()) {
                        options.selectedOther().add(createBillableItem(item));
                    }
                }
            }
        }
    }

    private void loadDetachedProducts(UnitOptionsSelectionDTO options) {
        Persistence.service().retrieve(options.selectedService().item().product());

        loadDetachedProducts(options.selectedPets());
        loadDetachedProducts(options.selectedParking());
        loadDetachedProducts(options.selectedStorage());
        loadDetachedProducts(options.selectedUtilities());
        loadDetachedProducts(options.selectedOther());
    }

    private void loadDetachedProducts(List<BillableItem> items) {
        for (BillableItem item : items) {
            Persistence.service().retrieve(item.item().product());
        }
    }

    private BillableItem createBillableItem(ProductItem productItem) {
        BillableItem newItem = EntityFactory.create(BillableItem.class);

        newItem.item().set(productItem);
        newItem.agreedPrice().setValue(productItem.price().getValue());

        return newItem;
    }

    private PolicyNode getPolicyNode(MasterOnlineApplication moa) {
        if (!moa.leaseApplication().lease().unit().isNull()) {
            return moa.leaseApplication().lease().unit();
        } else if (!moa.building().isNull()) {
            return moa.building();
        } else {
            throw new Error("Application do not have building relations");
        }
    }
}
