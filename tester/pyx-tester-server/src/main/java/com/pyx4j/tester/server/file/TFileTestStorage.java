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
 * Created on 2012-12-28
 * @author vlads
 */
package com.pyx4j.tester.server.file;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.Key;
import com.pyx4j.gwt.server.IOUtils;

class TFileTestStorage {

    private static long id = 0;

    private static Map<Key, byte[]> data = new HashMap<Key, byte[]>();

    static Key persist(byte[] content, String name, String contentType) {
        id++;
        Key key = new Key(id);
        data.put(key, content);
        return key;
    }

    static byte[] retrieve(Key key) {
        return data.get(key);
    }

    static {
        try {
            persist(IOUtils.getBinaryResource("1.png", TFileTestStorage.class), null, null);
            persist(IOUtils.getBinaryResource("2.png", TFileTestStorage.class), null, null);
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
