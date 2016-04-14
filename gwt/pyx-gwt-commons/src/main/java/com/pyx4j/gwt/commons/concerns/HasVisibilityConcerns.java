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

import com.google.gwt.user.client.ui.HasVisibility;

public interface HasVisibilityConcerns extends HasConcerns, HasVisibility {

    void applyVisibilityRules();

    @Override
    default boolean isVisible() {
        return VisibilityConcern.isVisible(concerns());
    }

    @Override
    default void setVisible(boolean visible) {
        setConcernsVisible(visible);
    }

    default void setConcernsVisible(boolean visible) {
        ExplicitVisibilityConcern explicit = null;
        for (AbstractConcern concern : concerns()) {
            if (concern instanceof ExplicitVisibilityConcern) {
                explicit = (ExplicitVisibilityConcern) concern;
            }
        }
        if (explicit == null) {
            concerns().add(explicit = new ExplicitVisibilityConcern());
        }
        explicit.setVisible(visible);
        applyVisibilityRules();
    }

    /**
     * Component will become Visible when function returns true.
     *
     * Multiple concerns:
     * - all concerns of the same type should return true for component to become Visible.
     * - if any of concerns returns false the component will not become Visible.
     */
    default void visible(VisibilityConcern concern, String... debuggingAdapterName) {
        // TODO Wrapper with 'adapterName' to simplify debug
        concerns().add(concern);
        applyVisibilityRules();
    }

}
