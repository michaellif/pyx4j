package com.propertyvista.oapi.ws;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "WSReceivableServiceImplService", targetNamespace = "http://ws.oapi.propertyvista.com/", wsdlLocation = "http://localhost:8888/WS/WSReceivableService?wsdl")
public class WSReceivableServiceStub extends Service {

    private final static URL IMPL_SERVICE_WSDL_LOCATION;

    private final static WebServiceException IMPL_SERVICE_EXCEPTION;

    private final static QName IMPL_SERVICE_QNAME = new QName("http://ws.oapi.propertyvista.com/", "WSReceivableServiceImplService");

    private final static QName IMPL_PORT_QNAME = new QName("http://ws.oapi.propertyvista.com/", "WSReceivableServiceImplPort");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8888/WS/WSReceivableService?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        IMPL_SERVICE_WSDL_LOCATION = url;
        IMPL_SERVICE_EXCEPTION = e;
    }

    public WSReceivableServiceStub() {
        super(getWsdlLocation(), IMPL_SERVICE_QNAME);
    }

    public WSReceivableServiceStub(WebServiceFeature... features) {
        super(getWsdlLocation(), IMPL_SERVICE_QNAME, features);
    }

    public WSReceivableServiceStub(URL wsdlLocation) {
        super(wsdlLocation, IMPL_SERVICE_QNAME);
    }

    public WSReceivableServiceStub(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, IMPL_SERVICE_QNAME, features);
    }

    public WSReceivableServiceStub(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSReceivableServiceStub(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    @WebEndpoint(name = "WSReceivableServiceImplPort")
    public WSReceivableService getReceivableServicePort() {
        return super.getPort(IMPL_PORT_QNAME, WSReceivableService.class);
    }

    @WebEndpoint(name = "WSReceivableServiceImplPort")
    public WSReceivableService getReceivableServicePort(WebServiceFeature... features) {
        return super.getPort(IMPL_PORT_QNAME, WSReceivableService.class, features);
    }

    private static URL getWsdlLocation() {
        if (IMPL_SERVICE_EXCEPTION != null) {
            throw IMPL_SERVICE_EXCEPTION;
        }
        return IMPL_SERVICE_WSDL_LOCATION;
    }

}
