package testrail;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestRailRun {

    private APIClient apiClient;

    public TestRailRun(APIClient apiClient) { this.apiClient = apiClient; }

    protected int addTestRun(String projectId, Integer suiteId, String runName) throws IOException, APIException {
        Map mapData = new HashMap();
        mapData.put("suite_id", suiteId);
        mapData.put("name", runName);
        mapData.put("include_all", true);
        JSONObject response = (JSONObject) apiClient.sendPost("add_run/" + projectId, mapData);
        return Integer.valueOf(response.get("id").toString());
    }

    protected int updateTestRun(Integer runId) throws IOException, APIException {
        Map mapData = new HashMap();
        mapData.put("include_all", true);
        JSONObject response = (JSONObject) apiClient.sendPost("update_run/" + runId, mapData);
        return Integer.valueOf(response.get("id").toString());
    }

    protected void addResult(Integer testId, Integer statusId, String comment) throws IOException, APIException {
        Map mapData = new HashMap();
        mapData.put("status_id", statusId);
        mapData.put("comment", comment);
        JSONObject response = (JSONObject) apiClient.sendPost("add_result/" + testId, mapData);
    }

    protected int getTest(Integer runId, Integer caseId) throws IOException, APIException {
        JSONArray response = (JSONArray) apiClient.sendGet("get_tests/" + runId);
        Optional<JSONObject> id = response.stream().filter(r -> ((JSONObject) r).get("case_id").toString().equals(caseId.toString())).findFirst();
        return Integer.valueOf(id.get().get("id").toString());
    }
}
