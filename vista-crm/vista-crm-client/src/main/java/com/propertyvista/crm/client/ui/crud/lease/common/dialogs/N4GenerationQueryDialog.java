/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common.dialogs;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationQueryDTO;

public class N4GenerationQueryDialog extends OkCancelDialog {

    private final N4GenerationQueryForm form;

    public N4GenerationQueryDialog() {
        super("");
        form = new N4GenerationQueryForm();
        form.initContent();
        form.populateNew();
        setBody(form);
    }

    @Override
    public boolean onClickOk() {
        form.revalidate();

        return form.isValid();
    }

    public N4GenerationQueryDTO getValue() {
        return form.getValue();
    }

    private static final class N4GenerationQueryForm extends CEntityDecoratableForm<N4GenerationQueryDTO> {

        public N4GenerationQueryForm() {
            super(N4GenerationQueryDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel panel = new BasicFlexFormPanel();
            panel.setWidget(0, 0, 2, new FormDecoratorBuilder(inject(proto().agent())).componentWidth("200px").build());
            panel.setWidget(1, 0, 2, new FormDecoratorBuilder(inject(proto().noticeDate())).componentWidth("150px").build());
            return panel;
        }

    }
}
