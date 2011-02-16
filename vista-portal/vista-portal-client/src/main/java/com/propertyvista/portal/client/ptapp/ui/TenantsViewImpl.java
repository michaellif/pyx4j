/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Singleton;
import com.propertyvista.portal.domain.pt.Tenants;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.essentials.client.crud.CrudDebugId;

@Singleton
public class TenantsViewImpl extends FlowPanel implements TenantsView {

    private Presenter presenter;

    private final CEntityEditableComponent<Tenants> form;

    private static I18n i18n = I18nFactory.getI18n(TenantsViewImpl.class);

    public TenantsViewImpl() {
        form = new TenantsViewForm();
        add(form);

        Button saveButton = new Button(i18n.tr("Save and Continue"));
        saveButton.ensureDebugId(CrudDebugId.Crud_Save.toString());
        saveButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.save(form.getValue());
            }

        });
        saveButton.getElement().getStyle().setProperty("margin", "3px 20px 3px 8px");
        add(saveButton);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(Tenants entity) {
        form.populate(entity);
    }

}
