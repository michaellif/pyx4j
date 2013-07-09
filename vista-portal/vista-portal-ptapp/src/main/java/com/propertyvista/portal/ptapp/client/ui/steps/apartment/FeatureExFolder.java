/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.apartment;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.BillableItem;

public class FeatureExFolder extends VistaBoxFolder<BillableItem> {

    private static final I18n i18n = I18n.get(FeatureExFolder.class);

    private final ARCode.Type type;

    private final ApartmentViewForm apartmentViewForm;

    private int maxCount;

    public FeatureExFolder(ARCode.Type type, ApartmentViewForm apartmentViewForm, boolean modifiable) {
        super(BillableItem.class, modifiable);

        this.type = type;
        this.apartmentViewForm = apartmentViewForm;
    }

    protected int getMaxCount() {
        return maxCount;
    };

    protected void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public IFolderItemDecorator<BillableItem> createItemDecorator() {
        BoxFolderItemDecorator<BillableItem> decor = (BoxFolderItemDecorator<BillableItem>) super.createItemDecorator();
        decor.setExpended(apartmentViewForm != null);
        return decor;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof BillableItem) {
            CComponent<?> comp = new FeatureExEditor();
            if (isAddable()) {
                comp.inheritViewable(false); // allow editing behavior (default) if folder modifiable...
            }
            return comp;

        } else {
            return super.create(member);
        }
    }

    @Override
    protected void addItem() {
        if (apartmentViewForm != null) {
            if (getValue().size() < getMaxCount()) {
                new SelectFeatureBox(type, apartmentViewForm.getValue()) {
                    @Override
                    public boolean onClickOk() {
                        for (ProductItem item : getSelectedItems()) {
                            if (getValue().size() < getMaxCount()) {
                                BillableItem newItem = EntityFactory.create(BillableItem.class);
                                newItem.item().set(item);
                                newItem.agreedPrice().setValue(item.price().getValue());
                                addItem(newItem);
                            }
                        }
                        return true;
                    }
                }.show();
            } else {
                MessageDialog.warn(i18n.tr("Sorry"), i18n.tr("You cannot add more than {0} items here!", getMaxCount()));
            }
        }
    }

    @Override
    public void addValidations() {
        addValueValidator(new EditableValueValidator<IList<BillableItem>>() {
            @Override
            public ValidationError isValid(CComponent<IList<BillableItem>> component, IList<BillableItem> value) {
                if (value == null) {
                    return null;
                }
                return (value.size() < getMaxCount()) ? null : new ValidationError(component,
                        i18n.tr("You cannot add more than {0} items here!", getMaxCount()));
            }
        });
        super.addValidations();
    }
}