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
 * Created on 2011-02-07
 * @author antonk
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.commons;

public class CompositeDebugId implements IDebugId {

    private final IDebugId parent;

    private final IDebugId child;

    public CompositeDebugId(IDebugId parent, IDebugId child) {
        this.parent = parent;
        this.child = child;
    }

    public CompositeDebugId(IDebugId parent, String child) {
        this(parent, new StringDebugId(child));
    }

    public CompositeDebugId(IDebugId parent, String child, int itemNumber) {
        this(parent, new StringDebugId(child + "-" + itemNumber));
    }

    public CompositeDebugId(String parent, IDebugId child) {
        this(new StringDebugId(parent), child);
    }

    @Override
    public String debugId() {
        return (parent != null ? parent.debugId() + "-" : "") + (child != null ? child.debugId() : "unknown");
    }

    public static String debugId(IDebugId parent, IDebugId child) {
        return new CompositeDebugId(parent, child).debugId();
    }

    public static String debugId(IDebugId parent, String child) {
        return new CompositeDebugId(parent, child).debugId();
    }

    public static String debugId(String parent, IDebugId child) {
        return new CompositeDebugId(parent, child).debugId();
    }

    public static String debugId(IDebugId parent, IDebugId child, int itemNumber) {
        return new CompositeDebugId(parent, child).debugId() + "-" + itemNumber;
    }
}
