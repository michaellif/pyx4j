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

    public static final String valueOf(String prefix, IStyleSuffix suffix, IStyleDependent dependent) {
        return "." + prefix + (suffix == null ? "" : (suffix.name())) + (dependent == null ? "" : ("-" + dependent.name()));
    }

    public static final String valueOf(String prefix, IStyleSuffix suffix) {
        return valueOf(prefix, suffix, null);
    }

    public static final String valueOf(String prefix) {
        return valueOf(prefix, null, null);
    }

    public static String valueOf(Enum<?> enumerator) {
        return valueOf(enumerator.name());
    }

    public static final String getStyleName(String pefix, IStyleSuffix suffix) {
        return pefix + (suffix == null ? "" : (suffix.name()));
    }

    public static final String getDependentName(IStyleDependent dependent) {
        return dependent == null ? "" : (dependent.name());
    }

}
