/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 22, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps.summary;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.widgets.client.CollapsablePanel;
import com.pyx4j.widgets.client.event.shared.ToggleEvent;
import com.pyx4j.widgets.client.event.shared.ToggleHandler;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.portal.prospect.themes.SummaryStepTheme;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public abstract class AbstractSectionPanel extends CollapsablePanel {

    private final SectionCaptionBar captionBar;

    private final BasicFlexFormPanel contentPanel;

    private final SummaryForm form;

    private final ApplicationWizardStep step;

    private final OnlineApplicationDTO entityPrototype;

    private final HTML errorMessageBar;

    private int row = -1;

    public AbstractSectionPanel(int index, String caption, SummaryForm form, ApplicationWizardStep step) {
        super(VistaImages.INSTANCE);
        this.form = form;
        this.step = step;
        this.captionBar = new SectionCaptionBar(index, caption);
        this.contentPanel = new BasicFlexFormPanel();
        this.entityPrototype = EntityFactory.getEntityPrototype(OnlineApplicationDTO.class);

        setStyleName(SummaryStepTheme.StyleName.SummaryStepSection.name());

        FlowPanel mainPanel = new FlowPanel();
        mainPanel.setWidth("100%");
        setWidget(mainPanel);
        mainPanel.add(captionBar);

        errorMessageBar = new HTML();
        errorMessageBar.setStyleName(VistaTheme.StyleName.ErrorMessage.name());
        mainPanel.add(errorMessageBar);

        mainPanel.add(contentPanel);

        addToggleHandler(new ToggleHandler() {

            @Override
            public void onToggle(ToggleEvent event) {
                errorMessageBar.setVisible(event.isToggleOn());
                contentPanel.setVisible(event.isToggleOn());

            }
        });

        setExpended(false);
    }

    protected void addField(IObject<?> member) {
        contentPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(form.inject(member)).build());
    }

    protected void addField(IObject<?> member, CComponent<?> component, boolean decorate) {
        if (decorate) {
            contentPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(form.inject(member, component)).build());
        } else {
            contentPanel.setWidget(++row, 0, form.inject(member, component));
        }
    }

    protected void addCaption(String caption) {
        contentPanel.setH4(++row, 0, 0, caption);
    }

    public OnlineApplicationDTO proto() {
        return entityPrototype;
    }

    public void onValueSet() {

        captionBar.updateState();

        errorMessageBar.setHTML(step.getValidationResults().getValidationMessage(true, true, false));
    }

    class SectionCaptionBar extends FlowPanel {

        private final Image indexLabel;

        public SectionCaptionBar(int index, String caption) {

            setStyleName(SummaryStepTheme.StyleName.SummaryStepSectionCaptionBar.name());

            Label captionLabel = new Label(caption);
            captionLabel.setStyleName(SummaryStepTheme.StyleName.SummaryStepSectionCaption.name());
            add(captionLabel);

            indexLabel = new Image();
            indexLabel.addStyleName(SummaryStepTheme.StyleName.SummaryStepSectionStatus.name());
            add(indexLabel);

        }

        void updateState() {
            if (step.isStepComplete()) {
                indexLabel.setResource(PortalImages.INSTANCE.messageSuccess());
            } else if (step.isStepVisited()) {
                indexLabel.setResource(PortalImages.INSTANCE.messageError());
            } else {
                indexLabel.setResource(null);
            }
        }

    }

}