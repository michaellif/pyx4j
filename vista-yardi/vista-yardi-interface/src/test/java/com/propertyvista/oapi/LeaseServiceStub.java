package com.propertyvista.oapi;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "LeaseServiceImplService", targetNamespace = "http://oapi.propertyvista.com/", wsdlLocation = "http://localhost:8888/WS/LeaseService?wsdl")
public class LeaseServiceStub extends Service {

    private final static URL IMPL_SERVICE_WSDL_LOCATION;

    private final static WebServiceException IMPL_SERVICE_EXCEPTION;

    private final static QName IMPL_SERVICE_QNAME = new QName("http://oapi.propertyvista.com/", "LeaseServiceImplService");

    private final static QName IMPL_PORT_QNAME = new QName("http://oapi.propertyvista.com/", "LeaseServiceImplPort");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8888/WS/LeaseService?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        IMPL_SERVICE_WSDL_LOCATION = url;
        IMPL_SERVICE_EXCEPTION = e;
    }

    public LeaseServiceStub() {
        super(getWsdlLocation(), IMPL_SERVICE_QNAME);
    }

    public LeaseServiceStub(WebServiceFeature... features) {
        super(getWsdlLocation(), IMPL_SERVICE_QNAME, features);
    }

    public LeaseServiceStub(URL wsdlLocation) {
        super(wsdlLocation, IMPL_SERVICE_QNAME);
    }

    public LeaseServiceStub(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, IMPL_SERVICE_QNAME, features);
    }

    public LeaseServiceStub(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public LeaseServiceStub(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    @WebEndpoint(name = "LeaseServiceImplPort")
    public LeaseService getLeaseServicePort() {
        return super.getPort(IMPL_PORT_QNAME, LeaseService.class);
    }

    @WebEndpoint(name = "LeaseServiceImplPort")
    public LeaseService getLeaseServicePort(WebServiceFeature... features) {
        return super.getPort(IMPL_PORT_QNAME, LeaseService.class, features);
    }

    private static URL getWsdlLocation() {
        if (IMPL_SERVICE_EXCEPTION != null) {
            throw IMPL_SERVICE_EXCEPTION;
        }
        return IMPL_SERVICE_WSDL_LOCATION;
    }

}
