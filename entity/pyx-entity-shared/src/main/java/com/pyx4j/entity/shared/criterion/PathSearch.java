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
 * Created on Feb 24, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.criterion;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class PathSearch extends Path implements Comparable<PathSearch> {

    private static final long serialVersionUID = -7287478852586750914L;

    private String pathProperty;

    private int cardinality;

    //TOSO should Restriction be here or in value? 
    //private Restriction restriction;

    protected PathSearch() {

    }

    public PathSearch(MemberMeta memberMeta, String path, String pathProperty) {
        super(path);
        this.pathProperty = pathProperty;
        setCardinality(cardinalityByType(memberMeta));
    }

    public PathSearch(IObject<?> object) {
        super(object);
        setCardinality(cardinalityByType(object.getMeta()));
    }

    public PathSearch(IObject<?> object, String pathProperty) {
        super(object);
        this.pathProperty = pathProperty;
        setCardinality(cardinalityByType(object.getMeta()));
    }

    /**
     * Longer key value ideally changes cardinality but this is not counted here!
     */
    public static int cardinalityByType(MemberMeta memberMeta) {
        if (memberMeta.getValueClass().isEnum()) {
            return 3;
        } else if (EditorType.phone.equals(memberMeta.getEditorType())) {
            return 2;
        } else {
            return 1;
        }
    }

    public String getPathProperty() {
        return pathProperty;
    }

    //TODO use better function name and probably in Path itself.
    public String getPathString() {
        return super.toString();
    }

    public int getCardinality() {
        return cardinality;
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    public String getHistoryKey() {
        StringBuilder key = new StringBuilder();
        boolean first = true;
        for (String p : getPathMembers()) {
            if (p.equals(COLLECTION_SEPARATOR)) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                key.append('.');
            }
            key.append(p);
        }
        if (pathProperty != null) {
            key.append('.');
            key.append(pathProperty);
        }
        return key.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PathSearch)) {
            return false;
        }
        return CommonsStringUtils.equals(pathProperty, ((PathSearch) other).pathProperty) && super.equals(other);
    }

    @Override
    public int hashCode() {
        return 0x1F * ((this.pathProperty != null) ? this.pathProperty.hashCode() : 0) + super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + " (" + pathProperty + ")";
    }

    @Override
    public int compareTo(PathSearch other) {
        return ((cardinality == other.cardinality) ? (toString().compareTo(other.toString())) : (cardinality < other.cardinality) ? -1 : 1);
    }

}
