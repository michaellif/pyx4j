/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jul 25, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.wizard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public class WizardDecorator<E extends IEntity> extends FlowPanel implements IDecorator<CEntityWizard<E>> {

    private static final I18n i18n = I18n.get(WizardDecorator.class);

    private final FlowPanel headerPanel;

    private final Label captionLabel;

    private final SimplePanel mainPanel;

    private final Toolbar footerToolbar;

    private final SimplePanel footerPanel;

    private final Button btnPrevious;

    private final Button btnNext;

    private final Button btnCancel;

    private String endButtonCaption;

    private CEntityWizard<E> component;

    public WizardDecorator() {
        this(i18n.tr("Finish"));
    }

    public WizardDecorator(String endButtonCaption) {
        this.endButtonCaption = endButtonCaption;
        captionLabel = new Label();
        captionLabel.setStyleName(CEntityWizardTheme.StyleName.WizardHeaderCaption.name());

        headerPanel = new FlowPanel();
        headerPanel.add(captionLabel);
        headerPanel.setStyleName(CEntityWizardTheme.StyleName.WizardHeader.name());
        add(headerPanel);

        add(mainPanel = new SimplePanel());
        mainPanel.setStyleName(CEntityWizardTheme.StyleName.WizardMain.name());

        footerToolbar = new Toolbar();
        footerPanel = new SimplePanel();
        footerPanel.setStyleName(CEntityWizardTheme.StyleName.WizardFooter.name());
        footerPanel.setWidget(footerToolbar);
        add(footerPanel);

        btnCancel = new Button(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                onCancel();
            }
        });
        footerToolbar.add(btnCancel);

        btnPrevious = new Button(i18n.tr("Previous"), new Command() {
            @Override
            public void execute() {
                component.previous();
                calculateButtonsState();
            }
        });
        footerToolbar.add(btnPrevious);

        btnNext = new Button(i18n.tr("Next"), new Command() {
            @Override
            public void execute() {
                if (component.isLast()) {
                    onFinish();
                } else {
                    component.next();
                    calculateButtonsState();
                }
            }
        });
        footerToolbar.add(btnNext);

        setWidth("100%");
    }

    @Override
    public void setComponent(CEntityWizard<E> component) {
        assert this.component == null;
        this.component = component;
        setContent(component.createContent());
    }

    public CEntityWizard<E> getComponent() {
        return component;
    }

    protected void onCancel() {
    }

    protected void onFinish() {
    }

    protected IsWidget getContent() {
        return mainPanel.getWidget();
    }

    protected void setContent(IsWidget widget) {
        mainPanel.clear();
        mainPanel.setWidget(widget);
    }

    public void setCaption(String caption) {
        captionLabel.setText(caption);
    }

    public String getCaption() {
        return captionLabel.getText();
    }

    public Panel getHeaderPanel() {
        return headerPanel;
    }

    public Panel getMainPanel() {
        return mainPanel;
    }

    public Panel getFooterPanel() {
        return footerPanel;
    }

    public Button getBtnPrevious() {
        return btnPrevious;
    }

    public Button getBtnNext() {
        return btnNext;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public void calculateButtonsState() {
        if (component.isLast()) {
            btnNext.setCaption(endButtonCaption);
        } else {
            btnNext.setCaption(i18n.tr("Next"));
        }

        btnPrevious.setEnabled(!component.isFirst());
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {

    }

}
