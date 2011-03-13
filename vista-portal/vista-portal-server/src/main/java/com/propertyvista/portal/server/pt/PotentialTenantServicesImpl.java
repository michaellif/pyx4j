/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.IUserEntity;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.IBoundToApplication;
import com.propertyvista.portal.domain.pt.PaymentInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.CurrentApplication;
import com.propertyvista.portal.rpc.pt.PotentialTenantServices;

import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;

public class PotentialTenantServicesImpl extends EntityServicesImpl implements PotentialTenantServices {

    private final static Logger log = LoggerFactory.getLogger(PotentialTenantServicesImpl.class);

//    private static I18n i18n = I18nFactory.getI18n();

//    public static class UnitExistsImpl implements PotentialTenantServices.UnitExists {
//
//        @Override
//        public Boolean execute(UnitSelectionCriteria request) {
//            UnitSelection unitSelection = EntityFactory.create(UnitSelection.class);
//            unitSelection.selectionCriteria().set(request);
//            loadAvailableUnits(unitSelection);
//            boolean unitExists = (unitSelection.availableUnits().units().size() > 0);
//            log.debug("unitExists {}", unitExists);
//            return unitExists;
//        }
//    }

    public static class GetCurrentApplicationImpl implements PotentialTenantServices.GetCurrentApplication {

        @Override
        public CurrentApplication execute(UnitSelectionCriteria request) {

            // find application by user
            EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), PtUserDataAccess.getCurrentUser()));
            Application application = secureRetrieve(criteria);

            CurrentApplication currentApplication = new CurrentApplication();

            if (application == null) {
                log.info("THIS SHOULD BE FIXED");
//                UnitSelection unitSelection = EntityFactory.create(UnitSelection.class);
//                unitSelection.selectionCriteria().set(request);
//                loadAvailableUnits(unitSelection);
//
//                if (unitSelection.building().isNull()) {
//                    log.info("Could not find building with propertyCode {}", request.propertyCode());
//                    throw new UserRuntimeException("Selected building not found");
//                }
//
//                application = EntityFactory.create(Application.class);
//                application.user().set(PtUserDataAccess.getCurrentUser());
//                secureSave(application);
//
//                ApplicationProgress progress = EntityFactory.create(ApplicationProgress.class);
//                progress.steps().add(createWizardStep(new SiteMap.Apartment(), ApplicationWizardStep.Status.notVisited));
//                progress.steps().add(createWizardStep(new SiteMap.Tenants(), ApplicationWizardStep.Status.notVisited));
//                progress.steps().add(createWizardStep(new SiteMap.Info(), ApplicationWizardStep.Status.notVisited));
//                progress.steps().add(createWizardStep(new SiteMap.Financial(), ApplicationWizardStep.Status.notVisited));
//                progress.steps().add(createWizardStep(new SiteMap.Pets(), ApplicationWizardStep.Status.notVisited));
//                progress.steps().add(createWizardStep(new SiteMap.Charges(), ApplicationWizardStep.Status.notVisited));
//                progress.steps().add(createWizardStep(new SiteMap.Summary(), ApplicationWizardStep.Status.notVisited));
//                progress.steps().add(createWizardStep(new SiteMap.Payment(), ApplicationWizardStep.Status.notVisited));
//                progress.steps().add(createWizardStep(new SiteMap.Completion(), ApplicationWizardStep.Status.notVisited));
//                progress.application().set(application);
//                secureSave(progress);
//
//                currentApplication.progress = progress;
//
//                unitSelection.application().set(application);
//                secureSave(unitSelection);

            } else {
                //Verify if buildingName and floorplanName are the same
                EntityQueryCriteria<UnitSelection> unitSelectionCriteria = EntityQueryCriteria.create(UnitSelection.class);
                unitSelectionCriteria.add(PropertyCriterion.eq(unitSelectionCriteria.proto().application(), application));
                UnitSelection unitSelection = secureRetrieve(unitSelectionCriteria);

                if ((unitSelection != null) && (request != null)) {
                    if ((!unitSelection.selectionCriteria().propertyCode().equals(request.propertyCode()))
                            || (!unitSelection.selectionCriteria().floorplanName().equals(request.floorplanName()))) {
                        //TODO What if they are diferent ?  We need to discard some part of application flow.
                    }
                }

                EntityQueryCriteria<ApplicationProgress> applicationProgressCriteria = EntityQueryCriteria.create(ApplicationProgress.class);
                applicationProgressCriteria.add(PropertyCriterion.eq(applicationProgressCriteria.proto().application(), application));
                currentApplication.progress = secureRetrieve(applicationProgressCriteria);
            }
            PtUserDataAccess.setCurrentUserApplication(application);
            currentApplication.application = application;
            log.info("Start application {}", application);
            log.info("  progress {}", currentApplication.progress);
            return currentApplication;
        }

//        private ApplicationWizardStep createWizardStep(AppPlace place, ApplicationWizardStep.Status status) {
//            ApplicationWizardStep ws = EntityFactory.create(ApplicationWizardStep.class);
//            ws.placeToken().setValue(AppPlaceInfo.getPlaceId(place.getClass()));
//            ws.status().setValue(status);
//            return ws;
//        }
    }

    public static class RetrieveByPKImpl extends EntityServicesImpl.RetrieveByPKImpl implements PotentialTenantServices.RetrieveByPK {

        @SuppressWarnings("unchecked")
        @Override
        public IEntity execute(EntityCriteriaByPK<?> request) {
            log.info("Retrieving data");
            IEntity ret;
            if (request.getPrimaryKey() == 0) {
                // Find first Entity of that type in Application 
                EntityQueryCriteria<IBoundToApplication> criteria = EntityQueryCriteria.create((Class<IBoundToApplication>) request.getEntityClass());
                criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
                ret = secureRetrieve(criteria);
                if (ret == null) {
                    //Nothing found -> create
                    //                    if (request.proto() instanceof Charges) {
                    //                        ret = createCharges();
//                    if (request.proto() instanceof Summary) {
//                        ret = EntityFactory.create(Summary.class);
//                    } else if (request.proto() instanceof PotentialTenantFinancial) {
//                        ret = createFinancial();
//                    } else if (request.proto() instanceof PaymentInfo) {
//                        ret = EntityFactory.create(PaymentInfo.class);
//                        ((PaymentInfo) ret).type().setValue(PaymentType.Echeck);
//                        ((PaymentInfo) ret).preauthorised().setValue(Boolean.TRUE);
//                    }
                }
            } else {
                ret = super.execute(request);
            }

//            if (ret instanceof UnitSelection) {
//                loadAvailableUnits((UnitSelection) ret);
            //            } else if (ret instanceof Charges) {
            //                Charges charges = (Charges) ret;
            //                ChargesServerCalculation.updateChargesFromApplication(charges);
            if (ret instanceof Summary) {
//                retrieveSummary((Summary) ret);
            } else if (ret instanceof PaymentInfo) {
//                retrievePaymentInfo((PaymentInfo) ret);
            }

            //            else if ((ret instanceof Pets) || (request.proto() instanceof Pets)) {
            //                if (ret == null) {
            //                    ret = EntityFactory.create(Pets.class);
            //                }
            //                // TODO get it from building
            //                PetChargeRule petCharge = EntityFactory.create(PetChargeRule.class);
            //                petCharge.chargeType().setValue(ChargeType.monthly);
            //                petCharge.value().setValue(20);
            //                ((Pets) ret).petChargeRule().set(petCharge);
            //                ((Pets) ret).petWeightMaximum().setValue(25);
            //                ((Pets) ret).petsMaximum().setValue(3);
            //
            //            }

            return ret;
        }

//        private Charges createCharges() {
//            Charges charges = EntityFactory.create(Charges.class);
//            charges.application().set(PtUserDataAccess.getCurrentUserApplication());
//            return charges;
//        }

//        private <T extends IBoundToApplication> void retrieveApplicationEntity(T entity) {
//            @SuppressWarnings("unchecked")
//            EntityQueryCriteria<T> criteria = (EntityQueryCriteria<T>) EntityQueryCriteria.create(entity.getValueClass());
//            criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
//            entity.set(secureRetrieve(criteria));
//        }

    }

//    public static class RetrieveUnitSelectionImpl implements PotentialTenantServices.RetrieveUnitSelection {
//
//        @Override
//        public UnitSelection execute(UnitSelectionCriteria request) {
//            EntityQueryCriteria<UnitSelection> criteria = EntityQueryCriteria.create(UnitSelection.class);
//            criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
//            UnitSelection unitSelection = secureRetrieve(criteria);
//            unitSelection.selectionCriteria().set(request);
//
//            loadAvailableUnits(unitSelection);
//            return unitSelection;
//        }
//
//    }

    public static class SaveImpl extends EntityServicesImpl.MergeSaveImpl implements PotentialTenantServices.Save {

        @Override
        public IEntity execute(IEntity request) {
            final User currentUser = PtUserDataAccess.getCurrentUser();
            final Application application = PtUserDataAccess.getCurrentUserApplication();

            if ((request instanceof IUserEntity) || (request instanceof IBoundToApplication)) {
                // update Owned Members
                EntityGraph.applyRecursively(request, new EntityGraph.ApplyMethod() {

                    @Override
                    public void apply(IEntity entity) {
                        if (entity instanceof IUserEntity) {
                            ((IUserEntity) entity).user().set(currentUser);
                        } else if (entity instanceof IBoundToApplication) {
                            ((IBoundToApplication) entity).application().set(application);
                        }
                    }
                });
            } else {
                throw new UnRecoverableRuntimeException("Invalid object");
            }

            IEntity ret = super.execute(request);

//            if (ret instanceof UnitSelection) {
//                loadAvailableUnits((UnitSelection) ret);
//            }

            if (ret instanceof PotentialTenantList) {

            } else if (ret instanceof PaymentInfo) {

            }

            //TODO we missed values for Pets see the same code in RetrieveByPKImpl
            //            (ret).petChargeRule().set(petCharge);
            //            (ret).petWeightMaximum().setValue(25);
            //            (ret).petsMaximum().setValue(3);

            return ret;
        }

    }
}
