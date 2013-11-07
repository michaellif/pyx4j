/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.css.IStyleName;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.BulkEditableEntity;

public abstract class BulkEditableEntityForm<Item extends BulkEditableEntity> extends CEntityDecoratableForm<Item> {

    public enum Styles implements IStyleName {

        BulkOperationItemSelected;

    }

    public BulkEditableEntityForm(Class<Item> clazz) {
        super(clazz);
    }

    public void setChecked(boolean isChecked) {
        get(proto().isSelected()).setValue(isChecked);
    }

    @Override
    public void addValidations() {
        super.addValidations();
        // This is a hack because we know that this is method called after createContent();
        get(proto().isSelected()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                asWidget().setStyleName(Styles.BulkOperationItemSelected.name(),
                        (get(proto().isSelected()).getValue() != null && get(proto().isSelected()).getValue()));
            }
        });
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        asWidget()
                .setStyleName(Styles.BulkOperationItemSelected.name(), (get(proto().isSelected()).getValue() != null && get(proto().isSelected()).getValue()));
    }

}
