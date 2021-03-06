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
 * Created on 2010-08-10
 * @author vlads
 */
package com.pyx4j.essentials.rpc.admin;

import java.io.Serializable;

public class BackupKey implements Serializable {

    private static final long serialVersionUID = -2803710332716787347L;

    private String kind;

    private long id;

    private String name;

    public BackupKey() {

    }

    public BackupKey(String kind, long id) {
        this.kind = kind;
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getKind() + ":" + ((getId() == 0) ? getName() : String.valueOf(getId()));
    }

    public static BackupKey valueOf(String s) {
        BackupKey k = new BackupKey();
        String[] v = s.split(":");
        k.setKind(v[0]);
        try {
            k.setId(Long.parseLong(v[1]));
        } catch (NumberFormatException e) {
            k.setName(v[1]);
        }
        return k;
    }
}
