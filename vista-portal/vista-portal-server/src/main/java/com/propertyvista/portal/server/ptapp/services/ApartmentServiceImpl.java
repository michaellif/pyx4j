/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceCatalog;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;

public class ApartmentServiceImpl implements ApartmentService {

    private final static I18n i18n = I18n.get(ApartmentServiceImpl.class);

    private final static Logger log = LoggerFactory.getLogger(ApartmentServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<ApartmentInfoDTO> callback, Key tenantId) {
        callback.onSuccess(retrieveData());
    }

    @Override
    public void save(AsyncCallback<ApartmentInfoDTO> callback, ApartmentInfoDTO entity) {

        // update agreed items:

        Lease lease = PtAppContext.getCurrentUserLease();
        for (Iterator<ChargeItem> iter = lease.serviceAgreement().featureItems().iterator(); iter.hasNext();) {
            ChargeItem item = iter.next();
            if (item.item().type().type().getValue().equals(ServiceItemType.Type.feature)) {
                switch (item.item().type().featureType().getValue()) {
                case utility:
                    break;
                case pet:
                case parking:
                case locker:
                default:
                    iter.remove(); // remove all non-utility items
                }
            }
        }

        // add user-selected ones: 
        lease.serviceAgreement().featureItems().addAll(entity.agreedPets());
        lease.serviceAgreement().featureItems().addAll(entity.agreedParking());
        lease.serviceAgreement().featureItems().addAll(entity.agreedStorage());
        lease.serviceAgreement().featureItems().addAll(entity.agreedOther());

        // save changes: 
        for (ChargeItem item : lease.serviceAgreement().featureItems()) {
            if (!item.extraData().isNull()) {
                Persistence.service().merge(item.extraData());
            }
        }

// actually, just feature list should be saved:        
//        Persistence.service().merge(lease.serviceAgreement().featureItems());
        Persistence.service().merge(lease);
        callback.onSuccess(entity);
    }

    public ApartmentInfoDTO retrieveData() {
        Lease lease = PtAppContext.getCurrentUserLease();
        if (!Persistence.service().retrieve(lease.unit())) {
            throw new UserRuntimeException("There is no Unit selected!?.");
        }
        if (!Persistence.service().retrieve(lease.unit().floorplan())) {
            throw new UserRuntimeException("There is no unit Floor plan data!?.");
        }
        if (!Persistence.service().retrieve(lease.unit().belongsTo())) {
            throw new UserRuntimeException("There is no unit building data!?.");
        }

        // fill DTO:
        ApartmentInfoDTO aptInfo = EntityFactory.create(ApartmentInfoDTO.class);

        aptInfo.floorplan().setValue(lease.unit().floorplan().marketingName().getValue());
        aptInfo.bedrooms().setValue(lease.unit().floorplan().bedrooms().getStringView());

        if (lease.unit().floorplan().dens().getValue() == 1) {
            aptInfo.bedrooms().setValue(aptInfo.bedrooms().getValue() + " + " + lease.unit().floorplan().dens().getStringView() + i18n.tr(" den"));
        } else if (lease.unit().floorplan().dens().getValue() > 1) {
            aptInfo.bedrooms().setValue(aptInfo.bedrooms().getValue() + " + " + lease.unit().floorplan().dens().getStringView() + i18n.tr(" dens"));
        }

        aptInfo.bathrooms().setValue(lease.unit().floorplan().bathrooms().getStringView());
        if (lease.unit().floorplan().halfBath().getValue() == 1) {
            aptInfo.bathrooms()
                    .setValue(aptInfo.bathrooms().getValue() + " + " + lease.unit().floorplan().halfBath().getStringView() + i18n.tr(" separate WC"));
        } else if (lease.unit().floorplan().halfBath().getValue() > 1) {
            aptInfo.bathrooms().setValue(
                    aptInfo.bathrooms().getValue() + " + " + lease.unit().floorplan().halfBath().getStringView() + i18n.tr(" separate WCs"));
        }

        aptInfo.address().set(lease.unit().belongsTo().info().address().clone(AddressStructured.class));
        aptInfo.address().suiteNumber().setValue(lease.unit().info().number().getValue());

        // find picture:
        Persistence.service().retrieve(lease.unit().belongsTo().media());
        for (Media media : lease.unit().belongsTo().media()) {
            if (Media.Type.file.equals(media.type())
                    && (PublicVisibilityType.tenant == media.visibility().getValue() || PublicVisibilityType.global == media.visibility().getValue())) {
                aptInfo.picture().set(media);
                break;
            }
        }

        // serviceCatalog processing:
        fillServiceItems(aptInfo, lease.unit().belongsTo(), lease);

        // Lease data:
        aptInfo.leaseFrom().setValue(lease.leaseFrom().getValue());
        aptInfo.leaseTo().setValue(lease.leaseTo().getValue());
        aptInfo.unitRent().setValue(lease.serviceAgreement().serviceItem().item().price().getValue());

        return aptInfo;
    }

    private ServiceCatalog syncBuildingServiceCatalog(Building building) {

        // load detached entities:
        Persistence.service().retrieve(building.serviceCatalog());

        // update service catalogue double-reference lists:
        EntityQueryCriteria<Service> serviceCriteria = EntityQueryCriteria.create(Service.class);
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().catalog(), building.serviceCatalog()));
        List<Service> services = Persistence.service().query(serviceCriteria);
        building.serviceCatalog().services().clear();
        building.serviceCatalog().services().addAll(services);

        // load detached data:
        for (Service item : services) {
            Persistence.service().retrieve(item.items());
            Persistence.service().retrieve(item.features());
            for (ServiceFeature fi : item.features()) {
                Persistence.service().retrieve(fi.feature().items());
            }
            Persistence.service().retrieve(item.concessions());
        }
//        
//  Currently not used here:        
//
//        EntityQueryCriteria<Feature> featureCriteria = EntityQueryCriteria.create(Feature.class);
//        featureCriteria.add(PropertyCriterion.eq(featureCriteria.proto().catalog(), building.serviceCatalog()));
//        List<Feature> features = Persistence.service().query(featureCriteria);
//        building.serviceCatalog().features().clear();
//        building.serviceCatalog().features().addAll(features);
//
//        EntityQueryCriteria<Concession> concessionCriteria = EntityQueryCriteria.create(Concession.class);
//        concessionCriteria.add(PropertyCriterion.eq(concessionCriteria.proto().catalog(), building.serviceCatalog()));
//        List<Concession> concessions = Persistence.service().query(concessionCriteria);
//        building.serviceCatalog().concessions().clear();
//        building.serviceCatalog().concessions().addAll(concessions);

        return building.serviceCatalog();
    }

    private void fillServiceItems(ApartmentInfoDTO entity, Building building, Lease lease) {

        entity.includedUtilities().clear();
        entity.externalUtilities().clear();

        entity.agreedUtilities().clear();
        entity.availableUtilities().clear();

        entity.agreedPets().clear();
        entity.availablePets().clear();
        entity.agreedParking().clear();
        entity.availableParking().clear();
        entity.agreedStorage().clear();
        entity.availableStorage().clear();
        entity.agreedOther().clear();
        entity.availableOther().clear();

        entity.concessions().clear();

        syncBuildingServiceCatalog(lease.unit().belongsTo());

        entity.includedUtilities().addAll(building.serviceCatalog().includedUtilities());
        entity.externalUtilities().addAll(building.serviceCatalog().externalUtilities());

        // fill agreed items:
        for (ChargeItem item : lease.serviceAgreement().featureItems()) {
            if (item.item().type().type().getValue().equals(ServiceItemType.Type.feature)) {
                PriceCalculationHelpers.calculateChargeItemAdjustments(item);
                switch (item.item().type().featureType().getValue()) {
                case utility:
                    entity.agreedUtilities().add(item);
                    break;
                case pet:
                    entity.agreedPets().add(item);
                    break;
                case parking:
                    entity.agreedParking().add(item);
                    break;
                case locker:
                    entity.agreedStorage().add(item);
                    break;
                default:
                    entity.agreedOther().add(item);
                }
            }
        }

        // fill available items:
        for (Service service : building.serviceCatalog().services()) {
            if (service.type().equals(lease.type())) {
                for (ServiceFeature feature : service.features()) {
                    for (ServiceItem item : feature.feature().items()) {
                        switch (item.type().featureType().getValue()) {
                        case utility:
                            if (!entity.includedUtilities().contains(item.type()) && !entity.externalUtilities().contains(item.type())) {
                                entity.availableUtilities().add(item);
                            }
                            break;
                        case pet:
                            entity.availablePets().add(item);
                            break;
                        case parking:
                            entity.availableParking().add(item);
                            break;
                        case locker:
                            entity.availableStorage().add(item);
                            break;
                        default:
                            entity.availableOther().add(item);
                        }
                    }
                }
            }
        }

        // fill concessions:
        for (ServiceConcession consession : lease.serviceAgreement().concessions()) {
            entity.concessions().add(consession.concession());
        }
    }
}
