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

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.editors.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.ChargeItemAdjustment;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.financial.offering.extradata.Vehicle;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.dto.LeaseDTO;

class ChargeItemFolder extends VistaBoxFolder<ChargeItem> {

    private static I18n i18n = I18n.get(ChargeItemFolder.class);

    private final CrmEntityForm<LeaseDTO> parent;

    public ChargeItemFolder(boolean modifyable, CrmEntityForm<LeaseDTO> parent) {
        super(ChargeItem.class, modifyable);
        this.parent = parent;
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
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ChargeItem) {
            return new ChargeItemEditor();
        }
        return super.create(member);
    }

    class ChargeItemEditor extends CEntityDecoratableEditor<ChargeItem> {

        private final SimplePanel extraDataPanel = new SimplePanel();

        private final FormFlexPanel adjustmentPanel = new FormFlexPanel();

        public ChargeItemEditor() {
            super(ChargeItem.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();
            int row = -1;

            CLabel lb;
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().item().type().name(), lb = new CLabel())).build());
            lb.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

            CNumberLabel nl;
            main.setWidget(row, 1, new DecoratorBuilder(inject(proto().price(), nl = new CNumberLabel()), 6).build());
            nl.setNumberFormat(proto().price().getMeta().getFormat());

            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().adjustedPrice(), nl = new CNumberLabel()), 6).build());
            nl.setNumberFormat(proto().adjustedPrice().getMeta().getFormat());
            nl.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

            main.setWidget(++row, 0, extraDataPanel);
            main.getFlexCellFormatter().setColSpan(row, 0, 2);

            main.setWidget(++row, 0, adjustmentPanel);
            main.getFlexCellFormatter().setColSpan(row, 0, 2);

            main.getColumnFormatter().setWidth(0, "50%");
            main.getColumnFormatter().setWidth(1, "50%");

            adjustmentPanel.setH1(0, 0, 1, i18n.tr("Adjustments"));
            adjustmentPanel.setWidget(1, 0, inject(proto().adjustments(), new ChargeItemAdjustmentFolder()));

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
                editor = new CEntityDecoratableEditor<Vehicle>(Vehicle.class) {
                    @Override
                    public IsWidget createContent() {
                        FormFlexPanel panel = new FormFlexPanel();

                        int row = -1;
                        panel.setH1(++row, 0, 2, i18n.tr("Vehicle data"));

                        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().year()), 5).build());
                        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().make()), 10).build());
                        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().model()), 10).build());

                        row = 0; // skip header
                        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().plateNumber()), 10).build());
                        CEditableComponent<Country, ?> country;
                        panel.setWidget(++row, 1, new DecoratorBuilder(country = (CEditableComponent<Country, ?>) inject(proto().country()), 13).build());
                        CEditableComponent<Province, ?> province;
                        panel.setWidget(++row, 1, new DecoratorBuilder(province = (CEditableComponent<Province, ?>) inject(proto().province()), 17).build());

                        ProvinceContryFilters.attachFilters(province, country, new OptionsFilter<Province>() {
                            @Override
                            public boolean acceptOption(Province entity) {
                                if (getValue() == null) {
                                    return true;
                                } else {
                                    Country country = (Country) getValue().getMember(proto().country().getPath());
                                    return country.isNull() || EqualsHelper.equals(entity.country().name(), country.name());
                                }
                            }
                        });

                        panel.getColumnFormatter().setWidth(0, "50%");
                        panel.getColumnFormatter().setWidth(1, "50%");

                        return panel;
                    }
                };

                if (value.extraData().isNull()) {
                    value.extraData().set(EntityFactory.create(Vehicle.class));
                }
                break;
            case pet:
                editor = new CEntityDecoratableEditor<Pet>(Pet.class) {
                    @Override
                    public IsWidget createContent() {
                        FormFlexPanel panel = new FormFlexPanel();

                        int row = -1;
                        panel.setH1(++row, 0, 2, i18n.tr("Pet data"));

                        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
                        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().color()), 15).build());
                        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().breed()), 15).build());

                        row = 0; // skip header
                        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().weight()), 4).build());
                        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().weightUnit()), 4).build());
                        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().birthDate()), 8.2).build());

                        panel.getColumnFormatter().setWidth(0, "50%");
                        panel.getColumnFormatter().setWidth(1, "50%");

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
            if (parent.isEditable()) {
                LeaseEditorView.Presenter presenter = (LeaseEditorView.Presenter) ((LeaseEditorView) parent.getParentView()).getPresenter();
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
                super(ChargeItemAdjustment.class, ChargeItemFolder.this.isEditable());
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

//                    List<ServiceItem> alreadySelected = new ArrayList<ServiceItem>();
//                    for (ChargeItem item : getValue().serviceAgreement().featureItems()) {
//                        alreadySelected.add(item.item());
//                    }

                for (ServiceItem item : parent.getValue().selectedFeatureItems()) {
//  TODO not sure if we need duplicate item check here:                
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
                    return new HTML(i18n.tr("All Features have already been selected!"));
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