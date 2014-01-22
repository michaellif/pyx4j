/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import static com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderBoxItem;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.EntityContainerDecoratorToolbar;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.CollapsablePanel;
import com.pyx4j.widgets.client.event.shared.ToggleEvent;
import com.pyx4j.widgets.client.event.shared.ToggleHandler;
import com.pyx4j.widgets.client.images.WidgetsImages;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class SummaryStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(SummaryStep.class);

    private SummaryPanel panel;

    public SummaryStep() {
        super(OnlineApplicationWizardStepMeta.Summary);
    }

    @Override
    public Widget createStepContent() {
        panel = new SummaryPanel();
        panel.initContent();
        return panel.asWidget();
    }

    @Override
    public void setStepSelected(boolean selected) {
        super.setStepSelected(selected);
        if (selected) {
            panel.setValue(getValue());
        } else {
            panel.reset();
        }
    }

    class SummaryPanel extends CEntityForm<OnlineApplicationDTO> {

        public SummaryPanel() {
            super(OnlineApplicationDTO.class);
            setViewable(true);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel contentPanel = new BasicFlexFormPanel();

            int row = -1;

            SectionPanel unitPanel = new SectionPanel(OnlineApplicationWizardStepMeta.Unit.toString());

            unitPanel.setWidget(new FormWidgetDecoratorBuilder(inject(proto().unit())).build());

            contentPanel.setWidget(++row, 0, unitPanel);

            return contentPanel;
        }

    }

    class SectionPanel extends CollapsablePanel {

        private final SectionCaptionBar captionBar;

        private final BasicFlexFormPanel contentPanel;

        private int row = -1;

        public SectionPanel(String caption) {
            super(VistaImages.INSTANCE);

            captionBar = new SectionCaptionBar(caption);
            contentPanel = new BasicFlexFormPanel();

            FlowPanel mainPanel = new FlowPanel();
            mainPanel.setWidth("100%");
            setWidget(mainPanel);
            mainPanel.add(captionBar);
            mainPanel.add(contentPanel);

            addToggleHandler(new ToggleHandler() {

                @Override
                public void onToggle(ToggleEvent event) {
                    contentPanel.setVisible(event.isToggleOn());
                    captionBar.update(event.isToggleOn());
                }
            });

        }

        protected void addField(IObject<?> member) {
            contentPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(member)).build());
        }
    }

    class SectionCaptionBar extends Label {

        public SectionCaptionBar(String caption) {
            super(caption);
        }

        public void update(boolean toggleOn) {
        }

    }

}
