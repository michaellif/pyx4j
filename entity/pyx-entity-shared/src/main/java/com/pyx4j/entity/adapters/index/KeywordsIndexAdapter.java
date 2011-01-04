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
 * Created on Oct 1, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.adapters.index;

import java.util.HashSet;
import java.util.Set;

import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class KeywordsIndexAdapter extends AbstractIndexAdapter<String> {

    public static String KEYWORD_SPLIT_PATTERN = "[\\W,.;\\-&&[^*]]";

    public static char WILDCARD_CHAR = '*';

    @Override
    public Object getIndexedValue(IEntity entity, MemberMeta memberMeta, String value) {
        if (value == null) {
            return null;
        }
        return getIndexValueKeys(memberMeta.getAnnotation(Indexed.class).keywordLenght(), value);
    }

    public static Set<String> getIndexValueKeys(int keywordLenght, String string) {
        Set<String> set = new HashSet<String>();
        for (String word : string.split(KEYWORD_SPLIT_PATTERN)) {
            word = word.trim().toLowerCase();
            for (int i = 1; i <= Math.min(keywordLenght, word.length()); i++) {
                set.add(word.substring(0, i));
            }
        }
        return set;
    }

    @Override
    public Class<?> getIndexValueClass() {
        return String[].class;
    }
}
