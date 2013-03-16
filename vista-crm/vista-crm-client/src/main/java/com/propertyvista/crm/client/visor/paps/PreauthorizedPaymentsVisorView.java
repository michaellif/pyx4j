/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-14
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.paps;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.ui.visor.IVisorEditor;

import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;

public class PreauthorizedPaymentsVisorView extends ScrollPanel implements IVisorEditor {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsVisorView.class);

    private final CEntityForm<PreauthorizedPaymentsDTO> form = new PreauthorizedPaymentsForm();

    private final PreauthorizedPaymentsVisorController controller;

    public PreauthorizedPaymentsVisorView(PreauthorizedPaymentsVisorController controller) {
        this.controller = controller;

        // UI:
        form.initContent();
        setWidget(form.asWidget());
        getElement().getStyle().setProperty("padding", "6px");
    }

    public void populate(final Command onPopulate) {
        controller.populate(new DefaultAsyncCallback<PreauthorizedPaymentsDTO>() {
            @Override
            public void onSuccess(PreauthorizedPaymentsDTO result) {
                form.populate(result);
                onPopulate.execute();
            }
        });
    }

    @Override
    public void save(AsyncCallback<VoidSerializable> callback) {
        controller.save(callback, form.getValue());
    }

    @Override
    public void apply() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onBeforeClose() {
        // TODO Auto-generated method stub
        return true;
    }
}
