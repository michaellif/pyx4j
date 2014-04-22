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

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.visor.AbstractVisorEditor;

import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;

public class PreauthorizedPaymentsVisorView extends AbstractVisorEditor<PreauthorizedPaymentsDTO> {

    private final CForm<PreauthorizedPaymentsDTO> form;

    public PreauthorizedPaymentsVisorView(PreauthorizedPaymentsVisorController controller) {
        super(controller);

        setForm(form = new PreauthorizedPaymentsForm(this));
        getElement().getStyle().setProperty("padding", "6px");
    }

    @Override
    public PreauthorizedPaymentsVisorController getController() {
        return (PreauthorizedPaymentsVisorController) super.getController();
    }

    public void populate(final Command onPopulate) {
        getController().retrieve(new DefaultAsyncCallback<PreauthorizedPaymentsDTO>() {
            @Override
            public void onSuccess(PreauthorizedPaymentsDTO result) {
                populate(result);
                onPopulate.execute();
            }
        });
    }
}
