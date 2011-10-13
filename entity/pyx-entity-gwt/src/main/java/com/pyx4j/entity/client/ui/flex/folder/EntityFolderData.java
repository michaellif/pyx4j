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
 * Created on Oct 9, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex.folder;

public class EntityFolderData {

    enum Multiplicity {
        zeroOrMore, oneOrMore
    }

    //if false Collape/Expand are hidden
    public boolean collapsible = true;

    public boolean collapsed = true;

    // if false Up/Down buttons
    public boolean orderable = true;

    //First item can be ordered or deleted
    public boolean firstFixed = true;

    // if false Add/Remove/Up/Down are hidden
    public boolean editable = true;

    public Multiplicity multiplicity = Multiplicity.zeroOrMore;

}
