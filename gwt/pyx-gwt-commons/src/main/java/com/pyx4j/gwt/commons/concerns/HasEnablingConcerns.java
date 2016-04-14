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

import com.google.gwt.user.client.ui.HasEnabled;

public interface HasEnablingConcerns extends HasConcerns, HasEnabled {

    void applyEnablingRules();

    @Override
    default boolean isEnabled() {
        return EnablingConcern.isEnabled(concerns());
    }

    @Override
    default void setEnabled(boolean enabled) {
        setConcernsEnabled(enabled);
    }

    default void setConcernsEnabled(boolean enabled) {
        ExplicitEnablingConcern explicit = null;
        for (AbstractConcern concern : concerns()) {
            if (concern instanceof ExplicitEnablingConcern) {
                explicit = (ExplicitEnablingConcern) concern;
            }
        }
        if (explicit == null) {
            concerns().add(explicit = new ExplicitEnablingConcern());
        }
        explicit.setEnabled(enabled);
        applyEnablingRules();
    }

    /**
     * Component will become Enabled when function returns true.
     *
     * Multiple concerns:
     * - all concerns of the same type should return true for component to become Enabled.
     * - if any of concerns returns false the component will not become Enabled.
     */
    default void enabled(EnablingConcern concern, String... debuggingAdapterName) {
        // TODO Wrapper with 'adapterName' to simplify debug
        concerns().add(concern);
        applyEnablingRules();
    }

}
