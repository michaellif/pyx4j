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
 * Created on Feb 4, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.rpc;

import static com.pyx4j.site.shared.meta.NavigNode.ARGS_GROUP_SEPARATOR;
import static com.pyx4j.site.shared.meta.NavigNode.ARGS_SEPARATOR;
import static com.pyx4j.site.shared.meta.NavigNode.NAME_VALUE_SEPARATOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18n.I18nStrategy;
import com.pyx4j.site.shared.meta.URLEncoder;

@I18n(strategy = I18nStrategy.DerivedOnly)
public class AppPlace extends Place {

    private static final Logger log = LoggerFactory.getLogger(AppPlace.class);

    public static final String ARG_NAME_ID = "Id";

    private Map<String, List<String>> queryArgs;

    private Map<String, List<String>> placeArgs;

    private boolean stable = true;

    @I18n(strategy = I18nStrategy.IgnoreAll)
    public static class NoWhereAppPlace extends AppPlace {
    }

    public static final AppPlace NOWHERE = new NoWhereAppPlace();

    public AppPlace() {
    }

    public AppPlace(Key itemID) {
        formPlace(itemID);
    }

    public AppPlace queryArg(String key, String... value) {
        if (queryArgs == null) {
            queryArgs = new HashMap<String, List<String>>();
        }
        List<String> valuesLists = new ArrayList<String>();
        valuesLists.addAll(Arrays.asList(value));
        queryArgs.put(key, valuesLists);
        return this;
    }

    public AppPlace placeArg(String key, String... value) {
        if (placeArgs == null) {
            placeArgs = new HashMap<String, List<String>>();
        }
        List<String> valuesLists = new ArrayList<String>();
        valuesLists.addAll(Arrays.asList(value));
        placeArgs.put(key, valuesLists);
        return this;
    }

    public void parseArgs(String queryString) {
        if (queryArgs == null) {
            queryArgs = new HashMap<String, List<String>>();
        }
        queryArgs.putAll(parseQueryString(queryString));
    }

    public Map<String, List<String>> getArgs() {
        Map<String, List<String>> args = new HashMap<String, List<String>>();
        if (queryArgs != null) {
            args.putAll(queryArgs);
        }
        if (placeArgs != null) {
            args.putAll(placeArgs);
        }
        return args.size() == 0 ? null : Collections.unmodifiableMap(args);
    }

    public List<String> getArg(String key) {
        List<String> arg = new ArrayList<String>();
        if (queryArgs != null) {
            arg.addAll(queryArgs.get(key));
        }
        if (placeArgs != null) {
            arg.addAll(placeArgs.get(key));
        }
        return arg.size() == 0 ? null : Collections.unmodifiableList(arg);
    }

    public String getFirstArg(String key) {
        if (queryArgs != null) {
            List<String> values = queryArgs.get(key);
            if (values != null && values.size() > 0) {
                return values.get(0);
            }
        }
        if (placeArgs != null) {
            List<String> values = placeArgs.get(key);
            if (values != null && values.size() > 0) {
                return values.get(0);
            }
        }
        return null;
    }

    public Key getItemId() {
        Key itemId = null;

        String val;
        if ((val = getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
            itemId = new Key(val);
        }
        return itemId;
    }

    public String getToken() {
        return getToken(false);
    }

    String getToken(boolean asUrlQuery) {
        return getPlaceId() + createQueryString(asUrlQuery);
    }

    public String getPlaceId() {
        return AppPlaceInfo.getPlaceId(getClass());
    }

    @Override
    public boolean equals(Object other) {
        if (getClass() == other.getClass()) {
            if (queryArgs == null && ((AppPlace) other).queryArgs == null) {
                return true;
            } else if (queryArgs != null && queryArgs.equals(((AppPlace) other).queryArgs)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        if (queryArgs != null) {
            hash = hash * 31 + queryArgs.hashCode();
        }
        return hash;
    }

    protected final String createQueryString(boolean asUrlQuery) {
        if (queryArgs == null) {
            return "";
        }
        StringBuilder queryString = new StringBuilder();
        boolean first = true;
        if (asUrlQuery) {
            first = false;
        }
        for (Map.Entry<String, List<String>> me : queryArgs.entrySet()) {
            if (first) {
                queryString.append(ARGS_GROUP_SEPARATOR);
                first = false;
            } else {
                queryString.append(ARGS_SEPARATOR);
            }

            List<String> values = me.getValue();
            for (int i = 0; i < values.size(); i++) {
                queryString.append(me.getKey());
                queryString.append(NAME_VALUE_SEPARATOR);
                queryString.append(URLEncoder.encodeQueryString(values.get(i)));
                if (i < values.size() - 1) {
                    queryString.append(ARGS_SEPARATOR);
                }
            }
        }
        return queryString.toString();
    }

    private Map<String, List<String>> parseQueryString(String queryString) {
        if (queryString.startsWith(ARGS_GROUP_SEPARATOR)) {
            queryString = queryString.substring(1);
        }
        Map<String, List<String>> args = new HashMap<String, List<String>>();
        if (queryString.length() == 0) {
            return args;
        }
        String[] nameValues = queryString.split(ARGS_SEPARATOR);
        if (nameValues.length > 0) {
            for (int i = 0; i < nameValues.length; i++) {
                String[] nameAndValue = nameValues[i].split(NAME_VALUE_SEPARATOR);
                if (nameAndValue.length == 2) {
                    if (!args.containsKey(nameAndValue[0])) {
                        args.put(nameAndValue[0], new ArrayList<String>());
                    }
                    args.get(nameAndValue[0]).add(URL.decodeQueryString(nameAndValue[1]));
                } else {
                    log.warn("Can't pars argument {}", nameValues[i]);
                }
            }
        }
        return args;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + createQueryString(false);
    }

    public AppPlace formPlace(Key itemID) {
        return queryArg(ARG_NAME_ID, itemID.toString());
    }

    /**
     * Place doesn't depend on the current application internal state and the state of application for that place is defined by place params.
     * This place can be bookmarked.
     * 
     * @return
     */
    public boolean isStable() {
        return stable;
    }

    public void setStable(boolean stable) {
        this.stable = stable;
    }

    public IDebugId asDebugId() {
        return new StringDebugId(getPlaceId().replace('/', '.'));
    }

    public boolean canUseAsDebugId() {
        if (placeArgs == null) {
            return true;
        } else if (placeArgs.containsKey(ARG_NAME_ID)) {
            return false;
        } else {
            return false;
        }
    }

    public AppPlace copy(AppPlace place) {
        if (place.queryArgs != null) {
            queryArgs = new HashMap<String, List<String>>(place.queryArgs);
        }
        if (place.placeArgs != null) {
            placeArgs = new HashMap<String, List<String>>(place.placeArgs);
        }
        stable = place.stable;

        return this;
    }
}
