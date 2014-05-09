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
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.client.themes.OperationsTheme;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.AuditRecordOperationsDTO;
import com.propertyvista.operations.rpc.dto.PmcDTO;

public class AuditRecordForm extends OperationsEntityForm<AuditRecordOperationsDTO> {

    private static final I18n i18n = I18n.get(AuditRecordForm.class);

    public AuditRecordForm(IForm<AuditRecordOperationsDTO> view) {
        super(AuditRecordOperationsDTO.class, view);

        setTabBarVisible(false);

        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().userName()).decorate();
        formPanel.append(Location.Right, proto().remoteAddr()).decorate();
        formPanel.append(Location.Left, proto().userKey(), new CLabel<Key>()).decorate();
        formPanel.append(Location.Right, proto().sessionId()).decorate();
        formPanel.append(Location.Left, proto().event()).decorate();
        formPanel.append(Location.Right, proto().when()).decorate();
        formPanel.append(Location.Left, proto().namespace()).decorate();
        formPanel.append(Location.Right, proto().worldTime()).decorate();
        formPanel.append(Location.Left, proto().pmc(), new CEntityCrudHyperlink<Pmc>(AppPlaceEntityMapper.resolvePlace(PmcDTO.class))).decorate();
        formPanel.append(Location.Left, proto().application()).decorate();
        formPanel.append(Location.Right, proto().targetEntity()).decorate();
        formPanel.append(Location.Dual, proto().details()).decorate();

        selectTab(addTab(formPanel, i18n.tr("Audit Record")));

        get(proto().details()).asWidget().setStyleName(OperationsTheme.OperationsStyles.TextFieldPreformatted.name(), true);

    }
}
