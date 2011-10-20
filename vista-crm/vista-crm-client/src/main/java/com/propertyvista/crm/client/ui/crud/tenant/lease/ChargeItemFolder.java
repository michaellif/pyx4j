/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.ChargeItemAdjustment;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.financial.offering.extradata.Vehicle;
import com.propertyvista.dto.LeaseDTO;

class ChargeItemFolder extends VistaBoxFolder<ChargeItem> {

    private static I18n i18n = I18n.get(ChargeItemFolder.class);

    private final CEntityEditor<LeaseDTO> parent;

    private final LeaseEditorView.Presenter presenter;

    public ChargeItemFolder(CEntityEditor<LeaseDTO> parent) {
        this(parent, null); // view mode constructor
    }

    public ChargeItemFolder(CEntityEditor<LeaseDTO> parent, LeaseEditorView.Presenter presenter) {
        super(ChargeItem.class);
        this.parent = parent;
        this.presenter = presenter;
    }

    @Override
    protected void addItem() {
        if (parent.getValue().serviceAgreement().serviceItem().isNull()) {
            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("Select Service Item first!"));
        } else {
            new ShowPopUpBox<SelectFeatureBox>(new SelectFeatureBox()) {
                @Override
                protected void onClose(SelectFeatureBox box) {
                    if (box.getSelectedItems() != null) {
                        for (ServiceItem item : box.getSelectedItems()) {
                            ChargeItem newItem = EntityFactory.create(ChargeItem.class);
                            newItem.item().set(item);
                            newItem.price().setValue(item.price().getValue());
                            newItem.adjustedPrice().setValue(item.price().getValue());
                            addItem(newItem);
                        }
                    }
                }
            };
        }
    }

    @Override
    public void populate(IList<ChargeItem> value) {
        super.populate(value);

        // prepopulate utilities for the new item: 
        if (isEditable() && value.isEmpty()) {
            for (ServiceItem item : parent.getValue().selectedUtilityItems()) {
                ChargeItem newItem = EntityFactory.create(ChargeItem.class);
                newItem.item().set(item);
                newItem.price().setValue(item.price().getValue());
                addItem(newItem);
            }
        }
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ChargeItem) {
            return new ChargeItemEditor();
        }
        return super.create(member);
    }

    class ChargeItemEditor extends CEntityEditor<ChargeItem> {

        private final SimplePanel extraDataPanel = new SimplePanel();

        private VistaDecoratorsFlowPanel adjustmentPanel;

        public ChargeItemEditor() {
            super(ChargeItem.class);
        }

        @Override
        public IsWidget createContent() {
            VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable(), 10);
            VistaDecoratorsSplitFlowPanel split;

            CLabel lb;
            main.add(split = new VistaDecoratorsSplitFlowPanel(!isEditable(), 10, 22));
            split.getLeftPanel().add(inject(proto().item().type().name(), lb = new CLabel()));
            lb.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

            CNumberLabel nl;
            split.getRightPanel().add(inject(proto().price(), nl = new CNumberLabel()), 6);
            nl.setNumberFormat(proto().price().getMeta().getFormat());

            split.getRightPanel().add(inject(proto().adjustedPrice(), nl = new CNumberLabel()), 6);
            nl.setNumberFormat(proto().adjustedPrice().getMeta().getFormat());
            nl.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

            main.add(extraDataPanel);

            adjustmentPanel = new VistaDecoratorsFlowPanel(!isEditable(), 10);
            adjustmentPanel.add(new CrmSectionSeparator(i18n.tr("Adjustments:")));
            adjustmentPanel.add(inject(proto().adjustments(), new ChargeItemAdjustmentFolder()));
            main.add(adjustmentPanel);
            return main;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void populate(ChargeItem value) {
            super.populate(value);

            if (value.item().type().featureType().getValue() == Feature.Type.utility) {
                // TODO - how to ?
//                    setRemovable(false);
            }

            if (!isEditable()) {
                adjustmentPanel.setVisible(!value.adjustments().isEmpty());
                get(proto().adjustedPrice()).setVisible(!value.adjustments().isEmpty());
            }

            CEntityEditor editor = null;
            switch (value.item().type().featureType().getValue()) {
            case parking:
                editor = new CEntityEditor<Vehicle>(Vehicle.class) {
                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel panel = new VistaDecoratorsFlowPanel(!isEditable(), 10);
                        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable(), 10, 30);

                        panel.add(new HTML(HtmlUtils.h5(i18n.tr("Vehicle data:"))));
                        panel.add(split);

                        split.getLeftPanel().add(inject(proto().year()), 5);
                        split.getLeftPanel().add(inject(proto().make()), 10);
                        split.getLeftPanel().add(inject(proto().model()), 10);

                        split.getRightPanel().add(inject(proto().plateNumber()), 10);
                        split.getRightPanel().add(inject(proto().country()), 10);
                        split.getRightPanel().add(inject(proto().province()), 17);

                        return panel;
                    }
                };

                if (value.extraData().isNull()) {
                    value.extraData().set(EntityFactory.create(Vehicle.class));
                }
                break;
            case pet:
                editor = new CEntityEditor<Pet>(Pet.class) {
                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel panel = new VistaDecoratorsFlowPanel(!isEditable(), 10);
                        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable(), 10, 30);

                        panel.add(new HTML(HtmlUtils.h5(i18n.tr("Pet data:"))));
                        panel.add(split);

                        split.getLeftPanel().add(inject(proto().name()), 15);
                        split.getLeftPanel().add(inject(proto().color()), 10);
                        split.getLeftPanel().add(inject(proto().breed()), 15);

                        split.getRightPanel().add(inject(proto().weight()), 4);
                        split.getRightPanel().add(inject(proto().weightUnit()), 4);
                        split.getRightPanel().add(inject(proto().birthDate()), 8.2);

                        return panel;
                    }
                };

                if (value.extraData().isNull()) {
                    value.extraData().set(EntityFactory.create(Pet.class));
                }
                break;
            }

            if (editor != null) {
                editor.onBound(this);
                editor.populate(value.extraData().cast());
                extraDataPanel.setWidget(editor);
            }
        }

        @Override
        public void addValidations() {
            super.addValidations();
            addValueChangeHandler(new ValueChangeHandler<ChargeItem>() {
                @Override
                public void onValueChange(ValueChangeEvent<ChargeItem> event) {
                    calculateAdjustments();
                }
            });
        }

        private void calculateAdjustments() {
            if (isEditable()) {
                presenter.calculateChargeItemAdjustments(new AsyncCallback<Double>() {

                    @Override
                    public void onSuccess(Double result) {
                        get(proto().adjustedPrice()).setValue(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        // TODO Auto-generated method stub
                    }
                }, getValue());
            }
        }

        private class ChargeItemAdjustmentFolder extends VistaTableFolder<ChargeItemAdjustment> {

            public ChargeItemAdjustmentFolder() {
                super(ChargeItemAdjustment.class);
            }

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().chargeType(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().termType(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
                return columns;
            }

        }
    }

    private class SelectFeatureBox extends OkCancelBox {

        private ListBox list;

        private List<ServiceItem> selectedItems;

        public SelectFeatureBox() {
            super(i18n.tr("Select Features"));
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);

            if (!parent.getValue().selectedFeatureItems().isEmpty()) {
                list = new ListBox(true);
                list.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        okButton.setEnabled(list.getSelectedIndex() >= 0);
                    }
                });

                //  TODO not sure if we need duplicate item restriction:                
//                    List<ServiceItem> alreadySelected = new ArrayList<ServiceItem>();
//                    for (ChargeItem item : getValue().serviceAgreement().featureItems()) {
//                        alreadySelected.add(item.item());
//                    }

                for (ServiceItem item : parent.getValue().selectedFeatureItems()) {
//                        if (!alreadySelected.contains(item)) {
                    list.addItem(item.getStringView());
                    list.setValue(list.getItemCount() - 1, item.id().toString());
//                        }
                }

                if (list.getItemCount() > 0) {
                    list.setVisibleItemCount(8);
                    list.setWidth("100%");
                    return list.asWidget();
                } else {
                    return new HTML(i18n.tr("All features have already been selected!"));
                }
            } else {
                return new HTML(i18n.tr("There are no features for this service!"));
            }
        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        protected boolean onOk() {
            selectedItems = new ArrayList<ServiceItem>(4);
            for (int i = 0; i < list.getItemCount(); ++i) {
                if (list.isItemSelected(i)) {
                    for (ServiceItem item : parent.getValue().selectedFeatureItems()) {
                        if (list.getValue(i).contentEquals(item.id().toString())) {
                            selectedItems.add(item);
                        }
                    }
                }
            }
            return super.onOk();
        }

        @Override
        protected void onCancel() {
            selectedItems = null;
        }

        protected List<ServiceItem> getSelectedItems() {
            return selectedItems;
        }
    }
}