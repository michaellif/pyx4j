/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi;

public class YardiParameters {
    private String username;

    private String password;

    private String serverName;

    private String database;

    private String platform;

    private String interfaceEntity;

    private String yardiPropertyId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getInterfaceEntity() {
        return interfaceEntity;
    }

    public void setInterfaceEntity(String interfaceEntity) {
        this.interfaceEntity = interfaceEntity;
    }

    public String getYardiPropertyId() {
        return yardiPropertyId;
    }

    public void setYardiPropertyId(String yardiPropertyId) {
        this.yardiPropertyId = yardiPropertyId;
    }
}
