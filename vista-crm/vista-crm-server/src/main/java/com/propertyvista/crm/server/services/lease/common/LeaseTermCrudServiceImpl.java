/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-01
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.common;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.AbstractVersionedCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public class LeaseTermCrudServiceImpl extends AbstractVersionedCrudServiceDtoImpl<LeaseTerm, LeaseTermDTO> implements LeaseTermCrudService {

    public LeaseTermCrudServiceImpl() {
        super(LeaseTerm.class, LeaseTermDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected LeaseTermDTO init(InitializationData initializationData) {
        LeaseTermInitializationData initData = (LeaseTermInitializationData) initializationData;
        if (initData.isOffer().isBooleanTrue()) {
            return createOffer(initData.lease(), initData.termType().getValue());
        } else { // creating new Application/Lease:
            Lease lease = createNewLease(initData.leaseType().getValue(), initData.leaseStatus().getValue());
            LeaseTermDTO term = createTO(lease.currentTerm());
            term.isNewLease().setValue(true);

            lease.currentTerm().set(term);

            if (!initData.unit().isNull()) {
                setSelectedUnit(initData.unit(), term);
            }

            switch (initData.leaseStatus().getValue()) {
            case ExistingLease:
                term.carryforwardBalance().setValue(BigDecimal.ZERO);
                break;

            case NewLease:
                term.termFrom().setValue(new LogicalDate(SystemDateManager.getDate()));
                break;

            case Application:
                term.termFrom().setValue(new LogicalDate(SystemDateManager.getDate()));
                break;
            }

            return term;
        }
    }

    @Override
    protected void create(LeaseTerm dbo, LeaseTermDTO dto) {
        updateAdjustments(dbo);

        // check for newly created parent (lease/application):
        if (dto.isNewLease().isBooleanTrue()) {
            dbo.lease().currentTerm().set(dbo);
            dbo.lease().unit().set(dbo.unit());
            dbo.lease().billingAccount().carryforwardBalance().setValue(dto.carryforwardBalance().getValue());
            ServerSideFactory.create(LeaseFacade.class).persist(dbo.lease());
        } else {
            ServerSideFactory.create(LeaseFacade.class).persist(dbo);
        }
    }

    @Override
    protected void save(LeaseTerm dbo, LeaseTermDTO dto) {
        updateAdjustments(dbo);

        if (VersionedEntityUtils.equalsIgnoreVersion(dbo, dbo.lease().currentTerm())) {
            dbo.lease().currentTerm().set(dbo);
            dbo.lease().unit().set(dbo.unit());
            dbo.lease().billingAccount().carryforwardBalance().setValue(dto.carryforwardBalance().getValue());
            ServerSideFactory.create(LeaseFacade.class).persist(dbo.lease());
        } else {
            ServerSideFactory.create(LeaseFacade.class).persist(dbo);
        }
    }

    @Override
    protected void persist(LeaseTerm dbo, LeaseTermDTO in) {
        throw new Error("Facade should be used");
    }

    @Override
    protected void saveAsFinal(LeaseTerm entity) {
        ServerSideFactory.create(LeaseFacade.class).finalize(entity);
    }

    @Override
    protected void enhanceRetrieved(LeaseTerm in, LeaseTermDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(in, to, retrieveTarget);

        Persistence.service().retrieve(to.lease());
        Persistence.service().retrieve(to.version().utilities());

        to.carryforwardBalance().setValue(to.lease().billingAccount().carryforwardBalance().getValue());
        if (in.getPrimaryKey() != null) {
            Persistence.service().retrieve(to.version().tenants());
        }
        for (LeaseTermTenant item : to.version().tenants()) {
            LeaseParticipantUtils.retrieveLeaseTermEffectiveScreening(to.lease(), item, AttachLevel.ToStringMembers);
            fillPreauthorizedPayments(item);
        }

        if (in.getPrimaryKey() != null) {
            Persistence.service().retrieve(to.version().guarantors());
        }
        for (LeaseTermGuarantor item : to.version().guarantors()) {
            LeaseParticipantUtils.retrieveLeaseTermEffectiveScreening(to.lease(), item, AttachLevel.ToStringMembers);
        }

        loadDetachedProducts(to);

        if (!to.unit().isNull()) {
            Persistence.ensureRetrieve(to.unit().building(), AttachLevel.ToStringMembers);
            to.building().set(to.unit().building());

            if (retrieveTarget == RetrieveTarget.Edit) {
                // fill runtime editor data:
                fillServiceEligibilityData(to);
                fillserviceItems(to);
            }

            checkUnitMoveOut(to);

            setAgeOfMajority(to);
        }
    }

    @Override
    public void setSelectedUnit(AsyncCallback<LeaseTermDTO> callback, AptUnit unitId, LeaseTermDTO currentValue) {
        callback.onSuccess(setSelectedUnit(unitId, currentValue));
    }

    private LeaseTermDTO setSelectedUnit(AptUnit unitId, LeaseTermDTO currentValue) {
        ServerSideFactory.create(LeaseFacade.class).setUnit(currentValue, unitId);
        currentValue.building().set(currentValue.unit().building());

        loadDetachedProducts(currentValue);

        // fill runtime editor data:
        fillServiceEligibilityData(currentValue);
        fillserviceItems(currentValue);

        checkUnitMoveOut(currentValue);
        setAgeOfMajority(currentValue);

        return currentValue;
    }

    @Override
    public void setSelectedService(AsyncCallback<LeaseTermDTO> callback, ProductItem serviceId, LeaseTermDTO currentValue) {
        if (currentValue.lease().unit().isNull()) {
            currentValue.lease().unit().set(currentValue.unit());
        }

        ServerSideFactory.create(LeaseFacade.class).setService(currentValue, serviceId);

        loadDetachedProducts(currentValue);

        // fill runtime editor data:
        fillServiceEligibilityData(currentValue);

        callback.onSuccess(currentValue);
    }

    @Override
    public void createBillableItem(AsyncCallback<BillableItem> callback, ProductItem productItemId, LeaseTermDTO currentValue) {
        callback.onSuccess(ServerSideFactory.create(LeaseFacade.class).createBillableItem(currentValue.lease(), productItemId, currentValue.unit().building()));
    }

    @Override
    public void createDeposit(AsyncCallback<Deposit> callback, DepositType depositType, BillableItem item, LeaseTermDTO currentValue) {
        assert !currentValue.unit().isNull();
        callback.onSuccess(ServerSideFactory.create(DepositFacade.class).createDeposit(depositType, item, currentValue.unit().building()));
    }

    @Override
    public void acceptOffer(AsyncCallback<VoidSerializable> callback, Key entityId) {
        LeaseTerm offer = Persistence.secureRetrieve(LeaseTerm.class, entityId);
        ServerSideFactory.create(LeaseFacade.class).acceptOffer(offer.lease(), offer);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    // Internals:
    private void loadDetachedProducts(LeaseTermDTO dto) {
        Persistence.service().retrieve(dto.version().leaseProducts().serviceItem().item().product());

        for (BillableItem item : dto.version().leaseProducts().featureItems()) {
            Persistence.service().retrieve(item.item().product());
        }
    }

    private boolean fillServiceEligibilityData(LeaseTermDTO currentValue) {
        currentValue.selectedFeatureItems().clear();
        currentValue.selectedConcessions().clear();

        assert !currentValue.unit().isNull();

        Persistence.ensureRetrieve(currentValue.unit().building(), AttachLevel.Attached);

        Building building = currentValue.unit().building();
        if (building == null || building.isNull()) {
            return false;
        }

        ProductCatalog catalog = building.productCatalog();
        ProductItem serviceItem = currentValue.version().leaseProducts().serviceItem().item();
        if (catalog == null || serviceItem == null) {
            return false;
        }

        // find the service by Service item:
        Service.ServiceV selectedService = null;
        Persistence.service().retrieve(serviceItem.product());
        if (serviceItem.product().getInstanceValueClass().equals(Service.ServiceV.class)) {
            selectedService = serviceItem.product().cast();
        }

        // fill related:
        if (selectedService != null) {
            LogicalDate termFrom = (currentValue.termFrom().isNull() ? new LogicalDate(SystemDateManager.getDate()) : currentValue.termFrom().getValue());

            // features:
            Persistence.service().retrieve(selectedService.features());
            for (Feature feature : selectedService.features()) {
                if (feature.expiredFrom().isNull() || feature.expiredFrom().getValue().before(termFrom)) {
                    Persistence.service().retrieveMember(feature.version().items());
                    for (ProductItem item : feature.version().items()) {
                        Persistence.service().retrieve(item.product());
                        currentValue.selectedFeatureItems().add(item);
                    }
                }
            }

            // concessions:
            Persistence.service().retrieve(selectedService.concessions());
            currentValue.selectedConcessions().addAll(selectedService.concessions());
        }

        return (selectedService != null);
    }

    private void fillserviceItems(LeaseTermDTO currentValue) {
        currentValue.selectedServiceItems().clear();

        Persistence.ensureRetrieve(currentValue.unit().building(), AttachLevel.Attached);

        // use default product catalog items for specific cases:
        boolean useDefaultCatalog = (currentValue.unit().building().defaultProductCatalog().isBooleanTrue() || currentValue.lease().status().getValue() == Lease.Status.ExistingLease);
        LogicalDate termFrom = (currentValue.termFrom().isNull() ? new LogicalDate(SystemDateManager.getDate()) : currentValue.termFrom().getValue());

        EntityQueryCriteria<Service> serviceCriteria = new EntityQueryCriteria<Service>(Service.class);
        serviceCriteria.eq(serviceCriteria.proto().catalog(), currentValue.unit().building().productCatalog());
        serviceCriteria.eq(serviceCriteria.proto().code().type(), currentValue.lease().type());
        serviceCriteria.eq(serviceCriteria.proto().isDefaultCatalogItem(), useDefaultCatalog);
        serviceCriteria.or(PropertyCriterion.isNull(serviceCriteria.proto().expiredFrom()),
                PropertyCriterion.lt(serviceCriteria.proto().expiredFrom(), termFrom));
        serviceCriteria.isCurrent(serviceCriteria.proto().version());

        for (Service service : Persistence.service().query(serviceCriteria)) {
            EntityQueryCriteria<ProductItem> productCriteria = EntityQueryCriteria.create(ProductItem.class);
            productCriteria.add(PropertyCriterion.eq(productCriteria.proto().product(), service.version()));
            productCriteria.add(PropertyCriterion.eq(productCriteria.proto().element(), currentValue.unit()));

            currentValue.selectedServiceItems().addAll(Persistence.service().query(productCriteria));
        }

        // load products for UI presentation:
        for (ProductItem item : currentValue.selectedServiceItems()) {
            Persistence.service().retrieve(item, AttachLevel.ToStringMembers, false);
        }
    }

    private void fillPreauthorizedPayments(LeaseTermTenant item) {
        item.leaseParticipant().preauthorizedPayments().setAttachLevel(AttachLevel.Attached);
        item.leaseParticipant().preauthorizedPayments().clear();

        Persistence.ensureRetrieve(item.leaseTermV(), AttachLevel.Attached);
        Persistence.ensureRetrieve(item.leaseTermV().holder(), AttachLevel.Attached);
        Persistence.ensureRetrieve(item.leaseTermV().holder().lease(), AttachLevel.Attached);
        if (item.leaseTermV().equals(item.leaseTermV().holder().lease().currentTerm().version())) {
            item.leaseParticipant().preauthorizedPayments()
                    .addAll(ServerSideFactory.create(PaymentMethodFacade.class).retrieveAutopayAgreements(item.leaseParticipant()));
        }
    }

    private void updateAdjustments(LeaseTerm leaseTerm) {
        // ServiceItem Adjustments:
        updateAdjustments(leaseTerm.version().leaseProducts().serviceItem());

        // BillableItem Adjustments:
        for (BillableItem ci : leaseTerm.version().leaseProducts().featureItems()) {
            updateAdjustments(ci);
        }
    }

    private void updateAdjustments(BillableItem item) {
        for (BillableItemAdjustment adj : item.adjustments()) {
            // set creator:
            if (adj.createdWhen().isNull()) {
                adj.createdBy().set(CrmAppContext.getCurrentUserEmployee());
            }
        }
    }

    private void checkUnitMoveOut(LeaseTermDTO dto) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), dto.unit()));
        criteria.add(PropertyCriterion.in(criteria.proto().status(), Lease.Status.current()));
        criteria.add(PropertyCriterion.ne(criteria.proto().id(), dto.lease().getPrimaryKey()));
        criteria.isNotNull(criteria.proto().completion());
        criteria.isNull(criteria.proto().actualMoveOut());

        if (Persistence.service().exists(criteria)) {
            dto.unitMoveOutNote().setValue("Warning: This unit is not freed completely by previous tenant!");
        }
    }

    private void setAgeOfMajority(LeaseTermDTO dto) {
        dto.ageOfMajority().setValue(null);

        if (!dto.unit().isNull()) {
            RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(dto.unit(), RestrictionsPolicy.class);
            if (restrictionsPolicy.enforceAgeOfMajority().isBooleanTrue()) {
                dto.ageOfMajority().setValue(restrictionsPolicy.ageOfMajority().getValue());
            }
        }
    }

    private LeaseTermDTO createOffer(Lease leaseId, Type type) {
        LeaseTerm term = ServerSideFactory.create(LeaseFacade.class).createOffer(leaseId, type);

        LeaseTermDTO termDto = createTO(term);
        enhanceRetrieved(term, termDto, RetrieveTarget.Edit);

        return termDto;
    }

    private Lease createNewLease(ARCode.Type leaseType, Lease.Status initialStatus) {
        Lease newLease = EntityFactory.create(Lease.class);

        newLease.type().setValue(leaseType);
        newLease.status().setValue(initialStatus);
        ServerSideFactory.create(LeaseFacade.class).init(newLease);

        return newLease;
    }
}
