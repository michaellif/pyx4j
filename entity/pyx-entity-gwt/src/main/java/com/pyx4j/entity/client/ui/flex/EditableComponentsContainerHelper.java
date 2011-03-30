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
 * Created on 2011-03-04
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CButton;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ContainerAccessAdapter;
import com.pyx4j.forms.client.ui.IAccessAdapter;
import com.pyx4j.forms.client.ui.ValidationResults;

/**
 * @see ContainerAccessAdapter
 * 
 */
public class EditableComponentsContainerHelper implements IAccessAdapter {

    private static I18n i18n = I18nFactory.getI18n(EditableComponentsContainerHelper.class);

    private final CEditableComponent<?, ?> container;

    public EditableComponentsContainerHelper(IComponentContainer container) {
        this.container = (CEditableComponent<?, ?>) container;
    }

    public boolean isValid() {
        for (CEditableComponent<?, ?> ccomponent : ((IComponentContainer) container).getComponents()) {
            if (!ccomponent.isValid()) {
                return false;
            }
        }
        return true;
    }

    public ValidationResults getContainerValidationResults() {
        ValidationResults validationResults = new ValidationResults();
        String message = container.getValidationMessage();
        if (message != null) {
            if (CommonsStringUtils.isStringSet(container.getTitle())) {
                validationResults.appendValidationError(i18n.tr("''{0}'' is not valid. {1}", container.getTitle(), message));
            } else {
                validationResults.appendValidationError(message);
            }
        }
        return validationResults;
    }

    public ValidationResults getAllValidationResults() {
        ValidationResults validationResults = getContainerValidationResults();
        for (CEditableComponent<?, ?> component : ((IComponentContainer) container).getComponents()) {
            if (component.isValid()) {
                continue;
            }
            if (component instanceof IComponentContainer) {
                validationResults.appendValidationErrors(((IComponentContainer) component).getValidationResults());
            } else if (component.isVisited()) {
                validationResults.appendValidationError(i18n.tr("Field ''{0}'' is not valid. {1}", component.getTitle(), component.getValidationMessage()));
            }
        }
        return validationResults;
    }

    public void setVisited(boolean visited) {
        for (CEditableComponent<?, ?> ccomponent : ((IComponentContainer) container).getComponents()) {
            ((CEditableComponent<?, ?>) ccomponent).setVisited(visited);
        }
    }

    @Override
    public boolean isEnabled(CComponent<?> component) {
        if (component instanceof CButton) {
            return container.isEditable() && container.isEnabled();
        } else {
            return container.isEnabled();
        }
    }

    @Override
    public boolean isEditable(CComponent<?> component) {
        return container.isEditable();
    }

    @Override
    public boolean isVisible(CComponent<?> component) {
        return container.isVisible();
    }
}
