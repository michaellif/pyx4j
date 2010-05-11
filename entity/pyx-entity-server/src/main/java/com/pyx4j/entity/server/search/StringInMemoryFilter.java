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
 * Created on Feb 28, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.search;

import java.util.regex.Pattern;

import com.pyx4j.entity.server.IndexString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;

public class StringInMemoryFilter extends InMemoryFilter {

    protected final String wordStart;

    protected final Pattern pattern;

    public StringInMemoryFilter(Path propertyPath, String word) {
        super(propertyPath);
        word = word.toLowerCase();
        int wc = word.indexOf(IndexString.WILDCARD_CHAR);
        if (wc == 0) {
            // Starts from Wildcard character = can't use startsWith
            wordStart = null;
            pattern = Pattern.compile(word.replace("*", ".*?") + "\\b.*");
        } else if (wc >= 1) {
            wordStart = word.substring(0, wc);
            pattern = Pattern.compile(word.replace("*", ".*?") + "\\b.*");
        } else {
            wordStart = word;
            pattern = null;
        }
    }

    @Override
    protected boolean accept(IEntity entity) {
        String value = (String) entity.getValue(propertyPath);
        if (value == null) {
            return false;
        }
        for (String word : value.toLowerCase().split(IndexString.KEYWORD_SPLIT_PATTERN)) {
            if (wordStart != null) {
                if (word.startsWith(wordStart)) {
                    if (pattern == null) {
                        return true;
                    } else {
                        return pattern.matcher(word).matches();
                    }
                }
            } else if (pattern.matcher(word).matches()) {
                return true;
            }
        }
        return false;
    }
}
