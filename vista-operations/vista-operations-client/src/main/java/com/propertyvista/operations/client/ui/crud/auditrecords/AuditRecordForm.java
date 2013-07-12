/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.auditrecords;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.AuditRecordOperationsDTO;

public class AuditRecordForm extends OperationsEntityForm<AuditRecordOperationsDTO> {

    private static final I18n i18n = I18n.get(AuditRecordForm.class);

    public AuditRecordForm(IForm<AuditRecordOperationsDTO> view) {
        super(AuditRecordOperationsDTO.class, view);

        FormFlexPanel detailsTab = new FormFlexPanel(i18n.tr("Details"));
        int row = -1;
        detailsTab.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().userName())).build());
        detailsTab.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().userKey(), new CLabel<Key>())).build());
        detailsTab.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().remoteAddr())).build());
        detailsTab.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().sessionId())).build());
        detailsTab.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().when())).build());
        detailsTab.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().worldTime())).build());
        detailsTab.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().event())).build());
        detailsTab.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().pmc())).build());
        detailsTab.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().application())).build());
        detailsTab.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().targetEntity())).build());
        detailsTab.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().details())).build());

        selectTab(addTab(detailsTab));
    }

}
