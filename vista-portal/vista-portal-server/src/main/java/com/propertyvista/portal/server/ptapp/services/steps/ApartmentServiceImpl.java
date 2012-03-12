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
package com.propertyvista.portal.server.ptapp.services.steps;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.policy.policies.MiscPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.ApartmentService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.services.util.ApplicationProgressMgr;
import com.propertyvista.portal.server.ptapp.services.util.DigitalSignatureMgr;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;
import com.propertyvista.server.common.policy.PolicyManager;
import com.propertyvista.server.common.util.AddressConverter;

public class ApartmentServiceImpl implements ApartmentService {

    private final static I18n i18n = I18n.get(ApartmentServiceImpl.class);

    private final static Logger log = LoggerFactory.getLogger(ApartmentServiceImpl.class);

    private final static AddressConverter.StructuredToSimpleAddressConverter ADDRESS_CONVERTER = new AddressConverter.StructuredToSimpleAddressConverter();

    @Override
    public void retrieve(AsyncCallback<ApartmentInfoDTO> callback, Key tenantId) {
        callback.onSuccess(retrieveData());
    }

    @Override
    public void save(AsyncCallback<ApartmentInfoDTO> callback, ApartmentInfoDTO entity) {
        // update agreed items:
        Lease lease = PtAppContext.getCurrentUserLease();
        for (Iterator<BillableItem> iter = lease.leaseProducts().featureItems().iterator(); iter.hasNext();) {
            BillableItem item = iter.next();
            if (item.item().type().type().getValue().equals(ProductItemType.Type.feature)) {
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
        lease.leaseProducts().featureItems().addAll(entity.agreedPets());
        lease.leaseProducts().featureItems().addAll(entity.agreedParking());
        lease.leaseProducts().featureItems().addAll(entity.agreedStorage());
        lease.leaseProducts().featureItems().addAll(entity.agreedOther());

        // save items data:
        for (BillableItem item : lease.leaseProducts().featureItems()) {
            if (!item.extraData().isNull()) {
                Persistence.service().merge(item.extraData());
            }
        }

        // TODO:
        // actually, just feature list should be saved:
//      Persistence.service().merge(lease.serviceAgreement().featureItems());
        // but it's not implemented yet - so save all Lease:
        Persistence.service().merge(lease);

        // wizard state correction:
        ApplicationProgressMgr.invalidateChargesStep(PtAppContext.getCurrentUserApplication());
        DigitalSignatureMgr.resetAll();
        Persistence.service().commit();

        // we do not use return value, so return the same as input one:        
        callback.onSuccess(entity);
        // but, strictly speaking, this call should look like:        
//        callback.onSuccess(retrieveData());
    }

    public ApartmentInfoDTO retrieveData() {
        Lease lease = PtAppContext.getCurrentUserLease();
        if (!Persistence.service().retrieve(lease.unit())) {
            throw new Error("There is no Unit selected!?.");
        }
        if (!Persistence.service().retrieve(lease.unit().floorplan())) {
            throw new Error("There is no unit Floor plan data!?.");
        }
        if (!Persistence.service().retrieve(lease.unit().belongsTo())) {
            throw new Error("There is no unit building data!?.");
        }
        // fill DTO:
        ApartmentInfoDTO aptInfo = EntityFactory.create(ApartmentInfoDTO.class);

        // TODO actually landlord is not always a property manager, but right now it's the most accurate 
        aptInfo.landlordName().setValue(lease.unit().belongsTo().propertyManager().name().getValue());

        aptInfo.floorplan().setValue(lease.unit().floorplan().marketingName().getValue());
        aptInfo.bedrooms().setValue(lease.unit().floorplan().bedrooms().getStringView());
        aptInfo.dens().setValue(lease.unit().floorplan().dens().getStringView());

        if (lease.unit().floorplan().dens().getValue() == 1) {
            aptInfo.bedroomsAndDens().setValue(aptInfo.bedrooms().getValue() + " + " + lease.unit().floorplan().dens().getStringView() + " " + i18n.tr("den"));
        } else if (lease.unit().floorplan().dens().getValue() > 1) {
            aptInfo.bedroomsAndDens().setValue(aptInfo.bedrooms().getValue() + " + " + lease.unit().floorplan().dens().getStringView() + " " + i18n.tr("dens"));
        }

        aptInfo.bathrooms().setValue(lease.unit().floorplan().bathrooms().getStringView());
        if (lease.unit().floorplan().halfBath().getValue() == 1) {
            aptInfo.bathrooms().setValue(
                    aptInfo.bathrooms().getValue() + " + " + lease.unit().floorplan().halfBath().getStringView() + " " + i18n.tr("separate WC"));
        } else if (lease.unit().floorplan().halfBath().getValue() > 1) {
            aptInfo.bathrooms().setValue(
                    aptInfo.bathrooms().getValue() + " + " + lease.unit().floorplan().halfBath().getStringView() + " " + i18n.tr("separate WCs"));
        }

        String suiteNumber = lease.unit().info().number().getValue();

        aptInfo.suiteNumber().setValue(suiteNumber);
        lease.unit().belongsTo().info().address().suiteNumber().setValue(suiteNumber); // this was just for the following copy
        ADDRESS_CONVERTER.copyDBOtoDTO(lease.unit().belongsTo().info().address(), aptInfo.address());

        // find picture:
        Persistence.service().retrieve(lease.unit().belongsTo().media());
        for (Media media : lease.unit().belongsTo().media()) {
            if (Media.Type.file == media.type().getValue()
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
        aptInfo.unitRent().setValue(lease.leaseProducts().serviceItem().item().price().getValue());

        // policy limits:
        MiscPolicy miscPolicy = PolicyManager.obtainEffectivePolicy(lease.unit(), MiscPolicy.class);
        if (miscPolicy == null) {
            throw new Error("There is no MiscPolicy for the Unit!?.");
        }

        aptInfo.maxParkingSpots().setValue(miscPolicy.maxParkingSpots().getValue());
        aptInfo.maxPets().setValue(miscPolicy.maxPets().getValue());

        aptInfo.unit().set(lease.unit().createIdentityStub());
        PolicyManager.sendPolicyToClient(lease.unit(), MiscPolicy.class, miscPolicy);

        return aptInfo;
    }

    private ProductCatalog syncBuildingProductCatalog(Building building) {
        if (building == null || building.isNull()) {
            return null;
        }

        // load detached entities:
        Persistence.service().retrieve(building.productCatalog());
        Persistence.service().retrieve(building.productCatalog().services());

        // load detached service eligibility matrix data:

        // load detached data:
        for (Service item : building.productCatalog().services()) {
            Persistence.service().retrieve(item.version().items());
            Persistence.service().retrieve(item.version().features());
            for (Feature fi : item.version().features()) {
                Persistence.service().retrieve(fi.version().items());
            }
            Persistence.service().retrieve(item.version().concessions());
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

        return building.productCatalog();
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

        syncBuildingProductCatalog(lease.unit().belongsTo());

        entity.includedUtilities().addAll(building.productCatalog().includedUtilities());
        entity.externalUtilities().addAll(building.productCatalog().externalUtilities());

        // fill agreed items:
        for (BillableItem item : lease.leaseProducts().featureItems()) {
            if (item.item().type().type().getValue().equals(ProductItemType.Type.feature)) {
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
        for (Service service : building.productCatalog().services()) {
            if (service.version().type().equals(lease.type())) {
                for (Feature feature : service.version().features()) {
                    for (ProductItem item : feature.version().items()) {
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
        entity.concessions().addAll(lease.leaseProducts().concessions());
    }
}