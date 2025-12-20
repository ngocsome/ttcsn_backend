package org.example.model.dto;

import org.example.model.MSTResult;

public class RunGaResponse {
    private long runId;
    private MSTResult result;

    public RunGaResponse() {}
    public RunGaResponse(long runId, MSTResult result) {
        this.runId = runId;
        this.result = result;
    }

    public long getRunId() { return runId; }
    public void setRunId(long runId) { this.runId = runId; }

    public MSTResult getResult() { return result; }
    public void setResult(MSTResult result) { this.result = result; }

    @Override
    public String toString() {
        return "RunGaResponse{" + "runId=" + runId + ", result=" + result + '}';
    }
}
