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
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.CollapsablePanel;
import com.pyx4j.widgets.client.event.shared.ToggleEvent;
import com.pyx4j.widgets.client.event.shared.ToggleHandler;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.portal.prospect.themes.SummaryStepTheme;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.PortalFormPanel;

public abstract class AbstractSectionPanel extends CollapsablePanel {

    private static final I18n i18n = I18n.get(AbstractSectionPanel.class);

    private final SectionCaptionBar captionBar;

    private final PortalFormPanel contentPanel;

    private final ApplicationWizardStep step;

    private final OnlineApplicationDTO entityPrototype;

    private final HTML errorMessageBar;

    public AbstractSectionPanel(int index, String caption, SummaryForm form, ApplicationWizardStep step) {
        super(VistaImages.INSTANCE);
        this.step = step;
        this.captionBar = new SectionCaptionBar(index, caption);
        this.contentPanel = new PortalFormPanel(form);
        this.entityPrototype = EntityFactory.getEntityPrototype(OnlineApplicationDTO.class);

        setStyleName(SummaryStepTheme.StyleName.SummaryStepSection.name());

        final FlowPanel mainPanel = new FlowPanel();
        mainPanel.setWidth("100%");
        setWidget(mainPanel);
        mainPanel.add(captionBar);

        final FlowPanel expandedBoxPanel = new FlowPanel();
        mainPanel.add(expandedBoxPanel);

        errorMessageBar = new HTML(
                i18n.tr("The information provided under this section is not complete, please fill in all required fields to continue with the application."));
        errorMessageBar.setStyleName(VistaTheme.StyleName.ErrorMessage.name());
        expandedBoxPanel.add(errorMessageBar);

        expandedBoxPanel.add(contentPanel);

        addToggleHandler(new ToggleHandler() {

            @Override
            public void onToggle(ToggleEvent event) {
                expandedBoxPanel.setVisible(event.isToggleOn());

            }
        });

        setExpended(false);
    }

    protected void addComponent(IObject<?> member, CComponent<?, ?, ?> component) {
        contentPanel.append(Location.Left, member, component);
    }

    protected void addField(IObject<?> member) {
        contentPanel.append(Location.Left, member).decorate();
    }

    protected void addField(IObject<?> member, String customLabel) {
        contentPanel.append(Location.Left, member).decorate().customLabel(customLabel);
    }

    protected void addField(IObject<?> member, CField<?, ?> field) {
        contentPanel.append(Location.Left, member, field).decorate();
    }

    protected void addCaption(String caption) {
        contentPanel.h4(caption);
    }

    public OnlineApplicationDTO proto() {
        return entityPrototype;
    }

    public void onValueSet() {
        captionBar.updateState();
        ValidationResults validationResults = step.getValidationResults();

        if (validationResults.isValid()) {
            contentPanel.setVisible(true);
            errorMessageBar.setVisible(false);
        } else {
            contentPanel.setVisible(false);
            errorMessageBar.setVisible(true);
        }
    }

    class SectionCaptionBar extends FlowPanel {

        private final Image indexLabel;

        public SectionCaptionBar(int index, String caption) {

            setStyleName(SummaryStepTheme.StyleName.SummaryStepSectionCaptionBar.name());

            Label captionLabel = new Label(caption);
            captionLabel.setStyleName(SummaryStepTheme.StyleName.SummaryStepSectionCaption.name());
            add(captionLabel);

            indexLabel = new Image(PortalImages.INSTANCE.messageSuccess());
            indexLabel.setVisible(false);
            indexLabel.addStyleName(SummaryStepTheme.StyleName.SummaryStepSectionStatus.name());
            add(indexLabel);

        }

        void updateState() {
            if (step.isStepComplete()) {
                indexLabel.setResource(PortalImages.INSTANCE.messageSuccess());
                indexLabel.setVisible(true);
            } else if (step.isStepVisited()) {
                indexLabel.setResource(PortalImages.INSTANCE.messageError());
                indexLabel.setVisible(true);
            } else {
                indexLabel.setVisible(false);
            }
        }

    }

}