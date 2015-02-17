/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Aug 15, 2014
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.propertyvista.biz.system.DNSResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xbill.DNS.Lookup;

public class JavaResolver {

    private String targetHost;

    private List<String> dnsServers = new ArrayList<String>();

    DNSLookup dnsLookup;

    public JavaResolver() {
        dnsLookup = new DNSLookup();
    }

    public JavaResolver(String host, List<String> dnsServers) {
        this.targetHost = host;
        this.dnsServers = dnsServers;
        dnsLookup = new DNSLookup();
    }

    public String resolveHost() throws IOException, InterruptedException {

        String ipAddress = "";

        Lookup lookupObj = dnsLookup.getLookupObj(targetHost, dnsServers);

        lookupObj.run();

        int result = lookupObj.getResult();

        if (result == Lookup.SUCCESSFUL) {
            ipAddress = lookupObj.getAnswers()[0].rdataToString();
        } else {
            try {
                dnsLookup.analyzeLookupError(targetHost, result);
            } catch (Throwable e) {
                throw e;
            }
        }

        return ipAddress;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    public List<String> getDnsServers() {
        return dnsServers;
    }

    public void setDnsServers(List<String> dnsServers) {
        this.dnsServers = dnsServers;
    }

}
