/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.media.InsuranceCertificateDocument;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceGeneric;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.dto.PreauthorizedPaymentDTO;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantInsuranceCertificateDTO;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.shared.config.VistaFeatures;

public class TenantCrudServiceImpl extends LeaseParticipantCrudServiceBaseImpl<Tenant, TenantDTO> implements TenantCrudService {

    private static final Logger log = LoggerFactory.getLogger(TenantCrudServiceImpl.class);

    public TenantCrudServiceImpl() {
        super(Tenant.class, TenantDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Tenant entity, TenantDTO dto, RetrieveTarget RetrieveTarget) {
        super.enhanceRetrieved(entity, dto, RetrieveTarget);

        dto.role().setValue(retrieveTenant(dto.leaseTermV(), entity).role().getValue());

        Persistence.service().retrieve(dto.customer().emergencyContacts());
        Persistence.service().retrieve(dto.lease().unit().building());
        for (PaymentMethod method : dto.paymentMethods()) {
            Persistence.service().retrieve(method.creator());
        }

        fillPreauthorizedPayments(dto);

        dto.insuranceCertificates().addAll(retrieveInsuranceCertificates(entity));
        // unattach tenant related information since we don't want to send again
        for (InsuranceCertificate insuranceCertificate : dto.insuranceCertificates()) {
            insuranceCertificate.tenant().set(insuranceCertificate.tenant().createIdentityStub());
        }

        if (RetrieveTarget == RetrieveTarget.Edit) {
            TenantInsurancePolicy insurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(entity.lease().unit(),
                    TenantInsurancePolicy.class);
            if (insurancePolicy.requireMinimumLiability().isBooleanTrue()) {
                dto.minimumRequiredLiability().setValue(insurancePolicy.minimumRequiredLiability().getValue());
            }

            RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(entity.lease().unit(),
                    RestrictionsPolicy.class);
            if (restrictionsPolicy.enforceAgeOfMajority().isBooleanTrue()) {
                dto.ageOfMajority().setValue((dto.role().getValue() != Role.Dependent) ? restrictionsPolicy.ageOfMajority().getValue() : null);
            }
        }

        if (VistaFeatures.instance().yardiIntegration()) {
            LeaseTerm leaseTerm = Persistence.service().retrieve(LeaseTerm.class, dto.leaseTermV().holder().getPrimaryKey());
            boolean isPotentialTenant = leaseTerm.status().getValue() != LeaseTerm.Status.Current & leaseTerm.status().getValue() != LeaseTerm.Status.Historic;
            dto.isPotentialTenant().setValue(isPotentialTenant);
        }

        dto.isMoveOutWithinNextBillingCycle().setValue(ServerSideFactory.create(LeaseFacade.class).isMoveOutWithinNextBillingCycle(entity.lease()));
    }

    @Override
    protected void enhanceListRetrieved(Tenant entity, TenantDTO dto) {
        super.enhanceListRetrieved(entity, dto);
        Persistence.service().retrieve(dto.lease().unit().building());
        dto.role().setValue(retrieveTenant(dto.leaseTermV(), entity).role().getValue());
    }

    @Override
    protected void persist(Tenant tenant, TenantDTO tenantDto) {
        super.persist(tenant, tenantDto);

        savePreauthorizedPayments(tenantDto);

        EntityQueryCriteria<InsuranceCertificate> oldInsuranceCertificatesCriteria = EntityQueryCriteria.create(InsuranceCertificate.class);
        oldInsuranceCertificatesCriteria.eq(oldInsuranceCertificatesCriteria.proto().tenant(), tenant.getPrimaryKey());
        List<InsuranceCertificate> oldInsuranceCertificates = Persistence.service().query(oldInsuranceCertificatesCriteria);
        List<InsuranceCertificate> deletedInsuranceCertificates = new ArrayList<InsuranceCertificate>();

        for (InsuranceCertificate oldCertificate : oldInsuranceCertificates) {
            boolean isDeleted = true;
            found: for (TenantInsuranceCertificateDTO newCertificate : tenantDto.insuranceCertificates()) {
                if (oldCertificate.getPrimaryKey().equals(newCertificate.getPrimaryKey())) {
                    isDeleted = false;
                    break found;
                }
            }
            if (isDeleted) {
                deletedInsuranceCertificates.add(oldCertificate);
            }
        }
        // TODO support delete insurance
        for (TenantInsuranceCertificateDTO insuranceCertificate : tenantDto.insuranceCertificates()) {
            if (insuranceCertificate.isPropertyVistaIntegratedProvider().isBooleanTrue() | insuranceCertificate.isManagedByTenant().isBooleanTrue()) {
                // property vista integrated insurance certificates should be managed by software, i.e. the users have no right to manually modify such insurance certificates
                // tenant is managed by tenant alos are managed by tenant... so no pmc can touch it

                continue;
            }
            // we use insurance generic for those insurances that are managed by pmc
            InsuranceGeneric insuranceGeneric = insuranceCertificate.duplicate(InsuranceCertificate.class).duplicate(InsuranceGeneric.class);
            // workaround since 'duplicate' seems not to do this
            for (InsuranceCertificateDocument document : insuranceGeneric.documents()) {
                document.owner().set(insuranceGeneric);
            }

            if (insuranceCertificate.getPrimaryKey() == null) {
                // This is new                               
                insuranceGeneric.tenant().set(tenant.createIdentityStub());
            } else {
                // check that nobody is tampering the PV/Tenant managed insurance certificates:
                InsuranceCertificate preUpdated = Persistence.service().retrieve(InsuranceCertificate.class, insuranceCertificate.getPrimaryKey());
                if (preUpdated.isPropertyVistaIntegratedProvider().isBooleanTrue() | insuranceCertificate.isManagedByTenant().isBooleanTrue()) {
                    log.warn(SimpleMessageFormat.format("Evil CRM user {0} has tried to override insurance setting for insurance id={1}", VistaContext
                            .getCurrentUser().getPrimaryKey(), preUpdated.getPrimaryKey()));
                    throw new Error();
                }
            }
            Persistence.secureSave(insuranceGeneric);
        }

        for (InsuranceCertificate deletedCertificate : deletedInsuranceCertificates) {
            if (deletedCertificate.isPropertyVistaIntegratedProvider().isBooleanTrue() | deletedCertificate.isManagedByTenant().isBooleanTrue()) {
                throw new SecurityViolationException("it's forbidden to delete property vista integrated or user managed insurance certificates");
            }
            Persistence.service().delete(deletedCertificate.getInstanceValueClass(), deletedCertificate.getPrimaryKey());
        }
    }

    private LeaseTermTenant retrieveTenant(LeaseTerm.LeaseTermV termV, Tenant leaseCustomer) {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), leaseCustomer));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV(), termV));
        return Persistence.service().retrieve(criteria);
    }

    private List<TenantInsuranceCertificateDTO> retrieveInsuranceCertificates(Tenant tenantId) {
        EntityQueryCriteria<InsuranceCertificate> tenantInsuranceCriteria = EntityQueryCriteria.create(InsuranceCertificate.class);
        tenantInsuranceCriteria.eq(tenantInsuranceCriteria.proto().tenant(), tenantId);
        tenantInsuranceCriteria.desc(tenantInsuranceCriteria.proto().inceptionDate());
        List<InsuranceCertificate> certificates = Persistence.service().query(tenantInsuranceCriteria);
        List<TenantInsuranceCertificateDTO> dtoCertificates = new ArrayList<TenantInsuranceCertificateDTO>();
        for (InsuranceCertificate c : certificates) {
            dtoCertificates.add(c.duplicate(TenantInsuranceCertificateDTO.class));
        }
        return dtoCertificates;
    }

    @Override
    public void createPreauthorizedPayment(AsyncCallback<PreauthorizedPaymentDTO> callback, Tenant tenantId) {
        PreauthorizedPaymentDTO papDto = EntityFactory.create(PreauthorizedPaymentDTO.class);

        papDto.tenant().set(tenantId);

        fillCoveredItemsDto(papDto);

        callback.onSuccess(papDto);
    }

    @Override
    public void getPortalAccessInformation(AsyncCallback<TenantPortalAccessInformationDTO> callback, Tenant tenantId) {
        Tenant tenant = Persistence.secureRetrieve(Tenant.class, tenantId.getPrimaryKey());
        Persistence.service().retrieveMember(tenant.lease());
        Persistence.service().retrieveMember(tenant.lease().unit().building());
        TenantPortalAccessInformationDTO dto = ExportTenantsPortalSecretsDeferredProcess.convert(tenant);
        callback.onSuccess(dto);
    }

    private void fillPreauthorizedPayments(TenantDTO dto) {
        Persistence.ensureRetrieve(dto.lease(), AttachLevel.Attached);

        for (PreauthorizedPayment pap : ServerSideFactory.create(PaymentMethodFacade.class).retrievePreauthorizedPayments(
                EntityFactory.createIdentityStub(Tenant.class, dto.getPrimaryKey()))) {
            dto.preauthorizedPayments().add(createPreauthorizedPaymentDto(pap));
        }

        dto.nextScheduledPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextScheduledPreauthorizedPaymentDate(dto.lease()));
        dto.paymentCutOffDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getPreauthorizedPaymentCutOffDate(dto.lease()));
    }

    private void savePreauthorizedPayments(TenantDTO dto) {
        List<PreauthorizedPayment> paps = new ArrayList<PreauthorizedPayment>();
        for (PreauthorizedPaymentDTO papDTO : dto.preauthorizedPayments()) {
            updateCoveredItems(papDTO);
            paps.add(new PapConverter().createDBO(papDTO));
        }

        // delete payment methods removed in UI:
        for (PreauthorizedPayment pap : ServerSideFactory.create(PaymentMethodFacade.class).retrievePreauthorizedPayments(
                EntityFactory.createIdentityStub(Tenant.class, dto.getPrimaryKey()))) {
            if (!paps.contains(pap)) {
                ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(pap);
            }
        }

        // save new/edited ones:
        for (PreauthorizedPayment pap : paps) {
            // remove zero covered items:
            Iterator<PreauthorizedPaymentCoveredItem> iterator = pap.coveredItems().iterator();
            while (iterator.hasNext()) {
                PreauthorizedPaymentCoveredItem item = iterator.next();
                if (item.amount().getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    iterator.remove();
                    if (item.getPrimaryKey() != null) {
                        Persistence.service().delete(item);
                    }
                }
            }

            ServerSideFactory.create(PaymentMethodFacade.class).persistPreauthorizedPayment(pap,
                    EntityFactory.createIdentityStub(Tenant.class, dto.getPrimaryKey()));
        }
    }

    private PreauthorizedPaymentDTO createPreauthorizedPaymentDto(PreauthorizedPayment pap) {
        PreauthorizedPaymentDTO papDto = new PapConverter().createDTO(pap);

        Persistence.service().retrieve(papDto.creator());

        updateCoveredItemsDto(papDto);
        fillCoveredItemsDto(papDto);

        return papDto;
    }

    private void fillCoveredItemsDto(PreauthorizedPaymentDTO papDto) {
        if (!papDto.expiring().isNull()) {
            return; // do not fill-up expired paps!.. 
        }

        Persistence.ensureRetrieve(papDto.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(papDto.tenant().lease(), AttachLevel.Attached);

        Lease lease = papDto.tenant().lease();

        if (!isCoveredItemExist(papDto, lease.currentTerm().version().leaseProducts().serviceItem())) {
            papDto.coveredItemsDTO().add(
                    createCoveredItemDto(lease.currentTerm().version().leaseProducts().serviceItem(), lease, papDto.getPrimaryKey() == null));
        }

        for (BillableItem billableItem : lease.currentTerm().version().leaseProducts().featureItems()) {
            Persistence.ensureRetrieve(billableItem.item().product(), AttachLevel.Attached);
            //@formatter:off
            if (!ARCode.Type.nonReccuringFeatures().contains(billableItem.item().product().holder().type().getValue())                                          // recursive
                && (billableItem.expirationDate().isNull() || billableItem.expirationDate().getValue().after(new LogicalDate(SystemDateManager.getDate())))     // non-expired 
                && !isCoveredItemExist(papDto, billableItem)) {                                                                                                 // absent
            //@formatter:on
                papDto.coveredItemsDTO().add(createCoveredItemDto(billableItem, lease, papDto.getPrimaryKey() == null));
            }
        }
    }

    private boolean isCoveredItemExist(PreauthorizedPaymentDTO papDto, BillableItem billableItem) {
        for (PreauthorizedPaymentCoveredItem item : papDto.coveredItemsDTO()) {
            if (item.billableItem().id().equals(billableItem.id())) {
                return true;
            }
        }
        return false;
    }

    private PreauthorizedPaymentCoveredItemDTO createCoveredItemDto(BillableItem billableItem, Lease lease, boolean isNewPap) {
        PreauthorizedPaymentCoveredItemDTO item = EntityFactory.create(PreauthorizedPaymentCoveredItemDTO.class);

        // calculate already covered amount by other tenants/paps: 
        EntityQueryCriteria<PreauthorizedPaymentCoveredItem> criteria = new EntityQueryCriteria<PreauthorizedPaymentCoveredItem>(
                PreauthorizedPaymentCoveredItem.class);
        criteria.eq(criteria.proto().pap().tenant().lease(), lease);
        criteria.eq(criteria.proto().billableItem().uid(), billableItem.uid());
        criteria.eq(criteria.proto().pap().isDeleted(), Boolean.FALSE);
        criteria.isNull(criteria.proto().pap().expiring());

        item.covered().setValue(BigDecimal.ZERO);
        for (PreauthorizedPaymentCoveredItem papci : Persistence.secureQuery(criteria)) {
            item.covered().setValue(item.covered().getValue().add(papci.amount().getValue()));
        }

        BigDecimal itemPrice = billableItem.agreedPrice().getValue();
        if (itemPrice.compareTo(BigDecimal.ZERO) != 0) {
            item.amount().setValue(isNewPap ? itemPrice.subtract(item.covered().getValue()) : BigDecimal.ZERO);
            item.percent().setValue(item.amount().getValue().divide(itemPrice, 2, RoundingMode.FLOOR));
        } else {
            item.amount().setValue(BigDecimal.ZERO);
            item.percent().setValue(BigDecimal.ONE);
        }

        item.billableItem().set(billableItem);

        return item;
    }

    private void updateCoveredItemsDto(PreauthorizedPaymentDTO papDto) {
        Persistence.ensureRetrieve(papDto.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(papDto.tenant().lease(), AttachLevel.Attached);

        papDto.coveredItemsDTO().clear();
        for (PreauthorizedPaymentCoveredItem item : papDto.coveredItems()) {
            PreauthorizedPaymentCoveredItemDTO itemDto = item.duplicate(PreauthorizedPaymentCoveredItemDTO.class);
            papDto.coveredItemsDTO().add(updateCoveredItemDto(itemDto, papDto.tenant().lease()));
        }
    }

    private PreauthorizedPaymentCoveredItemDTO updateCoveredItemDto(PreauthorizedPaymentCoveredItemDTO item, Lease lease) {
        // calculate already covered amount by other tenants/paps: 
        EntityQueryCriteria<PreauthorizedPaymentCoveredItem> criteria = new EntityQueryCriteria<PreauthorizedPaymentCoveredItem>(
                PreauthorizedPaymentCoveredItem.class);
        criteria.ne(criteria.proto().pap(), item.pap());
        criteria.eq(criteria.proto().pap().tenant().lease(), lease);
        criteria.eq(criteria.proto().billableItem().uid(), item.billableItem().uid());
        criteria.eq(criteria.proto().pap().isDeleted(), Boolean.FALSE);
        criteria.isNull(criteria.proto().pap().expiring());

        item.covered().setValue(BigDecimal.ZERO);
        for (PreauthorizedPaymentCoveredItem papci : Persistence.secureQuery(criteria)) {
            item.covered().setValue(item.covered().getValue().add(papci.amount().getValue()));
        }

        BigDecimal itemPrice = item.billableItem().agreedPrice().getValue();
        if (itemPrice.compareTo(BigDecimal.ZERO) != 0) {
            item.percent().setValue(item.amount().getValue().divide(itemPrice, 2, RoundingMode.FLOOR));
        } else {
            item.percent().setValue(BigDecimal.ONE);
        }

        return item;
    }

    private void updateCoveredItems(PreauthorizedPaymentDTO papDto) {
        papDto.coveredItems().clear();
        for (PreauthorizedPaymentCoveredItemDTO item : papDto.coveredItemsDTO()) {
            if (item.amount().getValue().compareTo(BigDecimal.ZERO) > 0) {
                papDto.coveredItems().add(item.duplicate(PreauthorizedPaymentCoveredItem.class));
            }
        }
    }

    private class PapConverter extends EntityDtoBinder<PreauthorizedPayment, PreauthorizedPaymentDTO> {

        protected PapConverter() {
            super(PreauthorizedPayment.class, PreauthorizedPaymentDTO.class);
        }

        @Override
        protected void bind() {
            bindCompleteDBO();
        }
    }
}
