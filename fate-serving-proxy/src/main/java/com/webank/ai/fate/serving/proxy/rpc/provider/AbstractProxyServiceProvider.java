package com.webank.ai.fate.serving.proxy.rpc.provider;

import com.webank.ai.fate.serving.core.bean.Context;
import com.webank.ai.fate.serving.core.exceptions.SysException;
import com.webank.ai.fate.serving.core.rpc.core.AbstractServiceAdaptor;
import com.webank.ai.fate.serving.core.rpc.core.InboundPackage;
import com.webank.ai.fate.serving.core.rpc.core.OutboundPackage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class AbstractProxyServiceProvider<req, resp> extends AbstractServiceAdaptor<req, resp> {

    @Override
    protected resp transformExceptionInfo(Context context, ExceptionInfo exceptionInfo) {
        return null;
    }

    @Override
    protected resp doService(Context context, InboundPackage<req> data, OutboundPackage<resp> outboundPackage) {
        Map<String, Method> methodMap = this.getMethodMap();
        String actionType = context.getActionType();
        Method method = methodMap.get(actionType);
        resp result = null;
        try {
            result = (resp) method.invoke(this, context, data);
        } catch (IllegalAccessException e) {
            throw new SysException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new SysException(e.getMessage());
        }
        return result;
    }

    @Override
    protected void printFlowLog(Context context) {

        flowLogger.info("{}|{}|" +
                        "{}|{}|{}|{}|" +
                        "{}|{}",
                context.getCaseId(), context.getReturnCode(), context.getCostTime(),
                context.getDownstreamCost(), serviceName, context.getRouterInfo() != null ? context.getRouterInfo() : "NO_ROUTER_INFO");
    }
}
