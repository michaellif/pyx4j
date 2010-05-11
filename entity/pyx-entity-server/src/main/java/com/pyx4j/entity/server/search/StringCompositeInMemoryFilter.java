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
 * Created on 2010-05-11
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.search;

import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.IndexString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;

public class StringCompositeInMemoryFilter extends InMemoryFilter {

    private static final Logger log = LoggerFactory.getLogger(StringCompositeInMemoryFilter.class);

    protected List<Pattern> patterns = new Vector<Pattern>();

    public StringCompositeInMemoryFilter(Path propertyPath, List<String> words) {
        super(propertyPath);
        for (String word : words) {
            StringBuilder rexExpr = new StringBuilder();
            if (word.indexOf(IndexString.WILDCARD_CHAR) != -1) {
                rexExpr.append(word.replace("*", ".*?"));
                rexExpr.append("\\b.*");
            } else {
                rexExpr.append(word);
                rexExpr.append(".*\\b.*");
            }
            log.debug("regular expression {}", rexExpr);
            patterns.add(Pattern.compile(rexExpr.toString()));
        }
    }

    @Override
    protected boolean accept(IEntity entity) {
        String value = (String) entity.getValue(propertyPath);
        if (value == null) {
            return false;
        }
        int matchePositions = 0;
        int matchePositionsCount = 0;
        String[] words = value.toLowerCase().split(IndexString.KEYWORD_SPLIT_PATTERN);
        for (Pattern pattern : patterns) {
            boolean match = false;
            int positionMask = 1;
            for (String word : words) {
                if (pattern.matcher(word).matches()) {
                    match = true;
                    if ((matchePositions & positionMask) == 0) {
                        matchePositions |= positionMask;
                        matchePositionsCount++;
                    }
                }
                positionMask <<= 1;
            }
            if (!match) {
                return false;
            }
        }
        return (matchePositionsCount == patterns.size());
    }

}
