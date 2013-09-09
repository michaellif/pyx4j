/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.autopayreviewer;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapReviewCaptionDTO;

public class PapReviewCaptionForm extends CEntityDecoratableForm<PapReviewCaptionDTO> {

    private static final I18n i18n = I18n.get(PapReviewCaptionForm.class);

    public enum Styles implements IStyleName {

        AutoPayReviewUpdaterPapCaptionPanel, AutoPayExpectedMoveOut

    }

    public PapReviewCaptionForm() {
        super(PapReviewCaptionDTO.class);
        setViewable(true);
        setEditable(false);
    }

    @Override
    public IsWidget createContent() {

        FlowPanel papCaptionPanel = new FlowPanel();
        papCaptionPanel.setStyleName(Styles.AutoPayReviewUpdaterPapCaptionPanel.name());
        papCaptionPanel.add(inject(proto().building(), createNavigationLabel(proto().building_())));
        papCaptionPanel.add(new HTML("&nbsp;"));
        papCaptionPanel.add(inject(proto().unit(), createNavigationLabel(proto().unit_())));
        papCaptionPanel.add(new HTML("&nbsp;"));
        papCaptionPanel.add(inject(proto().lease(), createNavigationLabel(proto().lease_())));
        papCaptionPanel.add(new HTML("&nbsp;"));
        papCaptionPanel.add(new MiniDecorator(inject(proto().expectedMoveOut()), Styles.AutoPayExpectedMoveOut.name()));
        get(proto().expectedMoveOut()).setTooltip(i18n.tr("Expected Move Out"));
        papCaptionPanel.add(new HTML(":&nbsp;"));
        papCaptionPanel.add(inject(proto().tenant(), createNavigationLabel(proto().tenant_())));
        papCaptionPanel.add(new HTML("&nbsp;"));
        papCaptionPanel.add(inject(proto().paymentMethod(), createNavigationLabel(proto().paymentMethod_())));

        return papCaptionPanel;
    }

    private CLabel<String> createNavigationLabel(IEntity member) {
        CLabel<String> label = new CLabel<String>();
        label.setNavigationCommand(createNavigationCommand(member));
        return label;
    }

    private Command createNavigationCommand(final IEntity protoMember) {
        return new Command() {
            @Override
            public void execute() {
                IEntity member = ((IEntity) (getValue().getMember(protoMember.getPath())));
                AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(member.getInstanceValueClass(), member.getPrimaryKey()));
            }
        };
    }

}
