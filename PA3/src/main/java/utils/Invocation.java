package utils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @description: Transmission object between the client and the server.
 * @author: Xue Zhang
 * @date: 2022-02-07
 * @version: 1.0
 **/
public class Invocation implements Serializable {

    private static final long serialVersionID = 1L;

    private String interfaceName;
    private String methodName;

    private Class[] paramTypes;
    private Object[] args;

    public Invocation(String interfaceName, String methodName, Class[] paramTypes, Object[] args) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.args = args;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "Invocation{" +
                "interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
