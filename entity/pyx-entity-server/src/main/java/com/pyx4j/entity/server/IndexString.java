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
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class IndexString {

    public static String KEYWORD_SPLIT_PATTERN = "[\\W,.;\\-&&[^*]]";

    public static char WILDCARD_CHAR = '*';

    public static Set<String> getIndexKeys(int keywordLenght, String searchCriteria) {
        Set<String> set = new HashSet<String>();
        for (String word : searchCriteria.split(KEYWORD_SPLIT_PATTERN)) {
            word = word.trim().toLowerCase();
            for (int i = 1; i <= Math.min(keywordLenght, word.length()); i++) {
                set.add(word.substring(0, i));
            }
        }
        return set;
    }

    public static Set<String> getIndexValues(int keywordLenght, String searchCriteria) {
        Set<String> set = new HashSet<String>();
        for (String word : searchCriteria.split(KEYWORD_SPLIT_PATTERN)) {
            word = word.toLowerCase().trim();
            int wc = word.indexOf(WILDCARD_CHAR);
            if (wc == 0) {
                // Starts from Wildcard character = can't use index
                continue;
            } else if (wc >= 1) {
                word = word.substring(0, wc);
            }
            if (word.length() == 0) {
                continue;
            } else if (word.length() > keywordLenght) {
                set.add(word.substring(0, keywordLenght));
            } else {
                set.add(word);
            }
        }
        return set;
    }

    public static List<String> splitIndexValues(String searchCriteria) {
        List<String> list = new Vector<String>();
        for (String word : searchCriteria.split(KEYWORD_SPLIT_PATTERN)) {
            word = word.trim();
            if (word.length() > 0) {
                list.add(word);
            }
        }
        return list;
    }
}
