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
 * Created on 2011-04-19
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.selenium;

import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.site.rpc.AppPlaceInfo;

/**
 * IDebugId builder.
 * 
 * Creates DebugId using CompositeDebugId the same way as used during UI Forms construction.
 * 
 * Example:
 * 
 * <pre>
 * 
 * < selenium.type("UnitSelection$selectionCriteria$availableFrom", strFrom);
 * > selenium.type(D.id(meta(UnitSelection.class).selectionCriteria().availableFrom()), strFrom);
 *  
 *  
 * < selenium.click("UnitSelection$availableUnits$units-row-2-Unit$unitType");
 * > selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(),  2, proto(Unit.class).unitType()));
 * 
 * </pre>
 * 
 */
public class D {

    public static IDebugId id(IList<?> formFolder, int itemNumber) {
        return new CompositeDebugId(formFolder.getPath(), "row", itemNumber);
    }

    public static IDebugId id(IList<?> formFolder, int itemNumber, IObject<?> member) {
        return new CompositeDebugId(id(formFolder, itemNumber), member.getPath());
    }

    public static IDebugId id(IList<?> formFolder, int itemNumber, String itemDebugId) {
        return new CompositeDebugId(id(formFolder, itemNumber), itemDebugId);
    }

    public static IDebugId id(IList<?> formFolder, IDebugId child) {
        return new CompositeDebugId(formFolder.getPath(), child);
    }

    public static IDebugId id(IDebugId parent, IDebugId child) {
        return new CompositeDebugId(parent, child);
    }

    public static IDebugId id(IDebugId parent, IObject<?> member) {
        return new CompositeDebugId(parent, member.getPath());
    }

    public static IDebugId id(IDebugId parent, Class<? extends Place> placeClass) {
        return new CompositeDebugId(parent, AppPlaceInfo.getPlaceIDebugId(placeClass));
    }

    public static IDebugId id(IDebugId parent, Class<? extends Place> placeClass, int itemNumber) {
        return new CompositeDebugId(parent, AppPlaceInfo.getPlaceIDebugId(placeClass), itemNumber);
    }

    public static IDebugId id(IDebugId parent, String child) {
        return new CompositeDebugId(parent, child);
    }

    public static IDebugId id(String parent, IDebugId child) {
        return new CompositeDebugId(parent, child);
    }

    public static IDebugId id(IObject<?> member) {
        return member.getPath();
    }

    public static IDebugId id(IEntity form, IObject<?> member) {
        if (form == null) {
            return member.getPath();
        } else {
            return new CompositeDebugId(form.getPath(), member.getPath());
        }
    }
}
