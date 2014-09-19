/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import java.math.BigDecimal;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeEvent;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.LeaseChargesDataDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;

public class OptionsStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(OptionsStep.class);

    private FormPanel depositPanel;

    private FormPanel chargedPanel;

    private FormPanel petsPanel;

    private FormPanel parkingPanel;

    private FormPanel storagePanel;

    private FormPanel otherPanel;

    private FeatureFolder2 lockerFolder;

    private FeatureFolder2 petFolder;

    private FeatureFolder2 parkingFolder;

    public OptionsStep() {
        super(OnlineApplicationWizardStepMeta.Options);
    }

    @Override
    public IsWidget createStepContent() {
        FormPanel formPanel = new FormPanel(getWizard());
        formPanel.append(Location.Left, proto().unitOptionsSelection(), new StepDataForm());
        return formPanel;
    }

    public void setStepValue(UnitOptionsSelectionDTO value) {
        get(proto().unitOptionsSelection()).setValue(value);
    }

    public UnitOptionsSelectionDTO getStepValue() {
        return getWizard().getValue().unitOptionsSelection();
    }

    @Override
    public void onStepLeaving() {
        super.onStepLeaving();

        getWizard().getPresenter().updateLeaseChargesData(new DefaultAsyncCallback<LeaseChargesDataDTO>() {
            @Override
            public void onSuccess(LeaseChargesDataDTO result) {
                // update summary gadgets:
                getValue().leaseChargesData().set(result);
                ClientEventBus.instance.fireEvent(new ApplicationWizardStateChangeEvent(getWizard(), ApplicationWizardStateChangeEvent.ChangeType.termChange));
            }
        }, getValue().unitOptionsSelection().<UnitOptionsSelectionDTO> duplicate());
    }

    private class StepDataForm extends CForm<UnitOptionsSelectionDTO> {

        public StepDataForm() {
            super(UnitOptionsSelectionDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().selectedService().agreedPrice(), new CMoneyLabel()).decorate().customLabel(i18n.tr("Monthly Unit Price"));

            depositPanel = new FormPanel(this);
            depositPanel.h3(i18n.tr("Unit Deposits"));
            depositPanel.append(Location.Left, proto().selectedService().deposits(), new DepositFolder());
            formPanel.append(Location.Left, depositPanel);

            petsPanel = new FormPanel(this);
            petsPanel.h2(i18n.tr("Pets"));
            petsPanel.append(Location.Left, proto().selectedPets(), petFolder = new FeatureFolder2(ARCode.Type.Pet));
            formPanel.append(Location.Left, petsPanel);

            parkingPanel = new FormPanel(this);
            parkingPanel.h2(i18n.tr("Parking"));
            parkingPanel.append(Location.Left, proto().selectedParking(), parkingFolder = new FeatureFolder2(ARCode.Type.Parking));
            formPanel.append(Location.Left, parkingPanel);

            storagePanel = new FormPanel(this);
            storagePanel.h2(i18n.tr("Storage"));
            storagePanel.append(Location.Left, proto().selectedStorage(), lockerFolder = new FeatureFolder2(ARCode.Type.Locker));
            formPanel.append(Location.Left, storagePanel);

            chargedPanel = new FormPanel(this);
            chargedPanel.h2(i18n.tr("Utilities"));
            chargedPanel.append(Location.Left, proto().selectedUtilities(), new FeatureFolder2(ARCode.Type.Utility));
            formPanel.append(Location.Left, chargedPanel);

            otherPanel = new FormPanel(this);
            otherPanel.h2(i18n.tr("Other"));
            otherPanel.append(Location.Left, proto().selectedOther(), new FeatureFolder2(ARCode.Type.OneTime));
            formPanel.append(Location.Left, otherPanel);

            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            depositPanel.setVisible(!getValue().selectedService().deposits().isEmpty());
            petsPanel.setVisible(!getValue().selectedPets().isEmpty() || !getValue().availablePets().isEmpty());
            parkingPanel.setVisible(!getValue().selectedParking().isEmpty() || !getValue().availableParking().isEmpty());
            storagePanel.setVisible(!getValue().selectedStorage().isEmpty() || !getValue().availableStorage().isEmpty());
            chargedPanel.setVisible(!getValue().selectedUtilities().isEmpty() || !getValue().availableOther().isEmpty());
            otherPanel.setVisible(!getValue().selectedOther().isEmpty() || !getValue().availableOther().isEmpty());

            if (!getValue().restrictions().isEmpty()) {
                petFolder.setMaxCount(getValue().restrictions().maxPets().getValue());
                parkingFolder.setMaxCount(getValue().restrictions().maxParkingSpots().getValue());
                lockerFolder.setMaxCount(getValue().restrictions().maxParkingSpots().getValue());
            }
        }
    }

    private class FeatureFolder2 extends FeatureFolder {

        public FeatureFolder2(ARCode.Type type) {
            super(type);
        }

        @Override
        protected void addItem() {
            if (getMaxCount() < 0 || getValue().size() < getMaxCount()) {
                new SelectFeatureBox(getType(), OptionsStep.this.getStepValue()) {
                    @Override
                    public boolean onClickOk() {
                        for (ProductItem item : getSelectedItems()) {
                            if (getMaxCount() < 0 || getValue().size() < getMaxCount()) {
                                BillableItem newItem = EntityFactory.create(BillableItem.class);
                                newItem.item().set(item);
                                newItem.agreedPrice().setValue(item.price().isNull() ? BigDecimal.ZERO : item.price().getValue());
                                addItem(newItem);
                            }
                        }
                        return true;
                    }
                }.show();
            }
        }

        @Override
        protected void removeItem(CFolderItem<BillableItem> item) {
            super.removeItem(item);
        }
    }

    private abstract static class SelectFeatureBox extends EntitySelectorListDialog<ProductItem> {

        public SelectFeatureBox(ARCode.Type type, UnitOptionsSelectionDTO selectionData) {
            super(i18n.tr("Select {0}(s)", type), true, getAvailableList(type, selectionData));
        }

        private static List<ProductItem> getAvailableList(ARCode.Type type, UnitOptionsSelectionDTO selectionData) {
            List<ProductItem> available = null;

            switch (type) {
            case AddOn:
            case Utility:
                available = selectionData.availableUtilities();
                break;
            case Pet:
                available = selectionData.availablePets();
                break;
            case Parking:
                available = selectionData.availableParking();
                break;
            case Locker:
                available = selectionData.availableStorage();
                break;
            default:
                available = selectionData.availableOther();
                break;
            }

            return available;
        }
    }
}
