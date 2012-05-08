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
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.building.catalog.ConcessionLister;
import com.propertyvista.crm.client.ui.crud.building.catalog.FeatureLister;
import com.propertyvista.crm.client.ui.crud.building.catalog.ServiceLister;
import com.propertyvista.crm.client.ui.crud.building.dashboard.BuildingDashboardView;
import com.propertyvista.crm.client.ui.crud.building.dashboard.BuildingDashboardViewImpl;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerAreaLister;
import com.propertyvista.crm.client.ui.crud.building.mech.BoilerLister;
import com.propertyvista.crm.client.ui.crud.building.mech.ElevatorLister;
import com.propertyvista.crm.client.ui.crud.building.mech.RoofLister;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingLister;
import com.propertyvista.crm.client.ui.crud.floorplan.FloorplanLister;
import com.propertyvista.crm.client.ui.crud.unit.UnitLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;
import com.propertyvista.misc.VistaTODO;

public class BuildingViewerViewImpl extends CrmViewerViewImplBase<BuildingDTO> implements BuildingViewerView {

    private static final I18n i18n = I18n.get(BuildingViewerViewImpl.class);

    private final IListerView<FloorplanDTO> floorplanLister;

    private final IListerView<AptUnitDTO> unitLister;

    private final IListerView<ElevatorDTO> elevatorLister;

    private final IListerView<BoilerDTO> boilerLister;

    private final IListerView<RoofDTO> roofLister;

    private final IListerView<ParkingDTO> parkingLister;

    private final IListerView<LockerAreaDTO> lockerAreaLister;

    private final IListerView<Service> serviceLister;

    private final IListerView<Feature> featureLister;

    private final IListerView<Concession> concessionLister;

    private final IListerView<BillingRun> billingRunLister;

    private final BuildingDashboardView dashboardView = new BuildingDashboardViewImpl();

    private final Button runBillAction;

    public BuildingViewerViewImpl() {
        super(CrmSiteMap.Properties.Building.class);

        floorplanLister = new ListerInternalViewImplBase<FloorplanDTO>(new FloorplanLister());

        unitLister = new ListerInternalViewImplBase<AptUnitDTO>(new UnitLister(true));

        elevatorLister = new ListerInternalViewImplBase<ElevatorDTO>(new ElevatorLister());
        boilerLister = new ListerInternalViewImplBase<BoilerDTO>(new BoilerLister());
        roofLister = new ListerInternalViewImplBase<RoofDTO>(new RoofLister());

        parkingLister = new ListerInternalViewImplBase<ParkingDTO>(new ParkingLister());
        lockerAreaLister = new ListerInternalViewImplBase<LockerAreaDTO>(new LockerAreaLister());

        serviceLister = new ListerInternalViewImplBase<Service>(new ServiceLister());
        featureLister = new ListerInternalViewImplBase<Feature>(new FeatureLister());
        concessionLister = new ListerInternalViewImplBase<Concession>(new ConcessionLister());

        billingRunLister = new ListerInternalViewImplBase<BillingRun>(new BillingRunLister());

        // set main form here:
        setForm(new BuildingEditorForm(true));

        runBillAction = new Button(i18n.tr("Run Bill"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new RunBillDataBox() {
                    @Override
                    public boolean onClickOk() {
                        // TODO: pass data from Box into the service call: 
                        ((BuildingViewerView.Presenter) presenter).runBill(/* getValue() */);
                        return true;
                    }
                }.show();

            }
        });
        if (!VistaTODO.removedForProduction) {
            addHeaderToolbarTwoItem(runBillAction.asWidget());
        }
    }

    @Override
    public BuildingDashboardView getDashboardView() {
        return dashboardView;
    }

    @Override
    public IListerView<FloorplanDTO> getFloorplanListerView() {
        return floorplanLister;
    }

    @Override
    public IListerView<AptUnitDTO> getUnitListerView() {
        return unitLister;
    }

    @Override
    public IListerView<ElevatorDTO> getElevatorListerView() {
        return elevatorLister;
    }

    @Override
    public IListerView<BoilerDTO> getBoilerListerView() {
        return boilerLister;
    }

    @Override
    public IListerView<RoofDTO> getRoofListerView() {
        return roofLister;
    }

    @Override
    public IListerView<ParkingDTO> getParkingListerView() {
        return parkingLister;
    }

    @Override
    public IListerView<LockerAreaDTO> getLockerAreaListerView() {
        return lockerAreaLister;
    }

    @Override
    public IListerView<Service> getServiceListerView() {
        return serviceLister;
    }

    @Override
    public IListerView<Feature> getFeatureListerView() {
        return featureLister;
    }

    @Override
    public IListerView<Concession> getConcessionListerView() {
        return concessionLister;
    }

    @Override
    public IListerView<BillingRun> getBillingRunListerView() {
        return billingRunLister;
    }

    // Internals:

    public interface RunBillData extends IEntity {

        @NotNull
        IPrimitive<PaymentFrequency> paymentFrequency();

        @NotNull
        IPrimitive<LogicalDate> billingPeriodStartDate();
    }

    private abstract class RunBillDataBox extends OkCancelDialog {

        private CEntityDecoratableForm<RunBillData> content;

        public RunBillDataBox() {
            super(i18n.tr("Please select"));

            RunBillData data = EntityFactory.create(RunBillData.class);
            data.paymentFrequency().setValue(PaymentFrequency.Monthly);
            data.billingPeriodStartDate().setValue(new LogicalDate());

            setBody(createBody(data));
        }

        protected Widget createBody(RunBillData data) {
            getOkButton().setEnabled(true);

            content = new CEntityDecoratableForm<RunBillData>(RunBillData.class) {

                private final CComboBox<PaymentFrequency> paymentFrequency = new CComboBox<PaymentFrequency>();
                {
                    Collection<PaymentFrequency> opt = new ArrayList<PaymentFrequency>(EnumSet.allOf(PaymentFrequency.class));
                    opt.removeAll(Arrays.asList(PaymentFrequency.SemiAnnyally, PaymentFrequency.Annually));
                    paymentFrequency.setOptions(opt);
                    paymentFrequency.addValueChangeHandler(new ValueChangeHandler<PaymentFrequency>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<PaymentFrequency> event) {
                            // TODO Auto-generated method stub
                        }
                    });
                }

                @Override
                public IsWidget createContent() {
                    FormFlexPanel main = new FormFlexPanel();

                    main.setWidget(1, 0, new DecoratorBuilder(inject(proto().paymentFrequency(), paymentFrequency), 10).build());
                    main.setWidget(2, 0, new DecoratorBuilder(inject(proto().billingPeriodStartDate()), 10).build());

                    return main;
                }

                @Override
                public void addValidations() {
                    super.addValidations();

                }
            };

            content.initContent();
            content.populate(data);
            return content.asWidget();
        }

        public RunBillData getValue() {
            return content.getValue();
        }
    }
}
