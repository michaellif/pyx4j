/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 13, 2016
 * @author vlads
 */
package com.pyx4j.gwt.commons.concerns;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.security.shared.AccessControlContext;

public interface HasSecureConcernedChildren extends HasSecureConcern {

    class SecureConcernsHolder {

        private final Collection<HasSecureConcern> secureConcerns = new ArrayList<>();

        private AbstractConcern parentConcern = null;
    }

    SecureConcernsHolder secureConcernsHolder();

    default Collection<HasSecureConcern> secureConcerns() {
        return secureConcernsHolder().secureConcerns;
    }

    @Override
    default void setSecurityContext(AccessControlContext context) {
        for (HasSecureConcern sc : secureConcerns()) {
            sc.setSecurityContext(context);
        }
    }

    default void applyConcernRules() {
        for (HasSecureConcern concern : secureConcerns()) {
            if (concern instanceof HasConcerns) {
                ((HasConcerns) concern).applyConcernRules();
            }
        }
    }

    @Override
    default void inserConcernedParent(AbstractConcern parentConcern) {
        for (HasSecureConcern concernedChild : secureConcerns()) {
            concernedChild.inserConcernedParent(parentConcern);
        }
        secureConcernsHolder().parentConcern = parentConcern;
    }

    default void addSecureConcern(HasSecureConcern secureConcern) {
        if (secureConcernsHolder().parentConcern != null) {
            secureConcern.inserConcernedParent(secureConcernsHolder().parentConcern);
        }
        secureConcerns().add(secureConcern);
    }

    default void addWidgetSecureConcern(IsWidget widget) {
        if (widget instanceof HasSecureConcern) {
            addSecureConcern((HasSecureConcern) widget);
        }
    }

    default void clearSecureConcerns() {
        secureConcerns().clear();
    }

}
