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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public class WizardDecorator<E extends IEntity> extends VerticalPanel implements IDecorator<CEntityWizard<E>> {

    private static final I18n i18n = I18n.get(WizardDecorator.class);

    private final FlowPanel header;

    private final Label captionLabel;

    private final SimplePanel contentHolder;

    private final Toolbar footerToolbar;

    private final SimplePanel footer;

    private final Button btnPrevious;

    private final Button btnNext;

    private String endButtonCaption = i18n.tr("Finish");

    private CEntityWizard<E> component;

    private String footerHeight = "auto";

    public WizardDecorator() {
        captionLabel = new Label();
        captionLabel.setStyleName(CEntityWizardTheme.StyleName.HeaderCaption.name());

        header = new FlowPanel();
        header.add(captionLabel);
        header.setStyleName(CEntityWizardTheme.StyleName.Header.name());
        add(header);

        add(contentHolder = new SimplePanel());

        footerToolbar = new Toolbar();
        footer = new SimplePanel();
        footer.setStyleName(CEntityWizardTheme.StyleName.FooterToolbar.name());
        footer.setWidget(footerToolbar);
        add(footer);

        Anchor btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                onCancel();
            }
        });
        addFooterItem(btnCancel);

        btnPrevious = new Button(i18n.tr("Previous"), new Command() {
            @Override
            public void execute() {
                component.previous();
                calculateButtonsState();
            }
        });
        addFooterItem(btnPrevious);

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
        addFooterItem(btnNext);

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
        return contentHolder.getWidget();
    }

    protected void setContent(IsWidget widget) {
        contentHolder.clear();
        contentHolder.setWidget(widget);
    }

    public void setCaption(String caption) {
        captionLabel.setText(caption);
    }

    public String getCaption() {
        return captionLabel.getText();
    }

    public void addFooterItem(Widget widget) {
        footer.setHeight(footerHeight);
        footerToolbar.add(widget);
    }

    public void setFooterHeight(String footerToolbarHeight) {
        this.footerHeight = footerToolbarHeight;
        if (footerToolbar.getWidgetCount() == 0) {
            footer.setHeight(footerToolbarHeight);
        }
    }

    public void setEndButtonCaption(String endButtonCaption) {
        this.endButtonCaption = endButtonCaption;
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
