/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import java.math.BigDecimal;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationStatus;

public class ApplicationStatusFolder extends VistaBoxFolder<OnlineApplicationStatus> {

    public ApplicationStatusFolder() {
        super(OnlineApplicationStatus.class, false);
    }

    @Override
    public VistaBoxFolderItemDecorator<OnlineApplicationStatus> createItemDecorator() {
        VistaBoxFolderItemDecorator<OnlineApplicationStatus> decor = super.createItemDecorator();
        decor.setCaptionFormatter(new IFormatter<OnlineApplicationStatus, SafeHtml>() {
            @Override
            public SafeHtml format(OnlineApplicationStatus value) {
                return SafeHtmlUtils.fromString(value.customer().getStringView() + ", " + value.role().getStringView() + ": " + value.status().getStringView()
                        + ", " + NumberFormat.getFormat("#").format(value.progress().getValue().multiply(new BigDecimal(100d))) + "%");
            }
        });
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected CForm<? extends OnlineApplicationStatus> createItemForm(IObject<?> member) {
        return new CForm<OnlineApplicationStatus>(OnlineApplicationStatus.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().customer()).decorate();
                formPanel.append(Location.Left, proto().role()).decorate();

                formPanel.append(Location.Right, proto().status()).decorate();
                formPanel.append(Location.Right, proto().submissionDate()).decorate();
                formPanel.append(Location.Right, proto().progress()).decorate();
                formPanel.append(Location.Right, proto().daysOpen()).decorate();

                return formPanel;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().submissionDate()).setVisible(!getValue().submissionDate().isNull());
                get(proto().progress()).setVisible(getValue().submissionDate().isNull());
            }
        };
    }
}