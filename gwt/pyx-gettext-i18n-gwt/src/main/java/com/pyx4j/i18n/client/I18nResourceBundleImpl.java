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
 * Created on Jun 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.client;

import java.util.HashMap;
import java.util.Map;

public class I18nResourceBundleImpl extends I18nResourceBundle {

    Map<String, String> map = new HashMap<String, String>();

    public I18nResourceBundleImpl(String[] values) {
        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }
    }

    @Override
    public String getString(String key) {
        return map.get(key);
    }

}
