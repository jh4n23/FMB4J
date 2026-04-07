import org.sat4j.specs.ContradictionException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ModelBuilderTester {
    record TestCase(Path file, boolean expectModel) {}
    record TestResult(TestCase testCase, boolean passed, String reason) {}

    public static void main(String[] args) throws IOException {
        Path satDir = Paths.get("src/main/java/resources/tptp/satisfiable");
        Path unsatDir = Paths.get("src/main/java/resources/tptp/unsatisfiable");

        List<TestCase> testCases = new ArrayList<>();
        collectTests(satDir, true, testCases);
        collectTests(unsatDir, false, testCases);

        List<TestResult> results = runAll(testCases);
        printReport(results);

        boolean anyFailed = results.stream().anyMatch(r -> !r.passed());
        System.exit(anyFailed ? 1 : 0);
    }

    static void collectTests(Path dir, boolean expectModel, List<TestCase> out) throws IOException {
        if (!Files.exists(dir)) return;
        Files.walk(dir)
                .filter(p -> p.toString().endsWith(".txt"))
                .map(p -> new TestCase(p, expectModel))
                .forEach(out::add);
    }

    static List<TestResult> runAll(List<TestCase> cases) {
        List<TestResult> results = new ArrayList<>();

        for (TestCase tc : cases) {
            System.out.printf("Running %-50s ... ", tc.file().getFileName());
            TestResult result = run(tc);
            System.out.println(result.passed() ? "PASS" : "FAIL  <-- " + result.reason());
            results.add(result);
        }

        return results;
    }

    static TestResult run(TestCase tc) {
        int timeout = 10;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> result = executorService.submit(
                () -> Main.runModelBuilder(tc.file.toString(), false)
        );
        try {
            boolean modelFound = result.get(timeout, TimeUnit.SECONDS);
            boolean passed = modelFound == tc.expectModel();
            String reason = passed ? null
                    : String.format("expected modelFound=%b but got %b",
                    tc.expectModel(), modelFound);
            return new TestResult(tc, passed, reason);

        } catch (TimeoutException e) {
            return new TestResult(tc, !tc.expectModel, String.format("expected modelFound=%b, timed out after %ds", tc.expectModel, timeout));
        } catch (Exception e) {
            return new TestResult(tc, false, "threw exception: " + e.getMessage());
        }
    }

    static void printReport(List<TestResult> results) {
        long passed = results.stream().filter(TestResult::passed).count();
        long failed = results.size() - passed;

        System.out.println("\n===== RESULTS =====");
        System.out.printf("  Total:  %d%n", results.size());
        System.out.printf("  Passed: %d%n", passed);
        System.out.printf("  Failed: %d%n", failed);

        if (failed > 0) {
            System.out.println("\nFailed tests:");
            results.stream()
                    .filter(r -> !r.passed())
                    .forEach(r -> System.out.printf("  [FAIL] %s — %s%n",
                            r.testCase().file().getFileName(), r.reason()));
        }
    }
}
