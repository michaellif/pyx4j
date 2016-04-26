/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 22, 2016
 * @author vlads
 */
package com.pyx4j.entity.ognl;

import com.pyx4j.entity.core.Path;

public class OGNLPathToVariableName implements PathToVariableName {

    private final String prefix;

    private final String sufix;

    public OGNLPathToVariableName() {
        this("${model.", "}");
    }

    public OGNLPathToVariableName(String prefix, String sufix) {
        this.prefix = prefix;
        this.sufix = sufix;
    }

    @Override
    public String pathToVarname(Path memberPath) {
        String varName = memberPath.toString().replace(Path.PATH_SEPARATOR, '.');

        varName = varName.replace(".[].", "[0].");

        // and remove the Root Object name
        varName = varName.substring(varName.indexOf('.') + 1);

        if (varName.endsWith(".")) {
            varName = varName.substring(0, varName.length() - 1);
        }

        return prefix + varName + sufix;

    }

}