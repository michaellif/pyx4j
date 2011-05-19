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
 * Created on Feb 4, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.rpc;

import java.util.Collections;
import java.util.Map;

import com.google.gwt.place.shared.Place;

public class AppPlace extends Place {

    private Map<String, String> args;

    public AppPlace() {
        args = Collections.emptyMap();
    }

    public void setArgs(Map<String, String> args) {
        this.args = Collections.unmodifiableMap(args);
    }

    public Map<String, String> getArgs() {
        return args;
    }

    @Override
    public boolean equals(Object other) {
        if (getClass() == other.getClass()) {
            if (args == null && ((AppPlace) other).getArgs() == null) {
                return true;
            } else if (args != null && args.equals(((AppPlace) other).getArgs())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        if (args != null) {
            hash = hash * 31 + args.hashCode();
        }
        return hash;
    }
}
