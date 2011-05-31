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
 * Created on May 31, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CButton;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.IAccessAdapter;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

public abstract class CEntityComponent<DATA_TYPE, WIDGET_TYPE extends Widget & INativeEditableComponent<DATA_TYPE>> extends
        CEditableComponent<DATA_TYPE, WIDGET_TYPE> implements IEditableComponentFactory, IAccessAdapter {

    private static I18n i18n = I18nFactory.getI18n(CEntityComponent.class);

    private CEntityComponent<?, ?> bindParent;

    @Override
    public boolean isValid() {

        if (!isEditable() || !isEnabled()) {
            return true;
        }

        if (this instanceof IComponentContainer) {
            for (CEditableComponent<?, ?> ccomponent : ((IComponentContainer) this).getComponents()) {
                if (!ccomponent.isValid()) {
                    return false;
                }
            }
        }
        return super.isValid();
    }

    public ValidationResults getContainerValidationResults() {
        ValidationResults validationResults = new ValidationResults();
        String message = getValidationMessage();
        if (message != null) {
            if (CommonsStringUtils.isStringSet(getTitle())) {
                validationResults.appendValidationError(i18n.tr("''{0}'' is not valid. {1}", getTitle(), message));
            } else {
                validationResults.appendValidationError(message);
            }
        }
        return validationResults;
    }

    public ValidationResults getAllValidationResults() {
        ValidationResults validationResults = getContainerValidationResults();
        if (this instanceof IComponentContainer) {
            for (CEditableComponent<?, ?> component : ((IComponentContainer) this).getComponents()) {
                if (component.isValid()) {
                    continue;
                }
                if (component instanceof IComponentContainer) {
                    validationResults.appendValidationErrors(((IComponentContainer) component).getValidationResults());
                } else if (component.isVisited()) {
                    validationResults.appendValidationError(i18n.tr("Field ''{0}'' is not valid. {1}", component.getTitle(), component.getValidationMessage()));
                }
            }
        }
        return validationResults;
    }

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        if (this instanceof IComponentContainer) {
            for (CEditableComponent<?, ?> ccomponent : ((IComponentContainer) this).getComponents()) {
                ((CEditableComponent<?, ?>) ccomponent).setVisited(visited);
            }
        }
    }

    @Override
    public boolean isEnabled(CComponent<?> component) {
        if (component instanceof CButton) {
            return isEditable() && isEnabled();
        } else {
            return isEnabled();
        }
    }

    @Override
    public boolean isEditable(CComponent<?> component) {
        return isEditable();
    }

    @Override
    public boolean isVisible(CComponent<?> component) {
        return isVisible();
    }

    @Override
    public boolean isVisited() {
        return true;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        assert (bindParent != null) : "Flex Component " + this.getClass().getName() + "is not bound";
        return bindParent.create(member);
    }

    public void onBound(CEntityComponent<?, ?> parent) {
        assert (bindParent == null) : "Flex Component " + this.getClass().getName() + " is already bound to " + bindParent;
        bindParent = parent;
    }

    public void addValidations() {

    }

    public abstract IsWidget createContent();

}
