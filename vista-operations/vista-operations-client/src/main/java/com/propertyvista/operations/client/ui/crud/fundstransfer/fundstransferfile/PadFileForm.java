/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundstransferfile;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.PadFileDTO;

public class PadFileForm extends OperationsEntityForm<PadFileDTO> {

    public PadFileForm(IForm<PadFileDTO> view) {
        super(PadFileDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fileCreationNumber())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fileName())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().companyId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().status())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fundsTransferType())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().sent())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().created())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().updated())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledged())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().recordsCount())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fileAmount())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledgmentStatusCode())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledgmentRejectReasonMessage())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledgmentStatus())).build());

        selectTab(addTab(panel));
        setTabBarVisible(false);
    }
}
