/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Feb 13, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.validators;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.i18n.shared.I18n;

@SuppressWarnings("rawtypes")
public class EntityContainerValidator extends AbstractComponentValidator {

    private static I18n i18n = I18n.get(EntityContainerValidationError.class);

    public EntityContainerValidator() {

    }

    @Override
    public ValidationError isValid() {
        if (!(getComponent() instanceof CEntityContainer)) {
            throw new Error("EntityContainerValidator can be added only to CEntityContainer");
        }
        CEntityContainer<?> container = (CEntityContainer<?>) getComponent();
        if (container.getComponents() != null) {
            for (CComponent<?> ccomponent : container.getComponents()) {
                if (!ccomponent.isValid()) {
                    return new EntityContainerValidationError(container, i18n.tr("Form is not valid."));
                }
            }
        }
        return null;
    }
}
