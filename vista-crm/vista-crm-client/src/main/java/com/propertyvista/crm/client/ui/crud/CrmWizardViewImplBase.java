/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.wizard.WizardForm;
import com.pyx4j.site.client.ui.wizard.WizardViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.components.AnchorButton;

public class CrmWizardViewImplBase<E extends IEntity> extends WizardViewImplBase<E> {

    private static final I18n i18n = I18n.get(CrmWizardViewImplBase.class);

    protected String defaultCaption;

    protected Button btnFinish;

    public CrmWizardViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        super();

        btnFinish = new Button(i18n.tr("Finish"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!getForm().isValid()) {
                    getForm().setUnconditionalValidationErrorRendering(true);
                    showValidationDialog();
                } else {
                    getPresenter().finish();
                }
            }
        });
        addFooterToolbarItem(btnFinish);

        defaultCaption = (placeClass != null ? AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption() : "");
        setCaption(defaultCaption);

        AnchorButton btnCancel = new AnchorButton(i18n.tr("Cancel"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getPresenter().cancel();
            }
        });
        addFooterToolbarItem(btnCancel);

        enableButtons(false);
    }

    @Override
    protected void setForm(WizardForm<? extends E> form) {
        super.setForm(form);
        this.getForm().addValueChangeHandler(new ValueChangeHandler<E>() {
            @Override
            public void onValueChange(ValueChangeEvent<E> event) {
                enableButtons(true);
            }
        });
    }

    public CrmWizardViewImplBase(Class<? extends CrudAppPlace> placeClass, WizardForm<E> form) {
        this(placeClass);
        setForm(form);
    }

    @Override
    public void populate(E value) {
        enableButtons(false);
        setCaption(defaultCaption + " " + (value == null ? "" : value.getStringView()));
        super.populate(value);
    }

    protected void enableButtons(boolean enable) {
//        
// TODO Currently buttons are enabled always - more precise form dirty-state mechanics should be implemented!..
//        
//        btnApply.setEnabled(enable);
//        btnSave.setEnabled(enable);
    }

}
