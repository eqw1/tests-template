package testrail;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestRailSuite {

    private APIClient apiClient;

    public TestRailSuite(APIClient apiClient) { this.apiClient = apiClient; }

    private int addTestCase(Integer sectionId, String title, String precondition, TestRailListener.Step[] steps)
            throws IOException, APIException
    {
        Map mapData = new HashMap();
        mapData.put("title", title);
        mapData.put("custom_preconds", precondition);
        mapData.put("custom_static_id", 1);
        JSONArray jSteps = new JSONArray();
        for (TestRailListener.Step step : steps) {
            JSONObject studentJSON = new JSONObject();
            studentJSON.put("content", step.content());
            studentJSON.put("expected", step.expected());
            jSteps.add(studentJSON);
        }
        mapData.put("custom_steps_separated", jSteps);
        JSONObject response = (JSONObject) apiClient.sendPost("add_case/" + sectionId, mapData);
        return Integer.valueOf(response.get("id").toString());
    }

    protected int getUpdateTestCaseIdByName(String projectId, Integer suiteId, Integer sectionId, String title, String preconditions, TestRailListener.Step[] steps)
        throws IOException, APIException
    {
        JSONArray response = (JSONArray) apiClient.sendGet("get_cases/" + projectId
                + "&suite_id=" + suiteId
                + "&section_id=" + sectionId);
        Optional<JSONObject> testCase = response.stream().filter(r -> ((JSONObject) r).get("title").equals(title)).findFirst();
        if (testCase.isPresent()) {
            Map mapData = new HashMap();
            mapData.put("title", title);
            mapData.put("custom_preconds", preconditions);
            mapData.put("custom_static_id", 2);
            JSONArray jSteps = new JSONArray();
            for (TestRailListener.Step step : steps) {
                JSONObject studentJSON = new JSONObject();
                studentJSON.put("content", step.content());
                studentJSON.put("expected", step.expected());
                jSteps.add(studentJSON);
            }
            mapData.put("custom_steps_separated", jSteps);
            JSONObject responseTest = (JSONObject) apiClient.sendPost("update_case/" + testCase.get().get("id").toString(), mapData);
            return Integer.valueOf(responseTest.get("id").toString());
        }
        else return addTestCase(sectionId, title, preconditions, steps);
    }

    private int addSection(String projectId, Integer suiteId, String sectionName, int parentId)
        throws IOException, APIException
    {
        Map mapData = new HashMap();
        mapData.put("suite_id", suiteId);
        mapData.put("name", sectionName);
        if (parentId != 0) mapData.put("parent_id", parentId);
        JSONObject response = (JSONObject) apiClient.sendPost("add_section/" + projectId, mapData);
        return Integer.valueOf(response.get("id").toString());
    }

    protected int getSectionIdByName(String projectId, Integer suiteId, String sectionName, int parentId)
        throws IOException, APIException
    {
        JSONArray response = (JSONArray) apiClient.sendGet("get_sections/" + projectId + "&suite_id=" + suiteId);
        Optional<JSONObject> section;
        if (parentId == 0) section = response.stream().filter(r -> ((JSONObject) r).get("name").equals(sectionName)).findFirst();
        else section = response.stream().filter(r -> ((JSONObject) r).get("name").equals(sectionName)
            && ((JSONObject) r).get("parent_id").toString().equals(String.valueOf(parentId))).findFirst();
        if (section.isPresent()) return Integer.valueOf(section.get().get("id").toString());
        else return addSection(projectId, suiteId, sectionName, parentId);
    }

    private int addSuite(String projectId, String suiteName)
        throws IOException, APIException
    {
        Map mapData = new HashMap();
        mapData.put("name", suiteName);
        JSONObject response = (JSONObject) apiClient.sendPost("add_suite/" + projectId, mapData);
        return  Integer.valueOf(response.get("id").toString());
    }

    protected int getSuiteIdByName(String projectId, String suiteName)
        throws IOException, APIException
    {
        JSONArray response = (JSONArray) apiClient.sendGet("get_suites/" + projectId);
        Optional<JSONObject> suite = response.stream().filter(r -> ((JSONObject) r).get("name").equals(suiteName)).findFirst();
        if (suite.isPresent()) return Integer.valueOf(suite.get().get("id").toString());
        else return addSuite(projectId, suiteName);
    }
}
