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
 * Created on Sep 12, 2011
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import com.google.gwt.place.shared.Place;

public interface IMemento {

    // Control:
    void setCurrentPlace(Place place);

    Place getCurrentPlace();

    boolean mayRestore();

    // Data manipulation:

    boolean isEmpty();

    void clear();

    String[] getAttributeKeys();

    // Abstract:
    Object getObject(String key);

    void putObject(String key, Object value);

    // Specialisations: 
    Boolean getBoolean(String key);

    void putBoolean(String key, boolean value);

    Integer getInteger(String key);

    void putInteger(String key, int value);

    Float getFloat(String key);

    void putFloat(String key, float value);

    String getString(String key);

    void putString(String key, String value);
}
