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
 * Created on Feb 25, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.util.HashSet;
import java.util.Set;

public class IndexString {

    public static Set<String> getIndexValues(int keywordLenght, String searchCriteria) {
        Set<String> set = new HashSet<String>();
        for (String word : searchCriteria.split("[\\W,.-]")) {
            word = word.toLowerCase();
            if (word.length() > keywordLenght) {
                set.add(word.substring(0, keywordLenght));
            } else {
                set.add(word);
            }
        }
        return set;
    }
}
