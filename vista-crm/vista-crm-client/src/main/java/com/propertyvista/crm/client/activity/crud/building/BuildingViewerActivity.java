/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.building;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.client.upload.UploadReceiver;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.site.client.ui.prime.lister.ILister.Presenter;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.UploadDialogBase;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.ListerControllerFactory;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.building.BuildingViewerView;
import com.propertyvista.crm.client.visor.communityevent.CommunityEventVisorController;
import com.propertyvista.crm.client.visor.dashboard.DashboardVisorController;
import com.propertyvista.crm.client.visor.dashboard.IDashboardVisorController;
import com.propertyvista.crm.client.visor.maintenance.MaintenanceRequestVisorController;
import com.propertyvista.crm.rpc.dto.DeferredProcessingStarted;
import com.propertyvista.crm.rpc.dto.ImportBuildingDataParametersDTO;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.services.billing.BillingCycleCrudService;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.crm.rpc.services.building.FloorplanCrudService;
import com.propertyvista.crm.rpc.services.building.LockerAreaCrudService;
import com.propertyvista.crm.rpc.services.building.ParkingCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.ConcessionCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.FeatureCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.ServiceCrudService;
import com.propertyvista.crm.rpc.services.building.mech.BoilerCrudService;
import com.propertyvista.crm.rpc.services.building.mech.ElevatorCrudService;
import com.propertyvista.crm.rpc.services.building.mech.RoofCrudService;
import com.propertyvista.crm.rpc.services.importer.ExportBuildingDataDownloadService;
import com.propertyvista.crm.rpc.services.importer.ImportBuildingDataService;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class BuildingViewerActivity extends CrmViewerActivity<BuildingDTO> implements BuildingViewerView.BuildingViewerPresenter {

    private static final I18n i18n = I18n.get(BuildingViewerActivity.class);

    private final Presenter<FloorplanDTO> floorplanLister;

    private final Presenter<AptUnitDTO> unitLister;

    private final Presenter<ElevatorDTO> elevatorLister;

    private final Presenter<BoilerDTO> boilerLister;

    private final Presenter<RoofDTO> roofLister;

    private final Presenter<ParkingDTO> parkingLister;

    private final Presenter<LockerAreaDTO> lockerAreaLister;

    private final Presenter<Service> serviceLister;

    private final Presenter<Feature> featureLister;

    private final Presenter<Concession> concessionLister;

    private final Presenter<BillingCycleDTO> billingCycleLister;

    private MaintenanceRequestVisorController maintenanceRequestVisorController;

    private CommunityEventVisorController communityEventtVisorController;

    private Key currentBuildingId;

    @SuppressWarnings("unchecked")
    public BuildingViewerActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(BuildingViewerView.class), (AbstractCrudService<BuildingDTO>) GWT.create(BuildingCrudService.class));

        floorplanLister = ListerControllerFactory.create(FloorplanDTO.class,
                ((BuildingViewerView) getView()).getFloorplanListerView(), (AbstractCrudService<FloorplanDTO>) GWT.create(FloorplanCrudService.class));

        unitLister = ListerControllerFactory.create(AptUnitDTO.class,
                ((BuildingViewerView) getView()).getUnitListerView(), (AbstractCrudService<AptUnitDTO>) GWT.create(UnitCrudService.class));

        elevatorLister = ListerControllerFactory.create(ElevatorDTO.class,
                ((BuildingViewerView) getView()).getElevatorListerView(), (AbstractCrudService<ElevatorDTO>) GWT.create(ElevatorCrudService.class));
        boilerLister = ListerControllerFactory.create(BoilerDTO.class,
                ((BuildingViewerView) getView()).getBoilerListerView(), (AbstractCrudService<BoilerDTO>) GWT.create(BoilerCrudService.class));
        roofLister = ListerControllerFactory.create(RoofDTO.class,
                ((BuildingViewerView) getView()).getRoofListerView(), (AbstractCrudService<RoofDTO>) GWT.create(RoofCrudService.class));

        parkingLister = ListerControllerFactory.create(ParkingDTO.class,
                ((BuildingViewerView) getView()).getParkingListerView(), (AbstractCrudService<ParkingDTO>) GWT.create(ParkingCrudService.class));
        lockerAreaLister = ListerControllerFactory.create(LockerAreaDTO.class,
                ((BuildingViewerView) getView()).getLockerAreaListerView(), (AbstractCrudService<LockerAreaDTO>) GWT.create(LockerAreaCrudService.class));

        serviceLister = ListerControllerFactory.create(Service.class,
                ((BuildingViewerView) getView()).getServiceListerView(), (AbstractCrudService<Service>) GWT.create(ServiceCrudService.class));
        featureLister = ListerControllerFactory.create(Feature.class,
                ((BuildingViewerView) getView()).getFeatureListerView(), (AbstractCrudService<Feature>) GWT.create(FeatureCrudService.class));
        concessionLister = ListerControllerFactory.create(Concession.class,
                ((BuildingViewerView) getView()).getConcessionListerView(), (AbstractCrudService<Concession>) GWT.create(ConcessionCrudService.class));

        billingCycleLister = ListerControllerFactory.create(BillingCycleDTO.class,
                ((BuildingViewerView) getView()).getBillingCycleListerView(), (AbstractCrudService<BillingCycleDTO>) GWT.create(BillingCycleCrudService.class));
    }

    @Override
    public MaintenanceRequestVisorController getMaintenanceRequestVisorController() {
        if (maintenanceRequestVisorController == null) {
            maintenanceRequestVisorController = new MaintenanceRequestVisorController(getView(), currentBuildingId);
        }
        return maintenanceRequestVisorController;
    }

    @Override
    public CommunityEventVisorController getCommunityEventVisorController() {
        if (communityEventtVisorController == null) {

            communityEventtVisorController = new CommunityEventVisorController(getView(),
                    EntityFactory.createIdentityStub(BuildingDTO.class, currentBuildingId));
        }
        return communityEventtVisorController;
    }

    @Override
    public boolean canEdit() {
        return SecurityController.check(DataModelPermission.permissionUpdate(BuildingDTO.class));
    }

    @Override
    public void onPopulateSuccess(BuildingDTO result) {
        super.onPopulateSuccess(result);

        currentBuildingId = result.id().getValue();

        // -------------------------------------------------------

        floorplanLister.setParent(result.getPrimaryKey());
        floorplanLister.populate();

        unitLister.setParent(result.getPrimaryKey());
        unitLister.populate();

        // -------------------------------------------------------

        elevatorLister.setParent(result.getPrimaryKey());
        elevatorLister.populate();

        boilerLister.setParent(result.getPrimaryKey());
        boilerLister.populate();

        roofLister.setParent(result.getPrimaryKey());
        roofLister.populate();

        // -------------------------------------------------------

        parkingLister.setParent(result.getPrimaryKey());
        parkingLister.populate();

        lockerAreaLister.setParent(result.getPrimaryKey());
        lockerAreaLister.populate();

        // -----------------------------------------------------------------------

        serviceLister.setParent(result.productCatalog().getPrimaryKey());
        serviceLister.populate();

        featureLister.setParent(result.productCatalog().getPrimaryKey());
        featureLister.populate();

        concessionLister.setParent(result.productCatalog().getPrimaryKey());
        concessionLister.populate();

        billingCycleLister.clearPreDefinedFilters();
        billingCycleLister.addPreDefinedFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(BillingCycleDTO.class).building(), result));
        billingCycleLister.populate();
    }

    @Override
    public IDashboardVisorController getDashboardController(DashboardMetadata dashboardMetadata, List<Building> buildings) {
        return new DashboardVisorController(getView(), dashboardMetadata, buildings);
    }

    @Override
    public void retrieveMerchantAccountStatus(AsyncCallback<MerchantAccount> callback, MerchantAccount merchantAccountStub) {
        ((BuildingCrudService) getService()).retrieveMerchantAccountStatus(callback, merchantAccountStub);
    }

    @Override
    public void updateFromYardi() {
        ((BuildingCrudService) getService()).updateFromYardi(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorrelationId) {
                // --copy of the code -- start
                DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("Building Update"), i18n.tr("Updating Building..."), false) {
                    @Override
                    public void onDeferredSuccess(final DeferredProcessProgressResponse result) {
                        super.onDeferredSuccess(result);
                        populate();
                    }
                };
                d.show();
                d.startProgress(deferredCorrelationId);
                // --copy of the code -- ends
            }
        }, EntityFactory.createIdentityStub(Building.class, getEntityId()));
    }

    @Override
    public void importBuildingData() {
        UploadDialogBase<ImportBuildingDataParametersDTO> dialog = new UploadDialogBase<ImportBuildingDataParametersDTO>(i18n.tr("Import Building Data"),
                GWT.<UploadService<ImportBuildingDataParametersDTO, AbstractIFileBlob>> create(ImportBuildingDataService.class)) {

            @Override
            protected ImportBuildingDataParametersDTO getUploadData() {
                ImportBuildingDataParametersDTO params = EntityFactory.create(ImportBuildingDataParametersDTO.class);
                params.buildingId().set(EntityFactory.createIdentityStub(Building.class, getEntityId()));
                return params;
            }

        };

        dialog.setUploadReciver(new UploadReceiver() {
            @Override
            public void onUploadComplete(IFile<?> uploadResponse) {
                DeferredProcessingStarted blob = uploadResponse.blob().cast();
                String deferredCorrelationId = blob.deferredCorrelationId().getValue();

                // This is the same as in updateFromYardi
                // --copy of the code -- start
                DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("Import Building Data"), i18n.tr("Processing Import..."), false) {
                    @Override
                    public void onDeferredSuccess(final DeferredProcessProgressResponse result) {
                        super.onDeferredSuccess(result);
                        populate();
                    }
                };
                d.setFailureMessage(i18n.tr("Import Failed. THE NEW BUILDING DATA HAS NOT BEEN LOADED. Please correct the issues below and try again."));
                d.show();
                d.startProgress(deferredCorrelationId);
                // --copy of the code -- ends
            }
        });

        dialog.show();
    }

    @Override
    public void exportBuildingData() {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().id(), getEntityId());
        ReportDialog d = new ReportDialog("Data Export", "Creating Building Data Export...");
        d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
        d.start(GWT.<ExportBuildingDataDownloadService> create(ExportBuildingDataDownloadService.class), criteria);
    }

}
