package testrail;

import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.aeonbits.owner.ConfigFactory;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.IClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import ru.yandex.qatools.allure.annotations.Title;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestRailListener implements ITestListener {

    private APIClient apiClient;
    private TestRailConfig config;

    private Integer testId;
    private String runName;
    private Integer testRunId = 0;

    private ByteArrayOutputStream queryLog = new ByteArrayOutputStream();

    private PrintStream requestVar = new PrintStream(queryLog, true);
    private PrintStream responseVar = new PrintStream(queryLog, true);

    public TestRailListener() { this.config = ConfigFactory.create(TestRailConfig.class); }

    TestRailConfig getConfig() {
        return config;
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        TestRailRun testRailRun = new TestRailRun(apiClient);
        try {
            testRailRun.addResult(testId, 1, "Passed\n" + queryLog);
            queryLog.reset();
            requestVar = new PrintStream(queryLog, true);
            responseVar = new PrintStream(queryLog, true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        TestRailRun testRailRun = new TestRailRun(apiClient);
        try {
            testRailRun.addResult(testId, 5, "Bug: " + iTestResult.getThrowable().getMessage() + "\n" + queryLog);
            queryLog.reset();
            requestVar = new PrintStream(queryLog, true);
            responseVar = new PrintStream(queryLog, true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        TestRailRun testRailRun = new TestRailRun(apiClient);
        try {
            testRailRun.addResult(testId, 4, "Retest, test skipped");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    @Override
    public void onStart(ITestContext iTestContext) {
        apiClient = new APIClient(getConfig().link());
        apiClient.setUser(getConfig().user());
        apiClient.setPassword(getConfig().password());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        runName = dtf.format(LocalDateTime.now());
    }

    @Override
    public void onFinish(ITestContext iTestContext) {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface UseAsTestRailId{
        int testRailId() default 0;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE,ElementType.METHOD})
    public @interface Precondition {
        String precondition() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface SectionNames {
        String[] sectionNames() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface StepsDescription {
        Step[] step();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Step {
        String content() default "";
        String expected() default "";
    }


    @Override
    public void onTestStart(ITestResult iTestResult) {
        try {
            RestAssured.filters(new ResponseLoggingFilter(LogDetail.ALL, responseVar),
                    new RequestLoggingFilter(LogDetail.ALL, requestVar));
            TestRailSuite testRailSuite = new TestRailSuite(apiClient);
            TestRailRun testRailRun = new TestRailRun(apiClient);
            // Создаем тест сьют, если новая версия. Получаем сьют если версия не изменилась
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            String suiteName = model.getParent().getVersion();
            int suiteId = testRailSuite.getSuiteIdByName(getConfig().projectId(), suiteName);
            int sectionId = 0;
            if (getTestClass(iTestResult).isAnnotationPresent(SectionNames.class)) {
                SectionNames sectionNamesAn = (SectionNames) getTestClass(iTestResult).getAnnotation(SectionNames.class);
                for (String sectionName : sectionNamesAn.sectionNames()) {
                    sectionId = testRailSuite.getSectionIdByName(getConfig().projectId(), suiteId, sectionName, sectionId);
                }
            }
            // Создаем или обновляем тест-кейс
            String precondition = "";
            if ((getTestClass(iTestResult).getAnnotation(Precondition.class)) != null) {
                precondition = ((Precondition) getTestClass(iTestResult).getAnnotation(Precondition.class)).precondition();
            }
            Integer caseId = testRailSuite.getUpdateTestCaseIdByName(getConfig().projectId(), suiteId, sectionId,
                    getTestMethod(iTestResult).getAnnotation(Title.class).value(),
                    precondition,
                    getTestMethod(iTestResult).getAnnotation(StepsDescription.class).step());
            // Создаем тест ран на каждый прогон автотестов
            if (testRunId == 0) {
                testRunId = testRailRun.addTestRun(getConfig().projectId(), suiteId, model.getParent().getVersion() + " : " + runName);
            } else {
                testRunId = testRailRun.updateTestRun(testRunId);
            }
            testId = testRailRun.getTest(testRunId, caseId);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private Method getTestMethod(ITestResult iTestResult) {
        IClass iClassObj = iTestResult.getTestClass();
        Class classObj = iClassObj.getRealClass();
        Method localTestMethod = null;
        try {
            localTestMethod = classObj.getMethod(iTestResult.getName(), iTestResult.getMethod().getConstructorOrMethod().getParameterTypes());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return localTestMethod;
    }

    private Class getTestClass(ITestResult iTestResult) {
        IClass iClassObj = iTestResult.getTestClass();
        Class classObj = iClassObj.getRealClass();
        return classObj;
    }
}
