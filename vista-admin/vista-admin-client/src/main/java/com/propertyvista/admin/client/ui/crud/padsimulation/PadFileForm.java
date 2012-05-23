/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.padsimulation;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.themes.AdminTheme;
import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimFile;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;

public class PadFileForm extends AdminEntityForm<PadSimFile> {

    private static final I18n i18n = I18n.get(PadFileForm.class);

    protected final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(AdminTheme.defaultTabHeight, Unit.EM);

    public PadFileForm() {
        this(false);
    }

    public PadFileForm(boolean viewMode) {
        super(PadSimFile.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        tabPanel.add(createDetailsTab(), i18n.tr("Details"));

        tabPanel.add(isEditable() ? new HTML() : ((PadFileViewerView) getParentView()).getBatchListerView().asWidget(), i18n.tr("PadSimBatches"));
        tabPanel.setLastTabDisabled(isEditable());

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    private Widget createDetailsTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sent()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().created()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().updated()), 10).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().acknowledged()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().recordsCount()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fileAmount()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().acknowledgmentStatusCode()), 10).build());

        return new ScrollPanel(main);
    }
}