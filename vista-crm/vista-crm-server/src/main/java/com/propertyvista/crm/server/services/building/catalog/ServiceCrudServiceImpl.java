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
package com.propertyvista.crm.server.services.building.catalog;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.productcatalog.ProductCatalogFacade;
import com.propertyvista.crm.rpc.services.building.catalog.ServiceCrudService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductDeposit.ValueType;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;

public class ServiceCrudServiceImpl extends AbstractCrudServiceImpl<Service> implements ServiceCrudService {

    public ServiceCrudServiceImpl() {
        super(Service.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected Service init(InitializationData initializationData) {
        ServiceInitializationdata initData = (ServiceInitializationdata) initializationData;

        Service entity = EntityFactory.create(Service.class);
        entity.code().set(initData.code());
        entity.catalog().setPrimaryKey(initData.parent().getPrimaryKey());
        entity.catalog().setValueDetached();

        entity.version().depositLMR().enabled().setValue(false);
        entity.version().depositLMR().depositType().setValue(DepositType.LastMonthDeposit);
        entity.version().depositLMR().chargeCode().set(getARCode(ARCode.Type.DepositLMR));
        entity.version().depositLMR().valueType().setValue(ValueType.Percentage);
        entity.version().depositLMR().value().setValue(BigDecimal.ONE);
        entity.version().depositLMR().description().setValue(DepositType.LastMonthDeposit.toString());

        entity.version().depositMoveIn().enabled().setValue(false);
        entity.version().depositMoveIn().depositType().setValue(DepositType.MoveInDeposit);
        entity.version().depositMoveIn().chargeCode().set(getARCode(ARCode.Type.DepositMoveIn));
        entity.version().depositMoveIn().valueType().setValue(ValueType.Percentage);
        entity.version().depositMoveIn().value().setValue(BigDecimal.ONE);
        entity.version().depositMoveIn().description().setValue(DepositType.MoveInDeposit.toString());

        entity.version().depositSecurity().enabled().setValue(false);
        entity.version().depositSecurity().depositType().setValue(DepositType.SecurityDeposit);
        entity.version().depositSecurity().chargeCode().set(getARCode(ARCode.Type.DepositSecurity));
        entity.version().depositSecurity().valueType().setValue(ValueType.Percentage);
        entity.version().depositSecurity().value().setValue(BigDecimal.ONE);
        entity.version().depositSecurity().description().setValue(DepositType.SecurityDeposit.toString());

        return entity;
    }

    private ARCode getARCode(ARCode.Type type) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().type(), type);
        return Persistence.service().retrieve(criteria);
    }

    @Override
    protected void enhanceRetrieved(Service dbo, Service to, RetrieveTarget retrieveTarget) {
        // Load detached data:

        /*
         * catalog retrieving is necessary for building element selection
         * (lister filtering by catalog().building() in @link ServiceItemFolder.AddItem())
         * and building element filtering in ServiceItemEditor
         */
        Persistence.service().retrieveMember(to.catalog());

        Persistence.service().retrieveMember(to.version().items());
        Persistence.service().retrieveMember(to.version().features());
        Persistence.service().retrieveMember(to.version().concessions());

        // next level:
        for (ProductItem item : to.version().items()) {
            Persistence.ensureRetrieve(item.element(), AttachLevel.Attached);
        }
        for (Feature feature : to.version().features()) {
            Persistence.service().retrieveMember(feature.version().items());
            // next level:
            for (ProductItem item : feature.version().items()) {
                Persistence.ensureRetrieve(item.element(), AttachLevel.Attached);
            }
        }
    }

    @Override
    public void retrieveCatalog(AsyncCallback<ProductCatalog> callback, Key entityId) {
        callback.onSuccess(Persistence.service().retrieve(ProductCatalog.class, entityId));
    }

    @Override
    protected void persist(Service bo, Service to) {
        super.persist(bo, to);

        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitMarketPrice(bo);
    }
}
