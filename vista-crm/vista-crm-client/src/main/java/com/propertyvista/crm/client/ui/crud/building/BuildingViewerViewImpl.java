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
package com.propertyvista.crm.client.ui.crud.building;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ILister;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.Button.SecureMenuItem;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleLister;
import com.propertyvista.crm.client.ui.crud.building.catalog.ConcessionLister;
import com.propertyvista.crm.client.ui.crud.building.catalog.FeatureLister;
import com.propertyvista.crm.client.ui.crud.building.catalog.ServiceLister;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.services.building.ac.CommunityEvents;
import com.propertyvista.crm.rpc.services.building.ac.ImportExport;
import com.propertyvista.crm.rpc.services.lease.ac.UpdateFromYardi;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class BuildingViewerViewImpl extends CrmViewerViewImplBase<BuildingDTO> implements BuildingViewerView {

    private static final I18n i18n = I18n.get(BuildingViewerViewImpl.class);

    private final BuildingFloorplanLister floorplanLister;

    private final BuildingUnitLister unitLister;

    private final BuildingElevatorLister elevatorLister;

    private final BuildingBoilerLister boilerLister;

    private final BuildingRoofLister roofLister;

    private final ParkingLister parkingLister;

    private final LockerAreaLister lockerAreaLister;

    private final ILister<Service> serviceLister;

    private final ILister<Feature> featureLister;

    private final ILister<Concession> concessionLister;

    private final ILister<BillingCycleDTO> billingCycleLister;

    private final ButtonMenuBar dashboardsMenu;

    public BuildingViewerViewImpl() {

        floorplanLister = new BuildingFloorplanLister();

        unitLister = new BuildingUnitLister();

        elevatorLister = new BuildingElevatorLister();
        boilerLister = new BuildingBoilerLister();
        roofLister = new BuildingRoofLister();

        parkingLister = new ParkingLister();
        lockerAreaLister = new LockerAreaLister();

        serviceLister = new ServiceLister(this);
        featureLister = new FeatureLister(this);
        concessionLister = new ConcessionLister();

        billingCycleLister = new BillingCycleLister();

        // set main form here:
        setForm(new BuildingForm(this));

        Button dashboardButton = new Button(i18n.tr("Dashboard"));
        dashboardsMenu = new ButtonMenuBar();
        dashboardButton.setMenu(dashboardsMenu);
        addHeaderToolbarItem(dashboardButton);

        addAction(new SecureMenuItem(i18n.tr("Maintenance Requests"), new Command() {
            @Override
            public void execute() {
                ((BuildingViewerPresenter) getPresenter()).getMaintenanceRequestVisorController().show();
            }
        }, DataModelPermission.permissionRead(MaintenanceRequestDTO.class)));

        addAction(new SecureMenuItem(i18n.tr("Community Events"), new Command() {
            @Override
            public void execute() {
                ((BuildingViewerPresenter) getPresenter()).getCommunityEventVisorController().show();
            }
        }, new ActionPermission(CommunityEvents.class)));

        if (VistaFeatures.instance().yardiIntegration()) {
            addAction(new SecureMenuItem(i18n.tr("Update From Yardi"), new Command() {
                @Override
                public void execute() {
                    ((BuildingViewerPresenter) getPresenter()).updateFromYardi();
                }
            }, new ActionPermission(UpdateFromYardi.class)));
        }

        addAction(new SecureMenuItem(i18n.tr("Import Building Data"), new Command() {
            @Override
            public void execute() {
                ((BuildingViewerPresenter) getPresenter()).importBuildingData();
            }
        }, new ActionPermission(ImportExport.class)));

        addAction(new SecureMenuItem(i18n.tr("Export Building Data"), new Command() {
            @Override
            public void execute() {
                ((BuildingViewerPresenter) getPresenter()).exportBuildingData();
            }
        }, new ActionPermission(ImportExport.class)));

        addAction(new SecureMenuItem(i18n.tr("Select Merchant Account"), new Command() {
            @Override
            public void execute() {
                selectMerchantAccount();
            }
        }, DataModelPermission.permissionUpdate(BuildingMerchantAccount.class)));
    }

    @Override
    public BuildingFloorplanLister getFloorplanListerView() {
        return floorplanLister;
    }

    @Override
    public BuildingUnitLister getUnitListerView() {
        return unitLister;
    }

    @Override
    public BuildingElevatorLister getElevatorListerView() {
        return elevatorLister;
    }

    @Override
    public BuildingBoilerLister getBoilerListerView() {
        return boilerLister;
    }

    @Override
    public BuildingRoofLister getRoofListerView() {
        return roofLister;
    }

    @Override
    public ParkingLister getParkingListerView() {
        return parkingLister;
    }

    @Override
    public LockerAreaLister getLockerAreaListerView() {
        return lockerAreaLister;
    }

    @Override
    public ILister<Service> getServiceListerView() {
        return serviceLister;
    }

    @Override
    public ILister<Feature> getFeatureListerView() {
        return featureLister;
    }

    @Override
    public ILister<Concession> getConcessionListerView() {
        return concessionLister;
    }

    @Override
    public ILister<BillingCycleDTO> getBillingCycleListerView() {
        return billingCycleLister;
    }

    // Internals:

    @Override
    public void selectMerchantAccount() {
        new MerchantAccountSelectionBox().show();
    }

    public interface RunBillData extends IEntity {

        @NotNull
        IPrimitive<BillingPeriod> billingPeriod();

        @NotNull
        IPrimitive<LogicalDate> billingPeriodStartDate();
    }

    @Override
    public void populate(BuildingDTO value) {
        super.populate(value);

        floorplanLister.getDataSource().setParentEntityId(value.getPrimaryKey());
        floorplanLister.populate();

        unitLister.getDataSource().setParentEntityId(value.getPrimaryKey());
        unitLister.populate();

        elevatorLister.getDataSource().setParentEntityId(value.getPrimaryKey());
        elevatorLister.populate();

        boilerLister.getDataSource().setParentEntityId(value.getPrimaryKey());
        boilerLister.populate();

        roofLister.getDataSource().setParentEntityId(value.getPrimaryKey());
        roofLister.populate();

        parkingLister.getDataSource().setParentEntityId(value.getPrimaryKey());
        parkingLister.populate();

        lockerAreaLister.getDataSource().setParentEntityId(value.getPrimaryKey());
        lockerAreaLister.populate();

        populateDashboardsMenu(value.dashboards().iterator());
    }

    private void populateDashboardsMenu(Iterator<DashboardMetadata> dashboardsIterator) {
        dashboardsMenu.clearItems();
        while (dashboardsIterator.hasNext()) {
            final DashboardMetadata dashboard = dashboardsIterator.next();
            dashboardsMenu.addItem(dashboard.name().getValue(), new Command() {

                @Override
                public void execute() {
                    List<Building> buildingsFilter = new ArrayList<Building>();
                    buildingsFilter.add(getForm().getValue());
                    ((BuildingViewerView.BuildingViewerPresenter) getPresenter()).getDashboardController(dashboard, buildingsFilter).show();

                }

            });
        }
    }

    @Transient
    public interface MerchantAccountSelection extends IEntity {

        MerchantAccount merchantAccount();
    }

    private class MerchantAccountSelectionBox extends OkCancelDialog {

        private final CForm<MerchantAccountSelection> content = new CForm<MerchantAccountSelection>(MerchantAccountSelection.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Dual, proto().merchantAccount()).decorate();

                // tweak:
                get(proto().merchantAccount()).addValueChangeHandler(new ValueChangeHandler<MerchantAccount>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<MerchantAccount> event) {
                        fillMerchantAccountStatus(event.getValue());
                    }
                });

                return formPanel;
            }

            private void fillMerchantAccountStatus(MerchantAccount value) {
                if (value != null && !value.isNull()) {
                    ((BuildingPresenterCommon) getPresenter()).retrieveMerchantAccountStatus(new DefaultAsyncCallback<MerchantAccount>() {
                        @Override
                        public void onSuccess(MerchantAccount result) {
                            get(proto().merchantAccount()).setNote(result.status().getStringView() + ", " + result.paymentsStatus().getStringView());
                        }
                    }, EntityFactory.createIdentityStub(MerchantAccount.class, value.getPrimaryKey()));
                } else {
                    get(proto().merchantAccount()).setNote(null);
                }
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);
                fillMerchantAccountStatus(getValue().merchantAccount());
            }
        };

        public MerchantAccountSelectionBox() {
            super(i18n.tr("Select Merchant Account"));
            setBody(createBody());
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            MerchantAccountSelection value = EntityFactory.create(MerchantAccountSelection.class);
            value.<MerchantAccount> set(value.merchantAccount(), getForm().getValue().merchantAccount().<MerchantAccount> duplicate());

            content.init();
            content.populate(value);
            return content.asWidget();
        }

        @Override
        public boolean onClickOk() {
            final MerchantAccount merchantAccount = content.getValue().merchantAccount();
            ((BuildingViewerPresenter) getPresenter()).setMerchantAccount(new DefaultAsyncCallback<VoidSerializable>() {
                @Override
                public void onSuccess(VoidSerializable result) {
                    getForm().get(getForm().proto().merchantAccount()).setValue(merchantAccount);
                }
            }, merchantAccount.<MerchantAccount> createIdentityStub());

            return true;
        }
    }
}
