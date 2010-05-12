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
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.IndexString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;

public class StringCompositeOrderedInMemoryFilter extends InMemoryFilter {

    private static final Logger log = LoggerFactory.getLogger(StringCompositeOrderedInMemoryFilter.class);

    protected Pattern pattern;

    public StringCompositeOrderedInMemoryFilter(Path propertyPath, List<String> words) {
        super(propertyPath);
        StringBuilder rexExpr = new StringBuilder();
        for (String word : words) {
            int wc = word.indexOf(IndexString.WILDCARD_CHAR);
            if ((wc != 0) && (rexExpr.length() == 0)) {
                rexExpr.append(".*");
            }
            if (wc != -1) {
                rexExpr.append(word.replace("*", ".*?"));
                rexExpr.append("\\b.*");
            } else {
                rexExpr.append(word);
                rexExpr.append(".*\\b.*");
            }
        }
        log.debug("regular expression {}", rexExpr);
        pattern = Pattern.compile(rexExpr.toString());
    }

    @Override
    protected boolean accept(IEntity entity) {
        String value = (String) entity.getValue(propertyPath);
        if (value == null) {
            return false;
        }
        return pattern.matcher(value.toLowerCase()).matches();
    }

}
