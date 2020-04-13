package com.ramon.ramonrm.webapi;

public class PageReqData {
    public PageReqData(ReqData.ReqType reqType, String sessionId, String validMD5) {
        reqData = ReqData.createReqData(reqType, sessionId, validMD5);
    }

    private ReqData reqData;
    public int RecordTotal = 1;
    public int PageSize = 20;
    public int PageIndex = 1;
    public String OutParams = "RecordTotal";

    public ReqData getReqData() {
        ReqData req = new ReqData(this.reqData);
        req.ExtParams.put("recordTotal", this.RecordTotal + "");
        req.ExtParams.put("outParams", this.OutParams);
        req.ExtParams.put("pageSize", this.PageSize + "");
        req.ExtParams.put("pageIndex", this.PageIndex + "");
        return req;
    }
}
