package com.propertyvista.oapi;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "PropertyServiceImplService", targetNamespace = "http://oapi.propertyvista.com/", wsdlLocation = "http://localhost:8888/WS/PropertyService?wsdl")
public class PropertyServiceStub extends Service {

    private final static URL IMPL_SERVICE_WSDL_LOCATION;

    private final static WebServiceException IMPL_SERVICE_EXCEPTION;

    private final static QName IMPL_SERVICE_QNAME = new QName("http://oapi.propertyvista.com/", "PropertyServiceImplService");

    private final static QName IMPL_PORT_QNAME = new QName("http://oapi.propertyvista.com/", "PropertyServiceImplPort");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8888/WS/PropertyService?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        IMPL_SERVICE_WSDL_LOCATION = url;
        IMPL_SERVICE_EXCEPTION = e;
    }

    public PropertyServiceStub() {
        super(getWsdlLocation(), IMPL_SERVICE_QNAME);
    }

    public PropertyServiceStub(WebServiceFeature... features) {
        super(getWsdlLocation(), IMPL_SERVICE_QNAME, features);
    }

    public PropertyServiceStub(URL wsdlLocation) {
        super(wsdlLocation, IMPL_SERVICE_QNAME);
    }

    public PropertyServiceStub(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, IMPL_SERVICE_QNAME, features);
    }

    public PropertyServiceStub(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public PropertyServiceStub(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    @WebEndpoint(name = "PropertyServiceImplPort")
    public PropertyService getPropertyServicePort() {
        return super.getPort(IMPL_PORT_QNAME, PropertyService.class);
    }

    @WebEndpoint(name = "PropertyServiceImplPort")
    public PropertyService getPropertyServicePort(WebServiceFeature... features) {
        return super.getPort(IMPL_PORT_QNAME, PropertyService.class, features);
    }

    private static URL getWsdlLocation() {
        if (IMPL_SERVICE_EXCEPTION != null) {
            throw IMPL_SERVICE_EXCEPTION;
        }
        return IMPL_SERVICE_WSDL_LOCATION;
    }

}
