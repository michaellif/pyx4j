/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Nov 12, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

public class Selector {

    private final String value;

    private Selector(String discriminator, IStyleName name, IStyleDependent dependent, boolean hover, boolean active, boolean focus) {
        value = (discriminator == null ? "." : (discriminator + " .")) + (name == null ? "" : (name.name()))
                + (dependent == null ? "" : ("-" + dependent.name())) + (hover ? ":hover" : "") + (active ? ":active" : "") + (focus ? ":focus" : "");
    }

    public static class Builder {

        private final IStyleName name;

        private String discriminator;

        private IStyleDependent dependent;

        private boolean hover = false;

        private boolean active = false;

        private boolean focus = false;

        public Builder(IStyleName name) {
            this.name = name;
        }

        public Builder discriminator(String discriminator) {
            this.discriminator = discriminator;
            return this;
        }

        public Builder dependent(IStyleDependent dependent) {
            this.dependent = dependent;
            return this;
        }

        public Builder hover() {
            this.hover = true;
            return this;
        }

        public Builder active() {
            this.active = true;
            return this;
        }

        public Builder focus() {
            this.focus = true;
            return this;
        }

        public Selector build() {
            return new Selector(discriminator, name, dependent, hover, active, focus);
        }
    }

    public Selector(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Deprecated
    public static final String valueOf(String prefix, IStyleName suffix, IStyleDependent dependent) {
        return "." + prefix + (suffix == null ? "" : (suffix.name())) + (dependent == null ? "" : ("-" + dependent.name()));
    }

    @Deprecated
    public static final String valueOf(String prefix, IStyleName suffix) {
        return valueOf(prefix, suffix, null);
    }

    @Deprecated
    public static final String valueOf(String prefix) {
        return valueOf(prefix, null, null);
    }

    @Deprecated
    public static String valueOf(Enum<?> enumerator) {
        return valueOf(enumerator.name());
    }

    @Deprecated
    public static final String getStyleName(String pefix, IStyleName suffix) {
        return pefix + (suffix == null ? "" : (suffix.name()));
    }

    @Deprecated
    public static final String getDependentName(IStyleDependent dependent) {
        return dependent == null ? "" : (dependent.name());
    }

}
