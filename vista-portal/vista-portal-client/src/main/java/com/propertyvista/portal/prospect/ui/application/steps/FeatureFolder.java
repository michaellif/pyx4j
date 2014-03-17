/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 21, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemExtraData;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;
import com.propertyvista.portal.prospect.ui.application.editors.PetDataEditor;
import com.propertyvista.portal.prospect.ui.application.editors.VehicleDataEditor;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class FeatureFolder extends PortalBoxFolder<BillableItem> {

    private static final I18n i18n = I18n.get(FeatureFolder.class);

    private final ARCode.Type type;

    private int maxCount = -1;

    public FeatureFolder() {
        super(BillableItem.class);
        type = null;
    }

    public FeatureFolder(ARCode.Type type) {
        super(BillableItem.class, type.toString());
        this.type = type;
    }

    public ARCode.Type getType() {
        return type;
    }

    public int getMaxCount() {
        return maxCount;
    };

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof BillableItem) {
            return new FeatureItemForm();
        } else {
            return super.create(member);
        }
    }

    class FeatureItemForm extends CEntityForm<BillableItem> {

        private final BasicFlexFormPanel depositPanel = new BasicFlexFormPanel();

        private final SimplePanel extraDataPanel = new SimplePanel();

        public FeatureItemForm() {
            super(BillableItem.class);
            setEditable(false);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().item().name(), new CLabel<String>())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().agreedPrice(), new CMoneyLabel())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().description(), new CLabel<String>())).build());
            content.setWidget(++row, 0, extraDataPanel);
            content.setWidget(++row, 0, depositPanel);

            depositPanel.setH4(0, 0, 1, proto().deposits().getMeta().getCaption());
            depositPanel.setWidget(1, 0, 1, inject(proto().deposits(), new DepositFolder()));

            return content;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            @SuppressWarnings("unchecked")
            CEntityFolderItem<BillableItem> item = (CEntityFolderItem<BillableItem>) getParent();
            item.setRemovable(!isMandatoryFeature(getValue().item().product()));

            depositPanel.setVisible(!getValue().deposits().isEmpty());

            get(proto().description()).setVisible(!getValue().description().isNull());
        }

        private boolean isMandatoryFeature(Product.ProductV product) {
            return product.isInstanceOf(Feature.FeatureV.class) && ((Feature.FeatureV) product.cast()).mandatory().isBooleanTrue();
        }

        @Override
        protected void onValuePropagation(BillableItem value, boolean fireEvent, boolean populate) {
            setExtraDataEditor(value, populate);
            super.onValuePropagation(value, fireEvent, populate);
        }

        @SuppressWarnings("unchecked")
        private void setExtraDataEditor(BillableItem value, boolean populate) {

            if (this.contains(proto().extraData())) {
                this.unbind(proto().extraData());
                extraDataPanel.setWidget(null);
            }

            if (value != null) {
                @SuppressWarnings("rawtypes")
                CEntityForm editor = null;
                BillableItemExtraData extraData = value.extraData();

                if (ARCode.Type.features().contains(value.item().product().holder().code().type().getValue())) {
                    switch (value.item().product().holder().code().type().getValue()) {
                    case Parking:
                        editor = new VehicleDataEditor();
                        if (extraData.getInstanceValueClass() != Vehicle.class) {
                            extraData.set(EntityFactory.create(Vehicle.class));
                        }
                        break;
                    case Pet:
                        editor = new PetDataEditor();
                        if (extraData.getInstanceValueClass() != Pet.class) {
                            extraData.set(EntityFactory.create(Pet.class));
                        }
                        break;
                    default:
                        // ok - there is no extra-data for other types!.. 
                    }
                }

                if (editor != null) {
                    editor.setEditable(FeatureFolder.this.isEditable());
                    editor.inheritEditable(false);
                    this.inject(proto().extraData(), editor);
                    editor.populate(extraData.cast());
                    extraDataPanel.setWidget(editor);
                }
            }
        }
    }

    @Override
    protected void addItem() {
        assert false : "Not supported functionality - the method should be overridden!";
    }

    @Override
    protected void removeItem(final CEntityFolderItem<BillableItem> item) {
        MessageDialog.confirm(i18n.tr("Feature removal"), i18n.tr("Do you really want to remove the Feature?"), new Command() {
            @Override
            public void execute() {
                FeatureFolder.super.removeItem(item);
            }
        });
    }

    @Override
    public void addValidations() {
        addComponentValidator(new AbstractComponentValidator<IList<BillableItem>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() == null || getMaxCount() < 0) {
                    return null;
                }
                return (getComponent().getValue().size() < getMaxCount()) ? null : new FieldValidationError(getComponent(), i18n.tr(
                        "You cannot add more than {0} items here!", getMaxCount()));
            }
        });
        super.addValidations();
    }
}