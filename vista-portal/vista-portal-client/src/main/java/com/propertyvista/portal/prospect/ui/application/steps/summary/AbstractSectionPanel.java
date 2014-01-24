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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.widgets.client.CollapsablePanel;
import com.pyx4j.widgets.client.event.shared.ToggleEvent;
import com.pyx4j.widgets.client.event.shared.ToggleHandler;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.portal.prospect.themes.SummaryStepTheme;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.prospect.ui.application.StepIndexLabel;
import com.propertyvista.portal.prospect.ui.application.NavigStepItem.StepStatus;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public abstract class AbstractSectionPanel extends CollapsablePanel {

    private final SectionCaptionBar captionBar;

    private final BasicFlexFormPanel contentPanel;

    private final SummaryForm form;

    private final ApplicationWizardStep step;

    private final OnlineApplicationDTO entityPrototype;

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
        mainPanel.add(contentPanel);

        addToggleHandler(new ToggleHandler() {

            @Override
            public void onToggle(ToggleEvent event) {
                contentPanel.setVisible(event.isToggleOn());
            }
        });

        setExpended(false);
    }

    protected void addField(IObject<?> member) {
        contentPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(form.inject(member)).build());
    }

    protected void addField(IObject<?> member, CComponent<?> component) {
        contentPanel.setWidget(++row, 0, form.inject(member, component));
    }

    protected void addCaption(String caption) {
        contentPanel.setH4(++row, 0, 0, caption);
    }

    public OnlineApplicationDTO proto() {
        return entityPrototype;
    }

    public void updateState() {
        if (step.isStepComplete()) {
            captionBar.setStepStatus(StepStatus.complete);
        } else if (step.isStepVisited()) {
            captionBar.setStepStatus(StepStatus.invalid);
        } else {
            captionBar.setStepStatus(StepStatus.notComplete);
        }
    }

    class SectionCaptionBar extends FlowPanel {

        private final StepIndexLabel indexLabel;

        public SectionCaptionBar(int index, String caption) {

            setStyleName(SummaryStepTheme.StyleName.SummaryStepSectionCaptionBar.name());

            indexLabel = new StepIndexLabel(String.valueOf(index));
            indexLabel.addStyleName(SummaryStepTheme.StyleName.SummaryStepSectionIndex.name());
            indexLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            add(indexLabel);

            Label captionLabel = new Label(caption);
            captionLabel.setStyleName(SummaryStepTheme.StyleName.SummaryStepSectionCaption.name());
            captionLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            add(captionLabel);

            getElement().getStyle().setFloat(Float.NONE);
        }

        public void setStepStatus(StepStatus status) {
            indexLabel.setStatus(status);
        }

    }

}