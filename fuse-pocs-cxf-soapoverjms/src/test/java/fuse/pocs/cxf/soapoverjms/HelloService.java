package fuse.pocs.cxf.soapoverjms;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface HelloService {

    @WebMethod
    String sayHello();

}
