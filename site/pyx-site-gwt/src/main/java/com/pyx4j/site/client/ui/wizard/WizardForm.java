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
 * Created on 2011-07-29
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.wizard;

import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.tabpanel.Tab;
import com.pyx4j.widgets.client.tabpanel.WizardPanel;

public abstract class WizardForm<E extends IEntity> extends CEntityForm<E> {

    private static final I18n i18n = I18n.get(WizardForm.class);

    private final WizardPanel wizardPanel;

    private final IWizardView<? extends IEntity> view;

    public WizardForm(Class<E> rootClass, final IWizardView<? extends IEntity> view) {
        super(rootClass);
        this.view = view;
        wizardPanel = new WizardPanel();
        wizardPanel.addSelectionHandler(new SelectionHandler<Tab>() {

            @Override
            public void onSelection(SelectionEvent<Tab> event) {
                onStepChange(event);
            }
        });
    }

    protected void onStepChange(SelectionEvent<Tab> event) {
        view.onStepChange();
    }

    @Override
    public IsWidget createContent() {
        wizardPanel.setSize("100%", "100%");
        return wizardPanel;
    }

    public Tab addStep(final FormFlexPanel panel) {
        final Tab tab = addStep(panel, panel.getTitle());
        panel.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid, PropertyName.repopulated, PropertyName.showErrorsUnconditional)) {
                    ValidationResults validationResults = panel.getValidationResults();
                    if (validationResults.isValid()) {
                        tab.setTabWarning(null);
                    } else {
                        tab.setTabWarning(validationResults.getValidationShortMessage());
                    }
                }
            }
        });
        return tab;
    }

    public Tab addStep(Widget content, String tabTitle) {
        Tab tab = null;
        ScrollPanel scroll = new ScrollPanel(content);
        tab = new Tab(scroll, tabTitle, null, false);
        wizardPanel.addTab(tab);
        return tab;
    }

    @Override
    public void onReset() {
        if (wizardPanel.getTabs().size() > 0) {
            wizardPanel.selectTab(0);
        }

        List<Tab> tabs = wizardPanel.getTabs();
        for (int i = 1; i < tabs.size(); i++) {
            tabs.get(i).setTabEnabled(false);
        }

        super.onReset();
    }

    public void previous() {
        int index = wizardPanel.getSelectedIndex();
        if (index > 0) {
            wizardPanel.selectTab(index - 1);
        }
    }

    public void next() {
        int index = wizardPanel.getSelectedIndex();
        if (index < wizardPanel.size() - 1) {
            wizardPanel.setTabEnabled(index + 1, true);
            wizardPanel.selectTab(index + 1);
        }

    }

    protected final void finish() {
        if (!isValid()) {
            setUnconditionalValidationErrorRendering(true);
            MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(true, true));
        } else {
            view.getPresenter().finish();
        }

    }

    public boolean isFirst() {
        return wizardPanel.getSelectedIndex() == 0;
    }

    public boolean isLast() {
        return wizardPanel.getSelectedIndex() == wizardPanel.size() - 1;
    }

    // decoration stuff:
    protected class DecoratorBuilder extends WidgetDecorator.Builder {

        public DecoratorBuilder(CComponent<?, ?> component) {
            super(component);
        }

        public DecoratorBuilder(CComponent<?, ?> component, double componentWidth) {
            super(component);
            componentWidth(componentWidth);
        }

        public DecoratorBuilder(CComponent<?, ?> component, double componentWidth, double labelWidth) {
            super(component);
            componentWidth(componentWidth);
            labelWidth(labelWidth);
        }

    }
}
