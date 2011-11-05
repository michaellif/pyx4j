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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CButton;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.forms.client.ui.IAccessAdapter;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.i18n.shared.I18n;

public abstract class CEntity<E extends IObject<?>> extends CContainer<E, NativeEntityPanel<E>> implements IEditableComponentFactory, IAccessAdapter {

    protected static I18n i18n = I18n.get(CEntity.class);

    private IDecorator decorator;

    private ImageResource icon;

    @Override
    public boolean isValid() {

        if (!isEditable() || !isEnabled()) {
            return true;
        }

        if (getComponents() != null) {
            for (CComponent<?, ?> ccomponent : getComponents()) {
                if (!ccomponent.isValid()) {
                    return false;
                }
            }
        }
        return super.isValid();
    }

    public ValidationResults getAllValidationResults() {
        ValidationResults validationResults = getContainerValidationResults();
        for (CComponent<?, ?> component : this.getComponents()) {
            if (component.isValid()) {
                continue;
            }
            if (component instanceof CEntity<?>) {
                validationResults.appendValidationErrors(((CEntity<?>) component).getValidationResults());
            } else if (component.isVisited()) {
                validationResults.appendValidationError(i18n.tr("Field ''{0}'' is not valid. {1}", component.getTitle(), component.getValidationMessage()));
            }
        }

        return validationResults;
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

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        if (getComponents() != null) {
            for (CComponent<?, ?> ccomponent : getComponents()) {
                ((CComponent<?, ?>) ccomponent).setVisited(visited);
            }
        }
    }

    public abstract IsWidget createContent();

    protected IDecorator<?> createDecorator() {
        return null;
    }

    public Panel getContainer() {
        return asWidget();
    }

    @Override
    protected NativeEntityPanel<E> createWidget() {
        return new NativeEntityPanel<E>();
    }

    public void initContent() {
        decorator = createDecorator();
        if (decorator == null) {
            asWidget().setWidget(createContent());
        } else {
            asWidget().setWidget(decorator);
            decorator.setComponent(this);
        }

        addValidations();
    }

    public IDecorator<?> getDecorator() {
        return decorator;
    }

    public void setIcon(ImageResource icon) {
        this.icon = icon;
    }

    public ImageResource getIcon() {
        return icon;
    }

    @Override
    public boolean isEnabled(CComponent<?, ?> component) {
        if (component instanceof CButton) {
            return isEditable() && isEnabled();
        } else {
            return isEnabled();
        }
    }

    @Override
    public boolean isEditable(CComponent<?, ?> component) {
        return isEditable();
    }

    @Override
    public boolean isVisible(CComponent<?, ?> component) {
        return isVisible();
    }

    @Override
    public boolean isVisited() {
        return true;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        assert (getParent() != null) : "Flex Component " + this.getClass().getName() + "is not bound";
        return ((CEntity<?>) getParent()).create(member);
    }

    public void addValidations() {

    }
}
