package com.webank.ai.fate.serving.guest.provider;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.webank.ai.fate.api.networking.proxy.Proxy;

import com.webank.ai.fate.serving.core.bean.*;
import com.webank.ai.fate.serving.core.exceptions.BaseException;
import com.webank.ai.fate.serving.core.exceptions.ErrorCode;
import com.webank.ai.fate.serving.core.model.Model;
import com.webank.ai.fate.serving.core.model.ModelProcessor;
import com.webank.ai.fate.serving.core.rpc.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@FateService(name ="batchInferenece",  preChain= {
       // "overloadMonitor",
        "guestBatchParamInterceptor",
        "guestModelInterceptor",
        "federationRouterInterceptor"
      },postChain = {
        //"cache",

})
@Service
public class BatchGuestInferenceProvider extends AbstractServingServiceProvider<BatchInferenceRequest,BatchInferenceResult>{

    @Autowired
    FederatedRpcInvoker federatedRpcInvoker;

    final long DEFAULT_TIME_OUT = 3000;


    @Override
    public BatchInferenceResult doService(Context context, InboundPackage inboundPackage, OutboundPackage outboundPackage) {

        Model  model =((ServingServerContext) context).getModel();

        ModelProcessor modelProcessor = model.getModelProcessor();

        BatchInferenceRequest   batchInferenceRequest =(BatchInferenceRequest)inboundPackage.getBody();
        /**
         *  有些算法模块如 SBT,需要特殊的处理，会添加数据到sendToRemote ，如果后续有算法模块需要同样的操作，可以实现PrepareRemoteable接口
         */
        modelProcessor.guestPrepareDataBeforeInference(context,batchInferenceRequest);
        /**
         *  准备发往对端的参数  , 因为要考虑之后有可能支持多方 ，
         */
        Map  futureMap = Maps.newHashMap();


        model.getFederationModelMap().forEach((hostPartyId,remoteModel)-> {

                    BatchHostFederatedParams batchHostFederatedParams = buildBatchHostFederatedParams(context, batchInferenceRequest,model,remoteModel);
                    /**
                     * guest 端与host同步预测，再合并结果
                     */
                    FederatedRpcInvoker.RpcDataWraper rpcDataWraper = new FederatedRpcInvoker.RpcDataWraper();
                    /**
                     *
                     */
                    rpcDataWraper.setGuestModel(model);
                    rpcDataWraper.setHostModel(remoteModel);
                    rpcDataWraper.setData(batchHostFederatedParams);
                    rpcDataWraper.setRemoteMethodName(Dict.REMOTE_METHOD_BATCH);

                    ListenableFuture<Proxy.Packet> originBatchResultFuture = federatedRpcInvoker.async(context, rpcDataWraper);

                    futureMap.put(hostPartyId,originBatchResultFuture);
                });
        /**
         *  超时时间需要根据实际情况调整
         */
        BatchInferenceResult batchFederatedResult = modelProcessor.guestBatchInference(context, batchInferenceRequest, futureMap,DEFAULT_TIME_OUT);

        return  batchFederatedResult;
    }

    @Override
    protected  OutboundPackage<BatchInferenceResult>  serviceFailInner(Context context, InboundPackage<BatchInferenceRequest> data, Throwable e) {

        Map result = new HashMap();
        OutboundPackage<BatchInferenceResult> outboundPackage = new OutboundPackage<BatchInferenceResult>();
        BatchInferenceResult  batchInferenceResult = new  BatchInferenceResult();
        if(e instanceof BaseException){
            BaseException  baseException = (BaseException) e;
            batchInferenceResult.setRetcode(baseException.getRetcode());
            batchInferenceResult.setRetmsg(e.getMessage());
        }else{
            batchInferenceResult.setRetcode(ErrorCode.SYSTEM_ERROR);
        }
        outboundPackage.setData(batchInferenceResult);
        return  outboundPackage;
    }


}
