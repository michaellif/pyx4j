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

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeEvent;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.prospect.ui.application.steps.common.DepositFolder;
import com.propertyvista.portal.prospect.ui.application.steps.common.FeatureFolder;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class OptionsStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(OptionsStep.class);

    private final BasicFlexFormPanel depositPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel chargedPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel petsPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel parkingPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel storagePanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel otherPanel = new BasicFlexFormPanel();

    private FeatureFolder2 lockerFolder;

    private FeatureFolder2 petFolder;

    private FeatureFolder2 parkingFolder;

    public OptionsStep() {
        super(OnlineApplicationWizardStepMeta.Options);
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(getStepTitle());
        int row = -1;

        panel.setWidget(++row, 0, inject(proto().unitOptionsSelection(), new StepDataForm()));

        return panel;
    }

    public void setStepValue(UnitOptionsSelectionDTO value) {
        get(proto().unitOptionsSelection()).setValue(value);
    }

    public UnitOptionsSelectionDTO getStepValue() {
        return getWizard().getValue().unitOptionsSelection();
    }

    private class StepDataForm extends CEntityForm<UnitOptionsSelectionDTO> {

        public StepDataForm() {
            super(UnitOptionsSelectionDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0,
                    new FormWidgetDecoratorBuilder(inject(proto().selectedService().agreedPrice(), new CMoneyLabel())).customLabel(i18n.tr("Unit Price"))
                            .build());
            depositPanel.setH3(0, 0, 1, i18n.tr("Unit Deposits"));
            depositPanel.setWidget(1, 0, 1, inject(proto().selectedService().deposits(), new DepositFolder()));
            content.setWidget(++row, 0, depositPanel);

            petsPanel.setH2(0, 0, 1, i18n.tr("Pets"));
            petsPanel.setWidget(1, 0, inject(proto().selectedPets(), petFolder = new FeatureFolder2(ARCode.Type.Pet)));
            content.setWidget(++row, 0, petsPanel);

            parkingPanel.setH2(0, 0, 1, i18n.tr("Parking"));
            parkingPanel.setWidget(1, 0, inject(proto().selectedParking(), parkingFolder = new FeatureFolder2(ARCode.Type.Parking)));
            content.setWidget(++row, 0, parkingPanel);

            storagePanel.setH2(0, 0, 1, i18n.tr("Storage"));
            storagePanel.setWidget(1, 0, inject(proto().selectedStorage(), lockerFolder = new FeatureFolder2(ARCode.Type.Locker)));
            content.setWidget(++row, 0, storagePanel);

            chargedPanel.setH2(0, 0, 1, i18n.tr("Utilities"));
            chargedPanel.setWidget(1, 0, inject(proto().selectedUtilities(), new FeatureFolder2(ARCode.Type.Utility)));
            content.setWidget(++row, 0, chargedPanel);

            otherPanel.setH2(0, 0, 1, i18n.tr("Other"));
            otherPanel.setWidget(1, 0, inject(proto().selectedOther(), new FeatureFolder2(ARCode.Type.OneTime)));
            content.setWidget(++row, 0, otherPanel);

            return content;
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
                                newItem.agreedPrice().setValue(item.price().getValue());
                                addItem(newItem);
                                ClientEventBus.instance.fireEvent(new ApplicationWizardStateChangeEvent(getWizard(),
                                        ApplicationWizardStateChangeEvent.ChangeType.termChange));
                            }
                        }
                        return true;
                    }
                }.show();
            } else {
                MessageDialog.warn(i18n.tr("Sorry"), i18n.tr("You cannot add more than {0} items here!", getMaxCount()));
            }
        }

        @Override
        protected void removeItem(CEntityFolderItem<BillableItem> item) {
            super.removeItem(item);
            ClientEventBus.instance.fireEvent(new ApplicationWizardStateChangeEvent(getWizard(), ApplicationWizardStateChangeEvent.ChangeType.termChange));
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
