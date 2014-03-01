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
import java.util.Collection;
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
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade.PaymentMethodUsage;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.ProspectPortalPolicy;
import com.propertyvista.domain.policy.policies.ProspectPortalPolicy.FeePayment;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.dto.ApplicantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.CoapplicantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.DependentDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.GuarantorDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.PaymentDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.TenantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.BathroomNumber;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.BedroomNumber;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.UnitTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.common.util.AddressConverter;
import com.propertyvista.server.common.util.AddressRetriever;

public class ApplicationWizardServiceImpl implements ApplicationWizardService {

    public ApplicationWizardServiceImpl() {
    }

    @Override
    public void init(AsyncCallback<OnlineApplicationDTO> callback) {
        OnlineApplication bo = ProspectPortalContext.getOnlineApplication();

        Persistence.ensureRetrieve(bo.masterOnlineApplication().leaseApplication().lease().unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.masterOnlineApplication().leaseApplication().lease().unit().floorplan(), AttachLevel.Attached);

        OnlineApplicationDTO to = EntityFactory.create(OnlineApplicationDTO.class);
        to.policyNode().set(ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplicationPolicyNode(bo));

        loadRestrictions(bo, to);

        fillLeaseData(bo, to);

        fillUnitSelectionData(bo, to);
        fillUnitOptionsData(bo, to);

        fillApplicantData(bo, to);

        fillOccupants(bo, to);
        fillGuarantors(bo, to);

        fillTerms(bo, to);

        fillStepsStatuses(bo, to);

        fillPaymentData(bo, to);

        ProspectPortalPolicy prospectPortalPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(to.policyNode(),
                ProspectPortalPolicy.class);
        to.feePaymentPolicy().set(prospectPortalPolicy.feePayment());

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
    public void getAvailableUnits(AsyncCallback<UnitSelectionDTO> callback, UnitSelectionDTO unitSelection) {
        unitSelection.availableUnits().clear();
        unitSelection.availableUnits().addAll(
                retriveAvailableUnits(unitSelection.building(), unitSelection.bedrooms().getValue(), unitSelection.bathrooms().getValue(), unitSelection
                        .moveIn().getValue()));

        unitSelection.potentialUnits().clear();
        unitSelection.potentialUnits().addAll(
                retrivePotentialUnits(unitSelection.building(), unitSelection.bedrooms().getValue(), unitSelection.bathrooms().getValue(), unitSelection
                        .moveIn().getValue()));

        excludeAvailbleFromPotential(unitSelection);

        callback.onSuccess(unitSelection);
    }

    @Override
    public void getAvailableUnitOptions(AsyncCallback<UnitOptionsSelectionDTO> callback, UnitTO unitId) {
        UnitOptionsSelectionDTO options = EntityFactory.create(UnitOptionsSelectionDTO.class);

        options.unit().set(filterUnitData(Persistence.service().retrieve(AptUnit.class, unitId.getPrimaryKey())));

        callback.onSuccess(fillAvailableUnitOptions(options));
    }

    @Override
    public void getCurrentDeposits(AsyncCallback<Vector<Deposit>> callback, OnlineApplicationDTO currentValue) {
        // TODO: implement it:
        callback.onSuccess(new Vector<Deposit>(/* calculateDeposits(lease) */));
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback) {
        List<LeasePaymentMethod> methods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(
                ProspectPortalContext.getLeaseTermTenant(), PaymentMethodUsage.OneTimePayments, VistaApplication.prospect);
        callback.onSuccess(new Vector<LeasePaymentMethod>(methods));
    }

    // internals: -----------------------------------------------------------------------------------------------------

    private void fillLeaseData(OnlineApplication bo, OnlineApplicationDTO to) {
        assert (!bo.masterOnlineApplication().leaseApplication().lease().isValueDetached());

        LeaseTerm term = Persistence.retrieveDraftForEdit(LeaseTerm.class, bo.masterOnlineApplication().leaseApplication().lease().currentTerm()
                .getPrimaryKey());

        to.unit().set(filterUnitData(bo.masterOnlineApplication().leaseApplication().lease().unit()));
        to.utilities().setValue(retrieveUtilities(term));

        to.leaseFrom().setValue(bo.masterOnlineApplication().leaseApplication().lease().leaseFrom().getValue());
        to.leaseTo().setValue(bo.masterOnlineApplication().leaseApplication().lease().leaseTo().getValue());

        to.selectedService().set(term.version().leaseProducts().serviceItem());
        to.selectedFeatures().addAll(term.version().leaseProducts().featureItems());

        fillTenants(bo, to);
    }

    private void fillTenants(OnlineApplication bo, OnlineApplicationDTO to) {
        EntityQueryCriteria<LeaseTermTenant> criteria = new EntityQueryCriteria<LeaseTermTenant>(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseTermV().holder(), bo.masterOnlineApplication().leaseApplication().lease().currentTerm());

        for (LeaseTermTenant ltt : Persistence.service().query(criteria)) {
            TenantDTO tenant = EntityFactory.create(TenantDTO.class);

            tenant.name().set(ltt.leaseParticipant().customer().person().name());
            tenant.role().setValue(ltt.role().getValue());

            // remember corresponding tenant: 
            tenant.set(tenant.tenantId(), ltt);
            tenant.tenantId().setAttachLevel(AttachLevel.IdOnly);

            to.tenants().add(tenant);
        }
    }

    private void fillApplicantData(OnlineApplication bo, OnlineApplicationDTO to) {
        to.applicant().set(EntityFactory.create(ApplicantDTO.class));

        switch (bo.role().getValue()) {
        case Applicant:
        case CoApplicant:
            fillLeaseTermParticipant(bo, to, ProspectPortalContext.getLeaseTermTenant());
            break;

        case Guarantor:
            fillLeaseTermParticipant(bo, to, ProspectPortalContext.getLeaseTermGuarantor());
            break;
        default:
            throw new IllegalArgumentException();
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

    private void fillLeaseTermParticipant(OnlineApplication bo, OnlineApplicationDTO to, LeaseTermParticipant<?> participant) {
        // customer:
        Customer customer = participant.leaseParticipant().customer();
        Persistence.service().retrieve(customer.picture());
        Persistence.service().retrieve(customer.emergencyContacts());
        //
        to.applicant().set(to.applicant().person(), customer.person());
        to.applicant().set(to.applicant().picture(), customer.picture());
        to.applicant().set(to.applicant().emergencyContacts(), customer.emergencyContacts());

        // screening:
        CustomerScreening screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftForEdit(customer);
        Persistence.ensureRetrieve(screening.version().incomes(), AttachLevel.Attached);
        Persistence.ensureRetrieve(screening.version().assets(), AttachLevel.Attached);
        Persistence.ensureRetrieve(screening.version().documents(), AttachLevel.Attached);
        ServerSideFactory.create(ScreeningFacade.class).registerUploadedDocuments(screening);
        //
        to.applicant().set(to.applicant().currentAddress(), screening.version().currentAddress());
        to.applicant().set(to.applicant().previousAddress(), screening.version().previousAddress());

        to.applicant().set(to.applicant().documents(), screening.version().documents());
        to.applicant().set(to.applicant().documentsPolicy(),
                ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(to.policyNode(), ApplicationDocumentationPolicy.class));
        initializeRequiredDocuments(to.applicant());

        to.applicant().set(to.applicant().legalQuestions(), screening.version().legalQuestions());

        to.applicant().set(to.applicant().incomes(), screening.version().incomes());
        to.applicant().set(to.applicant().assets(), screening.version().assets());
    }

    private void saveApplicantData(OnlineApplication bo, OnlineApplicationDTO to) {
        LeaseTerm leaseTerm = bo.masterOnlineApplication().leaseApplication().lease().currentTerm();

        switch (bo.role().getValue()) {
        case Applicant:
        case CoApplicant:
            Persistence.ensureRetrieve(leaseTerm.version().tenants(), AttachLevel.Attached);
            for (LeaseTermTenant tenant : leaseTerm.version().tenants()) {
                if (tenant.leaseParticipant().customer().equals(ProspectPortalContext.getCustomer())) {
                    saveLeaseTermParticipant(bo, to, tenant);
                    break;
                }
            }
            break;

        case Guarantor:
            Persistence.ensureRetrieve(leaseTerm.version().guarantors(), AttachLevel.Attached);
            for (LeaseTermGuarantor guarantor : leaseTerm.version().guarantors()) {
                if (guarantor.leaseParticipant().customer().equals(ProspectPortalContext.getCustomer())) {
                    saveLeaseTermParticipant(bo, to, guarantor);
                    break;
                }
            }
            break;
        case Dependent:
            throw new IllegalArgumentException();
        }

    }

    private void saveLeaseTermParticipant(OnlineApplication bo, OnlineApplicationDTO to, LeaseTermParticipant<?> participant) {
        // customer:
        Customer customer = participant.leaseParticipant().customer();
        Persistence.service().retrieve(customer.picture());
        Persistence.service().retrieve(customer.emergencyContacts());
        //
        customer.set(customer.person(), to.applicant().person());
        customer.set(customer.picture(), to.applicant().picture());
        customer.emergencyContacts().set(to.applicant().emergencyContacts());
        //DataDump.dump("customer", customer);

        // screening:
        CustomerScreening screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftForEdit(customer);
        //DataDump.dump("dbScreening", screening);
        //
        screening.version().set(screening.version().currentAddress(), to.applicant().currentAddress());
        screening.version().set(screening.version().previousAddress(), to.applicant().previousAddress());
        screening.version().set(screening.version().legalQuestions(), to.applicant().legalQuestions());

        screening.version().documents().set(to.applicant().documents());
        screening.version().incomes().set(to.applicant().incomes());
        screening.version().assets().set(to.applicant().assets());

        //DataDump.dump("saveScreening", screening);

        Persistence.service().merge(screening);
    }

    private void fillOccupants(OnlineApplication bo, OnlineApplicationDTO to) {
        EntityQueryCriteria<LeaseTermTenant> criteria = new EntityQueryCriteria<LeaseTermTenant>(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseTermV().holder(), bo.masterOnlineApplication().leaseApplication().lease().currentTerm());
        criteria.ne(criteria.proto().leaseParticipant().customer(), bo.customer());

        for (LeaseTermTenant ltt : Persistence.service().query(criteria)) {
            if (ltt.role().getValue() == Role.CoApplicant) {
                CoapplicantDTO cap = EntityFactory.create(CoapplicantDTO.class);

                cap.relationship().setValue(ltt.relationship().getValue());

                cap.name().set(ltt.leaseParticipant().customer().person().name());
                cap.email().setValue(ltt.leaseParticipant().customer().person().email().getValue());

                // remember corresponding tenant: 
                cap.set(cap.tenantId(), ltt);
                cap.tenantId().setAttachLevel(AttachLevel.IdOnly);

                to.coapplicants().add(cap);
            } else if (ltt.role().getValue() == Role.Dependent) {
                DependentDTO dep = EntityFactory.create(DependentDTO.class);

                dep.relationship().setValue(ltt.relationship().getValue());

                dep.name().set(ltt.leaseParticipant().customer().person().name());
                dep.birthDate().setValue(ltt.leaseParticipant().customer().person().birthDate().getValue());

                // remember corresponding tenant: 
                dep.set(dep.tenantId(), ltt);
                dep.tenantId().setAttachLevel(AttachLevel.IdOnly);

                to.dependents().add(dep);
            }
        }
    }

    private void saveOccupants(OnlineApplication bo, OnlineApplicationDTO to) {
        LeaseTerm leaseTerm = bo.masterOnlineApplication().leaseApplication().lease().currentTerm();

        // clear removed:
        Iterator<LeaseTermTenant> it = leaseTerm.version().tenants().iterator();
        while (it.hasNext()) {
            Boolean present = false;
            LeaseTermTenant ltt = it.next();
            Persistence.ensureRetrieve(ltt, AttachLevel.Attached);
            if (!ltt.leaseParticipant().customer().equals(bo.customer())) {
                for (CoapplicantDTO cap : to.coapplicants()) {
                    if (!cap.tenantId().isNull() && cap.tenantId().getPrimaryKey().equals(ltt.getPrimaryKey())) {
                        present = true;
                        break;
                    }
                }
                if (!present) {
                    for (DependentDTO dep : to.dependents()) {
                        if (!dep.tenantId().isNull() && dep.tenantId().getPrimaryKey().equals(ltt.getPrimaryKey())) {
                            present = true;
                            break;
                        }
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
        for (DependentDTO dep : to.dependents()) {
            if (dep.tenantId().isNull()) {
                // create new:
                LeaseTermTenant ltt = EntityFactory.create(LeaseTermTenant.class);
                updateDependent(ltt, dep);
                leaseTerm.version().tenants().add(ltt);
            } else {
                // update current:
                for (LeaseTermTenant ltt : leaseTerm.version().tenants()) {
                    if (ltt.getPrimaryKey().equals(dep.tenantId().getPrimaryKey())) {
                        Persistence.ensureRetrieve(ltt, AttachLevel.Attached);
                        updateDependent(ltt, dep);
                        break;
                    }
                }
            }
        }
    }

    private void updateCoApplicant(LeaseTermTenant ltt, CoapplicantDTO cap) {
        ltt.role().setValue(Role.CoApplicant);
        ltt.relationship().setValue(cap.relationship().getValue());

        ltt.leaseParticipant().customer().person().name().set(cap.name());
        ltt.leaseParticipant().customer().person().email().setValue(cap.email().getValue());
    }

    private void updateDependent(LeaseTermTenant ltt, DependentDTO dep) {
        ltt.role().setValue(Role.Dependent);
        ltt.relationship().setValue(dep.relationship().getValue());

        ltt.leaseParticipant().customer().person().name().set(dep.name());
        ltt.leaseParticipant().customer().person().birthDate().setValue(dep.birthDate().getValue());
    }

    private void fillGuarantors(OnlineApplication bo, OnlineApplicationDTO to) {
        EntityQueryCriteria<LeaseTermGuarantor> criteria = new EntityQueryCriteria<LeaseTermGuarantor>(LeaseTermGuarantor.class);
        criteria.eq(criteria.proto().leaseTermV().holder(), bo.masterOnlineApplication().leaseApplication().lease().currentTerm());
        criteria.eq(criteria.proto().tenant().customer(), bo.customer());

        for (LeaseTermGuarantor ltg : Persistence.service().query(criteria)) {
            GuarantorDTO grnt = EntityFactory.create(GuarantorDTO.class);

            grnt.relationship().setValue(ltg.relationship().getValue());

            grnt.name().setValue(ltg.leaseParticipant().customer().person().name().getValue());
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
            if (!ltg.leaseParticipant().customer().equals(bo.customer())) {
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

        validateGuarantors(bo, to);
    }

    private void updateGuarantor(LeaseTermGuarantor ltg, GuarantorDTO grt) {
        ltg.role().setValue(LeaseTermParticipant.Role.Guarantor);
        ltg.relationship().setValue(grt.relationship().getValue());
        ltg.leaseParticipant().customer().person().name().set(grt.name());
        ltg.leaseParticipant().customer().person().email().setValue(grt.email().getValue());

        ltg.tenant().set(ProspectPortalContext.getTenant());
    }

    // Removes duplicate emails in guarantors: 
    private void validateGuarantors(OnlineApplication bo, OnlineApplicationDTO to) {
        LeaseTerm leaseTerm = bo.masterOnlineApplication().leaseApplication().lease().currentTerm();

        Collection<String> emails = new ArrayList<>();
        for (LeaseTermTenant tenant : leaseTerm.version().tenants()) {
            if (!tenant.leaseParticipant().customer().person().email().isNull()) {
                emails.add(tenant.leaseParticipant().customer().person().email().getValue());
            }
        }

        for (LeaseTermGuarantor grnt : leaseTerm.version().guarantors()) {
            if (emails.contains(grnt.leaseParticipant().customer().person().email().getValue())) {
                System.out.println("Guarantor " + grnt.leaseParticipant().customer().person().getStringView() + " has duplicate email!");
                grnt.leaseParticipant().customer().person().email().setValue(null);
            }
        }
    }

    private void fillUnitSelectionData(OnlineApplication bo, OnlineApplicationDTO to) {
        MasterOnlineApplication moa = ProspectPortalContext.getMasterOnlineApplication();
        if (!moa.building().isNull() || !moa.floorplan().isNull()) {
            UnitSelectionDTO unitSelection = EntityFactory.create(UnitSelectionDTO.class);

            Lease lease = bo.masterOnlineApplication().leaseApplication().lease();

            if (!lease.unit().isNull()) {
                Persistence.ensureRetrieve(lease.unit().floorplan(), AttachLevel.Attached);
                Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);

                unitSelection.selectedUnit().set(createUnitDTO(lease.unit()));
                unitSelection.building().set(lease.unit().building());
                unitSelection.floorplan().set(lease.unit().floorplan());
                unitSelection.moveIn().setValue(lease.leaseFrom().getValue());
            } else {
                Persistence.ensureRetrieve(moa.floorplan(), AttachLevel.Attached);
                Persistence.ensureRetrieve(moa.building(), AttachLevel.ToStringMembers);

                unitSelection.building().set(moa.building());
                unitSelection.floorplan().set(moa.floorplan());
                unitSelection.moveIn().setValue(new LogicalDate(SystemDateManager.getDate()));
            }

            if (!unitSelection.floorplan().isNull()) {
                updateBedsDensBaths(unitSelection);
                unitSelection.availableUnits().addAll(retriveAvailableUnits(unitSelection.floorplan(), unitSelection.moveIn().getValue()));
                unitSelection.potentialUnits().addAll(retrivePotentialUnits(unitSelection.floorplan(), unitSelection.moveIn().getValue()));
                excludeAvailbleFromPotential(unitSelection);
            }

            to.unitSelection().set(unitSelection);

            Persistence.ensureRetrieve(unitSelection.building(), AttachLevel.ToStringMembers);
            Persistence.ensureRetrieve(unitSelection.floorplan(), AttachLevel.ToStringMembers);
        }
    }

    private void updateBedsDensBaths(UnitSelectionDTO unitSelection) {

        switch (unitSelection.floorplan().bedrooms().getValue()) {
        case 0:
            unitSelection.bedrooms().setValue(BedroomNumber.Any);
            break;

        case 1:
            unitSelection.bedrooms().setValue(BedroomNumber.One);
            if (!unitSelection.floorplan().dens().isNull()) {
                unitSelection.bedrooms().setValue(BedroomNumber.OneAndHalf);
            }
            break;
        case 2:
            unitSelection.bedrooms().setValue(BedroomNumber.Two);
            if (!unitSelection.floorplan().dens().isNull()) {
                unitSelection.bedrooms().setValue(BedroomNumber.TwoAndHalf);
            }
            break;
        case 3:
            unitSelection.bedrooms().setValue(BedroomNumber.Three);
            if (!unitSelection.floorplan().dens().isNull()) {
                unitSelection.bedrooms().setValue(BedroomNumber.ThreeAndHalf);
            }
            break;
        case 4:
            unitSelection.bedrooms().setValue(BedroomNumber.Four);
            if (!unitSelection.floorplan().dens().isNull()) {
                unitSelection.bedrooms().setValue(BedroomNumber.FourAndHalf);
            }
            break;
        case 5:
            unitSelection.bedrooms().setValue(BedroomNumber.Five);
            if (!unitSelection.floorplan().dens().isNull()) {
                unitSelection.bedrooms().setValue(BedroomNumber.FiveAndHalf);
            }
            break;

        default:
            unitSelection.bedrooms().setValue(BedroomNumber.More);
            break;
        }

        switch (unitSelection.floorplan().bathrooms().getValue()) {
        case 0:
            unitSelection.bathrooms().setValue(BathroomNumber.Any);
            break;

        case 1:
            unitSelection.bathrooms().setValue(BathroomNumber.One);
            break;
        case 2:
            unitSelection.bathrooms().setValue(BathroomNumber.Two);
            break;
        case 3:
            unitSelection.bathrooms().setValue(BathroomNumber.Three);
            break;

        default:
            unitSelection.bathrooms().setValue(BathroomNumber.More);
            break;
        }
    }

    private void fillUnitOptionsData(OnlineApplication bo, OnlineApplicationDTO to) {
        Lease lease = bo.masterOnlineApplication().leaseApplication().lease();
        if (!lease.unit().isNull()) {
            UnitOptionsSelectionDTO options = retriveCurrentUnitOptions(lease);
            options.restrictions().set(retriveUnitOptionRestrictions(options.unit()));
            fillCatalogFeatures(options, options.selectedService().item().product().<Service.ServiceV> cast(), false);
            to.unitOptionsSelection().set(options);
        }
    }

    private void saveUnitOptionsData(OnlineApplication bo, OnlineApplicationDTO to) {
        if (to.unitSelection().isNull()) {
            return; // currently - do nothing if non-unit-selection mode!
        }

        if (!to.unitOptionsSelection().isNull()) {
            LeaseTerm leaseTerm = bo.masterOnlineApplication().leaseApplication().lease().currentTerm();

            List<BillableItem> featureItems = new ArrayList<BillableItem>();
            featureItems.addAll(to.unitOptionsSelection().selectedPets());
            featureItems.addAll(to.unitOptionsSelection().selectedParking());
            featureItems.addAll(to.unitOptionsSelection().selectedStorage());
            featureItems.addAll(to.unitOptionsSelection().selectedUtilities());
            featureItems.addAll(to.unitOptionsSelection().selectedOther());

            ServerSideFactory.create(LeaseFacade.class).setPackage(leaseTerm,
                    EntityFactory.createIdentityStub(AptUnit.class, to.unitOptionsSelection().unit().getPrimaryKey()),
                    to.unitOptionsSelection().selectedService(), featureItems);
        }
    }

    private void fillTerms(OnlineApplication bo, OnlineApplicationDTO to) {
        to.legalTerms().addAll(ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplicationLegalTerms(bo));
        to.confirmationTerms().addAll(ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplicationConfirmationTerms(bo));
    }

    private void saveTerms(OnlineApplication bo, OnlineApplicationDTO to) {
        bo.legalTerms().clear();
        bo.legalTerms().addAll(to.legalTerms());

        bo.confirmationTerms().clear();
        bo.confirmationTerms().addAll(to.confirmationTerms());

    }

    private void fillStepsStatuses(OnlineApplication bo, OnlineApplicationDTO to) {
        to.stepsStatuses().addAll(bo.stepsStatuses());
    }

    private void saveStepsStatuses(OnlineApplication bo, OnlineApplicationDTO to) {
        bo.stepsStatuses().clear();
        bo.stepsStatuses().addAll(to.stepsStatuses());
    }

    private void fillPaymentData(OnlineApplication bo, OnlineApplicationDTO to) {
        Lease lease = bo.masterOnlineApplication().leaseApplication().lease();

        PaymentDTO dto = EntityFactory.create(PaymentDTO.class);

        dto.billingAccount().set(lease.billingAccount());
        dto.allowedPaymentsSetup().set(ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentsSetup(to.policyNode(), VistaApplication.prospect));

        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(AddressRetriever.getLeaseAddress(lease), dto.address());

        dto.propertyCode().set(lease.unit().building().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());

        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.status());

        dto.leaseTermParticipant().set(ProspectPortalContext.getLeaseTermTenant());

        // some default values:
        dto.createdDate().setValue(SystemDateManager.getDate());
        dto.convenienceFeeSignature().signatureFormat().setValue(SignatureFormat.AgreeBox);

        // current balance: -------------------------------------------------------------------------------------------------------

        // calculate deposits/fee:
        dto.deposits().addAll(calculateDeposits(lease));

        ProspectPortalPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(to.policyNode(), ProspectPortalPolicy.class);
        if (policy.feePayment().getValue() == FeePayment.perApplicant) {
            if (!SecurityController.checkBehavior(PortalProspectBehavior.Guarantor)) {
                dto.applicationFee().setValue(policy.feeAmount().getValue());
            }
        } else if (policy.feePayment().getValue() == FeePayment.perLease) {
            if (SecurityController.checkBehavior(PortalProspectBehavior.Applicant)) {
                dto.applicationFee().setValue(policy.feeAmount().getValue());
            }
        }

        to.payment().set(dto);
    }

    private List<Deposit> calculateDeposits(Lease lease) {
        List<Deposit> deposits = new ArrayList<>();

        LeaseTerm leaseTerm = Persistence.retrieveDraftForEdit(LeaseTerm.class, lease.currentTerm().getPrimaryKey());
        deposits.addAll(leaseTerm.version().leaseProducts().serviceItem().deposits());
        for (BillableItem feature : leaseTerm.version().leaseProducts().featureItems()) {
            deposits.addAll(feature.deposits());
        }

        return deposits;
    }

    private void savePaymentData(OnlineApplication bo, OnlineApplicationDTO to) {
        Lease lease = bo.masterOnlineApplication().leaseApplication().lease();
        PaymentDTO pto = to.payment();
        PaymentRecord pbo = new EntityBinder<PaymentRecord, PaymentDTO>(PaymentRecord.class, PaymentDTO.class) {
            @Override
            protected void bind() {
                bindCompleteObject();
            }
        }.createBO(pto);

        if (!pto.paymentMethod().isNull()) {
            pbo.paymentMethod().customer().set(ResidentPortalContext.getCustomer());
            pbo.billingAccount().set(lease.billingAccount());

            // Do not change profile methods
            if (pbo.paymentMethod().id().isNull()) {
                if (pto.storeInProfile().getValue(false) && PaymentType.availableInProfile().contains(pto.paymentMethod().type().getValue())) {
                    pbo.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);
                } else {
                    pbo.paymentMethod().isProfiledMethod().setValue(Boolean.FALSE);
                }

                // some corrections for particular method types:
                if (pto.paymentMethod().type().getValue() == PaymentType.Echeck) {
                    pbo.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);
                }
            }

            ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), pbo.paymentMethod(), VistaApplication.prospect);
            ServerSideFactory.create(PaymentFacade.class).validatePayment(pbo, VistaApplication.prospect);

            ServerSideFactory.create(PaymentFacade.class).persistPayment(pbo);
        }
    }

    private void savePaymentMethod(OnlineApplication bo, OnlineApplicationDTO to) {
        if (to.payment().storeInProfile().getValue(false)
                && ServerSideFactory.create(PaymentMethodFacade.class).isCompleatePaymentMethod(to.payment().paymentMethod())) {
            Lease lease = bo.masterOnlineApplication().leaseApplication().lease();
            Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.IdOnly);

            to.payment().paymentMethod().customer().set(ResidentPortalContext.getCustomer());
            to.payment().paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);

            ServerSideFactory.create(PaymentFacade.class)
                    .validatePaymentMethod(lease.billingAccount(), to.payment().paymentMethod(), VistaApplication.prospect);
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(to.payment().paymentMethod(), lease.unit().building());
        }
    }

    private void saveApplicationData(OnlineApplicationDTO to, boolean submit) {
        OnlineApplication bo = ProspectPortalContext.getOnlineApplication();

        Persistence.ensureRetrieve(bo.masterOnlineApplication().leaseApplication().lease(), AttachLevel.Attached);
        LeaseTerm leaseTerm = Persistence.retrieveDraftForEdit(LeaseTerm.class, bo.masterOnlineApplication().leaseApplication().lease().currentTerm()
                .getPrimaryKey());

        // All saveXXX methods SHOULD use this Lease and current LeaseTerm:
        bo.masterOnlineApplication().leaseApplication().lease().set(bo.masterOnlineApplication().leaseApplication().lease().currentTerm(), leaseTerm);
        bo.masterOnlineApplication().leaseApplication().lease().currentTerm().lease().set(bo.masterOnlineApplication().leaseApplication().lease());

        switch (bo.role().getValue()) {
        case Applicant:
            if (!to.unitSelection().isNull()) {
                // from lease term of 1 year from entered move in date:  
                leaseTerm.termFrom().setValue(to.unitSelection().moveIn().getValue());
                leaseTerm.termTo().setValue(new LogicalDate(DateUtils.yearsAdd(leaseTerm.termFrom().getValue(), 1)));
            }
            saveUnitOptionsData(bo, to);
            saveOccupants(bo, to);
            saveGuarantors(bo, to);
            break;

        case CoApplicant:
            saveGuarantors(bo, to);
            break;

        case Guarantor:
            break;

        default:
            throw new IllegalArgumentException();
        }

        saveApplicantData(bo, to);
        saveTerms(bo, to);

        saveStepsStatuses(bo, to);

        // do not forget to save LEASE:
        ServerSideFactory.create(LeaseFacade.class).persist(bo.masterOnlineApplication().leaseApplication().lease());

        if (submit) {
            savePaymentData(bo, to);
            ServerSideFactory.create(OnlineApplicationFacade.class).submitOnlineApplication(bo);
        } else {
            savePaymentMethod(bo, to);

            // update application status:
            bo.status().setValue(OnlineApplication.Status.Incomplete);
            Persistence.service().merge(bo);
        }
    }

    private void loadRestrictions(OnlineApplication bo, OnlineApplicationDTO to) {
        RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(to.policyNode(), RestrictionsPolicy.class);

        to.ageOfMajority().setValue(restrictionsPolicy.ageOfMajority().getValue());
        to.enforceAgeOfMajority().setValue(restrictionsPolicy.enforceAgeOfMajority().getValue());
        to.maturedOccupantsAreApplicants().setValue(restrictionsPolicy.maturedOccupantsAreApplicants().getValue());
    }

    // ================================================================================================================

    private AptUnit filterUnitData(AptUnit unit) {
        Persistence.ensureRetrieve(unit.building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(unit.floorplan(), AttachLevel.Attached);

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

    private UnitTO createUnitDTO(AptUnit unit) {
        Persistence.ensureRetrieve(unit.floorplan(), AttachLevel.Attached);

        UnitTO to = EntityFactory.create(UnitTO.class);
        to.setPrimaryKey(unit.getPrimaryKey());

        to.number().setValue(unit.info().number().getValue());
        to.floorplan().setValue(unit.floorplan().getStringView());
        to.floor().setValue(unit.info().floor().getValue());

        to.bedrooms().setValue(unit.floorplan().bedrooms().getValue());
        to.bathrooms().setValue(unit.floorplan().bathrooms().getValue());
        to.dens().setValue(unit.floorplan().dens().getValue());

        to.available().setValue(unit.availability().availableForRent().getValue());
        to.price().setValue(unit.financial()._marketRent().getValue());

        to.display().setValue(to.getStringView());

        return to;
    }

    private String retrieveUtilities(LeaseTerm term) {
        assert (!term.isValueDetached());

        Persistence.ensureRetrieve(term.version().utilities(), AttachLevel.ToStringMembers);

        String res = new String();
        for (BuildingUtility utility : term.version().utilities()) {
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

    private List<UnitTO> retriveAvailableUnits(Floorplan floorplanId, LogicalDate moveIn) {
        UnitSelectionDTO unitSelection = EntityFactory.create(UnitSelectionDTO.class);
        unitSelection.floorplan().set(Persistence.service().retrieve(Floorplan.class, floorplanId.getPrimaryKey()));
        updateBedsDensBaths(unitSelection);

        return retriveAvailableUnits(unitSelection.floorplan().building(), unitSelection.bedrooms().getValue(), unitSelection.bathrooms().getValue(), moveIn);
    }

    private List<UnitTO> retriveAvailableUnits(Building building, BedroomNumber beds, BathroomNumber baths, LogicalDate moveIn) {
        if (moveIn == null) {
            moveIn = new LogicalDate(SystemDateManager.getDate());
        }
        if (baths == null) {
            baths = BathroomNumber.Any;
        }
        if (beds == null) {
            beds = BedroomNumber.Any;
        }

        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);

        switch (beds) {
        case Any:
            break;

        case One:
            criteria.eq(criteria.proto().floorplan().bedrooms(), 1);
            criteria.eq(criteria.proto().floorplan().dens(), 0);
            break;
        case OneAndHalf:
            criteria.eq(criteria.proto().floorplan().bedrooms(), 1);
            criteria.ne(criteria.proto().floorplan().dens(), 0);
            break;

        case Two:
            criteria.eq(criteria.proto().floorplan().bedrooms(), 2);
            criteria.eq(criteria.proto().floorplan().dens(), 0);
            break;
        case TwoAndHalf:
            criteria.eq(criteria.proto().floorplan().bedrooms(), 2);
            criteria.ne(criteria.proto().floorplan().dens(), 0);
            break;

        case Three:
            criteria.eq(criteria.proto().floorplan().bedrooms(), 3);
            criteria.eq(criteria.proto().floorplan().dens(), 0);
            break;
        case ThreeAndHalf:
            criteria.eq(criteria.proto().floorplan().bedrooms(), 3);
            criteria.ne(criteria.proto().floorplan().dens(), 0);
            break;

        case Four:
            criteria.eq(criteria.proto().floorplan().bedrooms(), 4);
            criteria.eq(criteria.proto().floorplan().dens(), 0);
            break;
        case FourAndHalf:
            criteria.eq(criteria.proto().floorplan().bedrooms(), 4);
            criteria.ne(criteria.proto().floorplan().dens(), 0);
            break;

        case Five:
            criteria.eq(criteria.proto().floorplan().bedrooms(), 5);
            criteria.eq(criteria.proto().floorplan().dens(), 0);
            break;
        case FiveAndHalf:
            criteria.eq(criteria.proto().floorplan().bedrooms(), 5);
            criteria.ne(criteria.proto().floorplan().dens(), 0);
            break;

        case More:
            criteria.gt(criteria.proto().floorplan().bedrooms(), 5);
            break;
        }

        switch (baths) {
        case Any:
            break;
        case One:
            criteria.eq(criteria.proto().floorplan().bathrooms(), 1);
            break;
        case Two:
            criteria.eq(criteria.proto().floorplan().bathrooms(), 2);
            break;
        case Three:
            criteria.eq(criteria.proto().floorplan().bathrooms(), 3);
            break;
        case More:
            criteria.gt(criteria.proto().floorplan().bathrooms(), 3);
            break;
        }

        ProspectPortalPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, ProspectPortalPolicy.class);
        LogicalDate availabilityDeadline = DateUtils.daysAdd(moveIn, -policy.unitAvailabilitySpan().getValue());

        // building
        criteria.eq(criteria.proto().building(), building);
        // correct service type:
        criteria.in(criteria.proto().productItems().$().product().holder().code().type(), ARCode.Type.Residential);
        criteria.eq(criteria.proto().productItems().$().product().holder().defaultCatalogItem(), Boolean.FALSE);
        criteria.eq(criteria.proto().productItems().$().product().holder().version().availableOnline(), Boolean.TRUE);
        criteria.isCurrent(criteria.proto().productItems().$().product().holder().version());
        // availability: 
        criteria.add(ServerSideFactory.create(OccupancyFacade.class).buildAvalableCriteria(criteria.proto(), AptUnitOccupancySegment.Status.available, moveIn,
                availabilityDeadline));

        criteria.sort(new Sort(criteria.proto().availability().availableForRent(), false));

        List<UnitTO> availableUnits = new ArrayList<UnitTO>();
        for (AptUnit unit : Persistence.service().query(criteria)) {
            if (availableUnits.size() < policy.maxExactMatchUnits().getValue()) {
                availableUnits.add(createUnitDTO(unit));
            } else {
                break; // list no more
            }
        }

        return availableUnits;
    }

    private List<UnitTO> retrivePotentialUnits(Floorplan floorplanId, LogicalDate moveIn) {
        UnitSelectionDTO unitSelection = EntityFactory.create(UnitSelectionDTO.class);
        unitSelection.floorplan().set(Persistence.service().retrieve(Floorplan.class, floorplanId.getPrimaryKey()));
        updateBedsDensBaths(unitSelection);

        return retrivePotentialUnits(unitSelection.floorplan().building(), unitSelection.bedrooms().getValue(), unitSelection.bathrooms().getValue(), moveIn);
    }

    private List<UnitTO> retrivePotentialUnits(Building building, BedroomNumber beds, BathroomNumber baths, LogicalDate moveIn) {
        if (moveIn == null) {
            moveIn = new LogicalDate(SystemDateManager.getDate());
        }

        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);

        ProspectPortalPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, ProspectPortalPolicy.class);
        LogicalDate availabilityDeadline = DateUtils.daysAdd(moveIn, -policy.unitAvailabilitySpan().getValue());
        LogicalDate availabilityRightBound = DateUtils.monthAdd(moveIn, 2);

        // building
        criteria.eq(criteria.proto().building(), building);
        // correct service type:
        criteria.in(criteria.proto().productItems().$().product().holder().code().type(), ARCode.Type.Residential);
        criteria.eq(criteria.proto().productItems().$().product().holder().defaultCatalogItem(), Boolean.FALSE);
        criteria.eq(criteria.proto().productItems().$().product().holder().version().availableOnline(), Boolean.TRUE);
        criteria.isCurrent(criteria.proto().productItems().$().product().holder().version());
        // availability: 
        criteria.add(ServerSideFactory.create(OccupancyFacade.class).buildAvalableCriteria(criteria.proto(), AptUnitOccupancySegment.Status.available,
                availabilityRightBound, availabilityDeadline));
        criteria.sort(new Sort(criteria.proto().availability().availableForRent(), false));

        List<UnitTO> availableUnits = new ArrayList<UnitTO>();
        for (AptUnit unit : Persistence.service().query(criteria)) {
            if (availableUnits.size() < policy.maxPartialMatchUnits().getValue()) {
                availableUnits.add(createUnitDTO(unit));
            } else {
                break; // list no more
            }
        }

        return availableUnits;
    }

    private void excludeAvailbleFromPotential(UnitSelectionDTO unitSelection) {
        List<UnitTO> potential = new ArrayList<UnitTO>(unitSelection.potentialUnits());

        potential.removeAll(unitSelection.availableUnits());

        unitSelection.potentialUnits().clear();
        unitSelection.potentialUnits().addAll(potential);
    }

    private UnitOptionsSelectionDTO retriveCurrentUnitOptions(Lease lease) {
        UnitOptionsSelectionDTO options = EntityFactory.create(UnitOptionsSelectionDTO.class);
        assert (!lease.unit().isNull());

        options.unit().set(lease.unit());

        fillCurrentProductItems(options, lease.currentTerm());
        loadDetachedProducts(options);

        return options;
    }

    private UnitOptionsSelectionDTO fillAvailableUnitOptions(UnitOptionsSelectionDTO options) {
        options.restrictions().set(retriveUnitOptionRestrictions(options.unit()));

        fillAvailableCatalogProducts(options);
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
    }

    private void fillAvailableCatalogProducts(UnitOptionsSelectionDTO options) {
        assert (!options.unit().isNull());
        EntityQueryCriteria<ProductItem> criteria = new EntityQueryCriteria<ProductItem>(ProductItem.class);
        // correct service type:
        criteria.eq(criteria.proto().product().holder().catalog().building().units(), options.unit());
        criteria.in(criteria.proto().product().holder().code().type(), ARCode.Type.Residential);
        criteria.eq(criteria.proto().product().holder().defaultCatalogItem(), Boolean.FALSE);
        criteria.isCurrent(criteria.proto().product().holder().version());
        criteria.eq(criteria.proto().product().holder().version().availableOnline(), Boolean.TRUE);
        // correct unit:
        criteria.eq(criteria.proto().element(), options.unit());

        ProductItem productItem = Persistence.service().retrieve(criteria);
        if (productItem != null) {
            options.selectedService().set(createBillableItem(productItem));
            fillCatalogFeatures(options, productItem.product().<Service.ServiceV> cast(), true);
        }
    }

    private void fillCatalogFeatures(UnitOptionsSelectionDTO options, Service.ServiceV service, boolean fillMandatory) {
        Persistence.service().retrieveMember(service.features());

        for (Feature feature : service.features()) {
            Persistence.service().retrieveMember(feature.version().items());

            for (ProductItem item : feature.version().items()) {
                Persistence.service().retrieve(item.product());
                if (feature.version().availableOnline().isBooleanTrue()) {
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
        return ServerSideFactory.create(LeaseFacade.class).createBillableItem(ProspectPortalContext.getLease(), productItem);
    }

}
