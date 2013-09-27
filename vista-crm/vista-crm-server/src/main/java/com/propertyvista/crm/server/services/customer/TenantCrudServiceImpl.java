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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.insurance.GeneralInsuranceFacade;
import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.media.InsuranceCertificateDocument;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.GeneralInsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.PropertyVistaIntegratedInsurance;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.dto.PreauthorizedPaymentDTO;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class TenantCrudServiceImpl extends LeaseParticipantCrudServiceBaseImpl<Tenant, TenantDTO> implements TenantCrudService {

    private static final Logger log = LoggerFactory.getLogger(TenantCrudServiceImpl.class);

    public TenantCrudServiceImpl() {
        super(Tenant.class, TenantDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Tenant bo, TenantDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        to.role().setValue(retrieveTenant(to.leaseTermV(), bo).role().getValue());

        Persistence.service().retrieve(to.customer().emergencyContacts());
        Persistence.service().retrieve(to.lease().unit().building());

        fillPreauthorizedPayments(to);
        fillInsuranceCertificates(to);

        if (retrieveTarget == RetrieveTarget.Edit) {
            TenantInsurancePolicy insurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(bo.lease().unit(),
                    TenantInsurancePolicy.class);
            if (insurancePolicy.requireMinimumLiability().isBooleanTrue()) {
                to.minimumRequiredLiability().setValue(insurancePolicy.minimumRequiredLiability().getValue());
            }

            RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(bo.lease().unit(),
                    RestrictionsPolicy.class);
            if (restrictionsPolicy.enforceAgeOfMajority().isBooleanTrue()) {
                to.ageOfMajority().setValue((to.role().getValue() != Role.Dependent) ? restrictionsPolicy.ageOfMajority().getValue() : null);
            }
        }

        if (VistaFeatures.instance().yardiIntegration()) {
            LeaseTerm leaseTerm = Persistence.service().retrieve(LeaseTerm.class, to.leaseTermV().holder().getPrimaryKey());
            boolean isPotentialTenant = leaseTerm.status().getValue() != LeaseTerm.Status.Current & leaseTerm.status().getValue() != LeaseTerm.Status.Historic;
            to.isPotentialTenant().setValue(isPotentialTenant);
        }

        to.isMoveOutWithinNextBillingCycle().setValue(ServerSideFactory.create(LeaseFacade.class).isMoveOutWithinNextBillingCycle(bo.lease()));
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
        updateInsuranceCertificates(tenantDto);

    }

    private LeaseTermTenant retrieveTenant(LeaseTerm.LeaseTermV termV, Tenant leaseCustomer) {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), leaseCustomer));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV(), termV));
        return Persistence.service().retrieve(criteria);
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

        dto.nextScheduledPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextPreauthorizedPaymentDate(dto.lease()));
        dto.paymentCutOffDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getPreauthorizedPaymentCutOffDate(dto.lease()));
    }

    private void savePreauthorizedPayments(TenantDTO dto) {
        List<PreauthorizedPayment> paps = new ArrayList<PreauthorizedPayment>();
        for (PreauthorizedPaymentDTO papDTO : dto.preauthorizedPayments()) {
            updateCoveredItems(papDTO);
            paps.add(new PapConverter().createBO(papDTO));
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
        PreauthorizedPaymentDTO papDto = new PapConverter().createTO(pap);

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

    private class PapConverter extends EntityBinder<PreauthorizedPayment, PreauthorizedPaymentDTO> {

        protected PapConverter() {
            super(PreauthorizedPayment.class, PreauthorizedPaymentDTO.class);
        }

        @Override
        protected void bind() {
            bindCompleteObject();
        }
    }

    private void fillInsuranceCertificates(TenantDTO dto) {
        dto.insuranceCertificates().addAll(
                ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceCertificates(
                        EntityFactory.createIdentityStub(Tenant.class, dto.getPrimaryKey()), true));
    }

    private void updateInsuranceCertificates(TenantDTO tenantDto) {
        List<InsuranceCertificate> oldInsuranceCertificates = ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceCertificates(
                EntityFactory.createIdentityStub(Tenant.class, tenantDto.getPrimaryKey()), true);

        Collection<InsuranceCertificate> deletedInsuranceCertificates = CollectionUtils.subtract(oldInsuranceCertificates, tenantDto.insuranceCertificates());
        for (InsuranceCertificate insuranceCertificate : tenantDto.insuranceCertificates()) {
            // skip certificates that cannot be updated by pmc
            if ((insuranceCertificate instanceof PropertyVistaIntegratedInsurance) | insuranceCertificate.isManagedByTenant().isBooleanTrue()) {
                continue;
            }
            for (Object document : insuranceCertificate.documents()) {
                ((InsuranceCertificateDocument) document).owner().set(insuranceCertificate);
            }
            if (insuranceCertificate.getPrimaryKey() == null && (insuranceCertificate instanceof GeneralInsuranceCertificate)) {
                ServerSideFactory.create(GeneralInsuranceFacade.class).createGeneralTenantInsurance(
                        EntityFactory.createIdentityStub(Tenant.class, tenantDto.getPrimaryKey()), (GeneralInsuranceCertificate) insuranceCertificate);
            } else {
                // check that nobody is tampering the PV/Tenant managed insurance certificates (we have to validate the type of the data based on pk from our db and don't rely on flags from outside)
                InsuranceCertificate oldInsuranceCertificate = Persistence.service().retrieve(InsuranceCertificate.class, insuranceCertificate.getPrimaryKey());
                if (!(oldInsuranceCertificate instanceof PropertyVistaIntegratedInsurance) || !oldInsuranceCertificate.isManagedByTenant().isBooleanTrue()) {
                    Persistence.secureSave(insuranceCertificate);
                }
            }

        }

        for (InsuranceCertificate deletedCertificate : deletedInsuranceCertificates) {
            if ((deletedCertificate instanceof PropertyVistaIntegratedInsurance) || deletedCertificate.isManagedByTenant().isBooleanTrue()) {
                throw new SecurityViolationException("it's forbidden to delete property vista integrated or user managed insurance certificates");
            }
            ServerSideFactory.create(GeneralInsuranceFacade.class).deleteGeneralInsurance((GeneralInsuranceCertificate) deletedCertificate.cast());
        }
    }
}
