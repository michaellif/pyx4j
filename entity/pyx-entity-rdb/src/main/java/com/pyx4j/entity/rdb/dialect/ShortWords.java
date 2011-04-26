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
 * Created on 2011-04-26
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.dialect;

import java.util.HashMap;
import java.util.Map;

public class ShortWords {

    private static Map<String, String> words = new HashMap<String, String>();

    public ShortWords() {

    }

    public void add(String word, String shortForm) {
        words.put(word, shortForm);
    }

    public String getShortForm(String word) {
        String shortForm = words.get(word);
        if (shortForm != null) {
            return shortForm;
        } else {
            return word;
        }
    }
}
