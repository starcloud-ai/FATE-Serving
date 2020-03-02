package com.webank.ai.fate.serving.core.bean;


import java.util.List;
import java.util.Map;

public class BatchHostFederatedParams {

    String  hostTableName;
    String  hostNamespace;
    String  guestPartyId;
    String  hostPartyId;
    String  seqNo;
    List<SingleBatchHostFederatedParams> dataList;

    public String getHostTableName() {
        return hostTableName;
    }

    public void setHostTableName(String hostTableName) {
        this.hostTableName = hostTableName;
    }

    public String getHostNamespace() {
        return hostNamespace;
    }

    public void setHostNamespace(String hostNamespace) {
        this.hostNamespace = hostNamespace;
    }

    public String getGuestPartyId() {
        return guestPartyId;
    }

    public void setGuestPartyId(String guestPartyId) {
        this.guestPartyId = guestPartyId;
    }

    public String getHostPartyId() {
        return hostPartyId;
    }

    public void setHostPartyId(String hostPartyId) {
        this.hostPartyId = hostPartyId;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public List<SingleBatchHostFederatedParams> getDataList() {
        return dataList;
    }

    public void setDataList(List<SingleBatchHostFederatedParams> dataList) {
        this.dataList = dataList;
    }



    public  class  SingleBatchHostFederatedParams{

        String  caseId;
        Map<String,Object> sendToRemoteData;

    }



}
