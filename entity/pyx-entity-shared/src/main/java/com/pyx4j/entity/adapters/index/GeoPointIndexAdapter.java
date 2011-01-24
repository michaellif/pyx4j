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

import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.geo.GeoCell;
import com.pyx4j.geo.GeoPoint;

public class GeoPointIndexAdapter extends AbstractIndexAdapter<GeoPoint> {

    @Override
    public Object getIndexedValue(IEntity entity, MemberMeta memberMeta, GeoPoint value) {
        if (value == null) {
            return null;
        }
        if (allowAnyLocation(entity)) {
            List<String> cells = new Vector<String>();
            cells.add(GeoCell.GEOCELL_ANYLOCATION);
            cells.addAll(value.getCells());
            return cells;
        } else {
            return value.getCells();
        }
    }

    @Override
    public Class<?> getIndexValueClass() {
        return String[].class;
    }

    public boolean allowAnyLocation(IEntity entity) {
        return false;
    }
}
