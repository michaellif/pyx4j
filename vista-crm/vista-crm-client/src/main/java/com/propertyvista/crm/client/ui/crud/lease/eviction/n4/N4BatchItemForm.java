/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2015
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction.n4;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.domain.tenant.lease.Lease;

public class N4BatchItemForm extends CrmEntityForm<N4BatchItem> {

    private static final I18n i18n = I18n.get(N4BatchItemForm.class);

    public N4BatchItemForm(IPrimeFormView<N4BatchItem, ?> view) {
        super(N4BatchItem.class, view);

        FormPanel formPanel = new FormPanel(this);

        CEntityLabel<N4Batch> batchLabel = isEditable() ? new CEntityLabel<N4Batch>() : new CEntityCrudHyperlink<N4Batch>(
                AppPlaceEntityMapper.resolvePlace(N4Batch.class)) {
            @Override
            protected void onValuePropagation(N4Batch value, boolean fireEvent, boolean populate) {
                super.onValuePropagation(value, fireEvent, populate);
            }
        };

        CEntityLabel<Lease> leaseLabel = isEditable() ? new CEntityLabel<Lease>() : new CEntityCrudHyperlink<Lease>(
                AppPlaceEntityMapper.resolvePlace(Lease.class));
        leaseLabel.setFormatter(new IFormatter<Lease, String>() {

            @Override
            public String format(Lease value) {
                return value == null ? null : SimpleMessageFormat.format( //
                        "{0}, {1}, {2}{3,choice,null# - {3}}", //
                        value.unit().building().propertyCode(), value.unit(), value.type(), value._applicant());
            }
        });

        formPanel.append(Location.Left, proto().batch(), batchLabel).decorate();
        formPanel.append(Location.Left, proto().lease(), leaseLabel).decorate();
        formPanel.append(Location.Dual, proto().leaseArrears(), new N4LeaseArrearsEditor());

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
