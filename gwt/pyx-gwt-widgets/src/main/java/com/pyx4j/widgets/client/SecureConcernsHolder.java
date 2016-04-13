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
 * Created on Jul 22, 2014
 * @author vlads
 */
package com.pyx4j.widgets.client;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.gwt.commons.concerns.AbstractConcern;
import com.pyx4j.gwt.commons.concerns.HasSecureConcern;
import com.pyx4j.security.shared.AccessControlContext;

public class SecureConcernsHolder implements HasSecureConcern {

    private final Collection<HasSecureConcern> secureConcerns = new ArrayList<>();

    private AbstractConcern parentConcern = null;

    public void add(IsWidget widget) {
        if (widget instanceof HasSecureConcern) {
            addSecureConcern((HasSecureConcern) widget);
        }
    }

    public void addSecureConcern(HasSecureConcern secureConcern) {
        if (parentConcern != null) {
            secureConcern.inserConcernedParent(parentConcern);
        }
        secureConcerns.add(secureConcern);
    }

    public void addAll(Collection<HasSecureConcern> secureConcerns) {
        for (HasSecureConcern c : secureConcerns) {
            addSecureConcern(c);
        }
    }

    @Override
    public void inserConcernedParent(AbstractConcern parentConcern) {
        HasSecureConcern.super.inserConcernedParent(parentConcern);
        this.parentConcern = parentConcern;
    }

    @Override
    public void setSecurityContext(AccessControlContext context) {
        for (HasSecureConcern sc : secureConcerns) {
            sc.setSecurityContext(context);
        }
    }

    public void clear() {
        secureConcerns.clear();
    }

}
