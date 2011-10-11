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
 * Created on Sep 9, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.model;

import java.io.Serializable;

public class PromoDataModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long propId;

    private String img;

    private String address;

    public Long getPropId() {
        return propId;
    }

    public void setPropId(Long id) {
        propId = id;
    }

    public String getImg() {
        return (img == null ? "" : img);
    }

    public PromoDataModel setImg(String img) {
        this.img = img;
        return this;
    }

    public String getAddress() {
        return (address == null ? "" : address);
    }

    public PromoDataModel setAddress(String address) {
        this.address = address;
        return this;
    }

}
