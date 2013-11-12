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
package com.propertyvista.portal.server.portal.services;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.services.UnitStepService;
import com.propertyvista.portal.rpc.portal.web.dto.application.UnitInfoDTO;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.services.util.ApplicationProgressMgr;
import com.propertyvista.portal.server.ptapp.services.util.DigitalSignatureMgr;
import com.propertyvista.server.common.util.AddressConverter;

public class UnitStepServiceImpl implements UnitStepService {

    private final static I18n i18n = I18n.get(UnitStepServiceImpl.class);

    private final static Logger log = LoggerFactory.getLogger(UnitStepServiceImpl.class);

    private final static AddressConverter.StructuredToSimpleAddressConverter ADDRESS_CONVERTER = new AddressConverter.StructuredToSimpleAddressConverter();

    @Override
    public void retrieve(AsyncCallback<UnitInfoDTO> callback) {
        callback.onSuccess(retrieveData());
    }

    @Override
    public void submit(AsyncCallback<UnitInfoDTO> callback, UnitInfoDTO entity) {
        // update agreed items:
        Lease lease = PtAppContext.retrieveCurrentUserLease();
        for (Iterator<BillableItem> iter = lease.currentTerm().version().leaseProducts().featureItems().iterator(); iter.hasNext();) {
            BillableItem item = iter.next();
            if (ARCode.Type.features().contains(item.item().code().type().getValue())) {
                switch (item.item().code().type().getValue()) {
                case Utility:
                    break;
                case Pet:
                case Parking:
                case Locker:
                default:
                    iter.remove(); // remove all non-utility items
                }
            }
        }

        // add user-selected ones:
        lease.currentTerm().version().leaseProducts().featureItems().addAll(entity.agreedPets());
        lease.currentTerm().version().leaseProducts().featureItems().addAll(entity.agreedParking());
        lease.currentTerm().version().leaseProducts().featureItems().addAll(entity.agreedStorage());
        lease.currentTerm().version().leaseProducts().featureItems().addAll(entity.agreedOther());

        // save items data:
        for (BillableItem item : lease.currentTerm().version().leaseProducts().featureItems()) {
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
        ApplicationProgressMgr.invalidateChargesStep(PtAppContext.retrieveCurrentUserApplication());
        DigitalSignatureMgr.resetAll();
        Persistence.service().commit();

        // we do not use return value, so return the same as input one:        
        callback.onSuccess(entity);
        // but, strictly speaking, this call should look like:        
//        callback.onSuccess(retrieveData());
    }

    public UnitInfoDTO retrieveData() {
        Lease lease = PtAppContext.retrieveCurrentUserLease();
        if (!Persistence.service().retrieve(lease.unit())) {
            throw new Error("There is no Unit selected!?.");
        }
        if (!Persistence.service().retrieve(lease.unit().floorplan())) {
            throw new Error("There is no unit Floor plan data!?.");
        }
        if (!Persistence.service().retrieve(lease.unit().building())) {
            throw new Error("There is no unit building data!?.");
        }
        // fill DTO:
        UnitInfoDTO aptInfo = EntityFactory.create(UnitInfoDTO.class);

        // TODO actually landlord is not always a property manager, but right now it's the most accurate 
        aptInfo.landlordName().setValue(lease.unit().building().propertyManager().name().getValue());

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
        lease.unit().building().info().address().suiteNumber().setValue(suiteNumber); // this was just for the following copy
        ADDRESS_CONVERTER.copyBOtoTO(lease.unit().building().info().address(), aptInfo.address());

        // find picture:
        Persistence.service().retrieve(lease.unit().building().media());
        for (MediaFile media : lease.unit().building().media()) {
            if ((PublicVisibilityType.tenant == media.visibility().getValue() || PublicVisibilityType.global == media.visibility().getValue())) {
                aptInfo.picture().set(media);
                break;
            }
        }

        // serviceCatalog processing:
        fillServiceItems(aptInfo, lease.unit().building(), lease);

        // Lease data:
        aptInfo.leaseFrom().setValue(lease.currentTerm().termFrom().getValue());
        aptInfo.leaseTo().setValue(lease.currentTerm().termTo().getValue());
        aptInfo.unitRent().setValue(lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue());
        aptInfo.unit().set(lease.unit());

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

    private void fillServiceItems(UnitInfoDTO entity, Building building, Lease lease) {

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

        syncBuildingProductCatalog(lease.unit().building());

        // fill agreed items:
        for (BillableItem item : lease.currentTerm().version().leaseProducts().featureItems()) {
            switch (item.item().code().type().getValue()) {
            case Utility:
                entity.agreedUtilities().add(item);
                break;
            case Pet:
                entity.agreedPets().add(item);
                break;
            case Parking:
                entity.agreedParking().add(item);
                break;
            case Locker:
                entity.agreedStorage().add(item);
                break;
            default:
                entity.agreedOther().add(item);
            }
        }

        // fill available items:
        for (Service service : building.productCatalog().services()) {
            if (service.type().equals(lease.type())) {
                for (Feature feature : service.version().features()) {
                    for (ProductItem item : feature.version().items()) {
                        switch (item.code().type().getValue()) {
                        case AddOn:
                        case Utility:
                            entity.availableUtilities().add(item);
                            break;
                        case Pet:
                            entity.availablePets().add(item);
                            break;
                        case Parking:
                            entity.availableParking().add(item);
                            break;
                        case Locker:
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
        entity.concessions().addAll(lease.currentTerm().version().leaseProducts().concessions());
    }

}