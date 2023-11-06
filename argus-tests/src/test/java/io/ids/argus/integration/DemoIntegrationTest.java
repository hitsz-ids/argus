package io.ids.argus.integration;

import io.ids.argus.base.BaseHttpTest;
import io.ids.argus.core.base.json.ArgusJson;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.entry.Application;
import io.ids.argus.extension.demo.DemoApplication;
import io.ids.argus.job.server.JobApplication;
import io.ids.argus.store.server.StoreApplication;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author jalr4real[jalrhex@gmail.com]
 * @since 2023-11-02
 */

public class DemoIntegrationTest extends BaseHttpTest {


    private final ArgusLogger log = new ArgusLogger(DemoIntegrationTest.class);

    private static final String HTTP_SERVER_URL = "http://localhost:%s" + "/argus/execute";

    private static final AtomicInteger HTTP_SERVER_PORT = new AtomicInteger();

    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:5.7"));

    private static final ExecutorService INTEGRATION_TEST_THREAD_POOL = new ThreadPoolExecutor(8, 64, 0L, java.util.concurrent.TimeUnit.MILLISECONDS, new SynchronousQueue<>(), new ThreadFactory() {
        private int count = 0;

        @Override
        public Thread newThread(@NotNull Runnable r) {
            count++;
            return new Thread(r, "argus-test-" + count);
        }
    }, new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());

    @BeforeAll
    static void init() throws InterruptedException {
        // generate certificates
        String workPath = System.getProperty("user.dir") + "/src/main/resources/";
        exec(Arrays.asList("sh", workPath + "mirror_certificate.sh"), workPath);
        String caPath = workPath + "output/ca/ca.pem";
        String centerPublicPath = workPath + "output/center/center.pem";
        String centerPrivatePath = workPath + "output/center/center-pkcs8.key";
        String centerPubModulePath = workPath + "output/center/module";
        String centerModules = "module://demo?version=1.0.0;2.0.0";
        String modulePublicPath = workPath + "output/module/module.pem";
        String modulePrivatePath = workPath + "output/module/module-pkcs8.key";

        mysql.withReuse(true)
                .withDatabaseName("argus_db")
                .withInitScript("mirror_init.sql")
                .withUsername("root")
                .withPassword("123456")
                .withExposedPorts(3306)
                .start();

        AtomicInteger storeServerPort = new AtomicInteger();
        AtomicInteger jobServerPort = new AtomicInteger();
        AtomicInteger httpServerPort = new AtomicInteger();
        AtomicInteger centerServerPort = new AtomicInteger();


        Semaphore storeRunning = new Semaphore(1);
        Semaphore jobRunning = new Semaphore(0);
        Semaphore httpRunning = new Semaphore(0);
        Semaphore demoRunning = new Semaphore(0);
        Semaphore mainRunning = new Semaphore(0);
        CountDownLatch last = new CountDownLatch(1);

        // store server
        CompletableFuture.runAsync(() -> {
            try {
                storeRunning.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            storeServerPort.set(availablePort());
            System.setProperty("store.server.port", String.valueOf(storeServerPort.get()));
            System.setProperty("argus.store.db.host", "localhost");
            System.setProperty("argus.store.db.host", "localhost");
            System.setProperty("argus.store.db.port", String.valueOf(mysql.getMappedPort(3306)));
            System.setProperty("argus.store.db.username", "root");
            System.setProperty("argus.store.db.auth", "123456");
            System.setProperty("argus.store.db.database", "argus_db");

            StoreApplication.register(jobRunning::release);
            StoreApplication.main(new String[0]);

        }, INTEGRATION_TEST_THREAD_POOL);

        // job server
        CompletableFuture.runAsync(() -> {
            try {
                System.setProperty("store.server.port", String.valueOf(storeServerPort.get()));
                jobServerPort.set(availablePort());
                System.setProperty("job.server.port", String.valueOf(jobServerPort.get()));

                jobRunning.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            JobApplication.register(httpRunning::release);
            JobApplication.main(new String[0]);
        }, INTEGRATION_TEST_THREAD_POOL);

        // http server
        CompletableFuture.runAsync(() -> {
            try {
                httpRunning.acquire();

                httpServerPort.set(availablePort());
                centerServerPort.set(availablePort());
                System.setProperty("ca.public", caPath);
                System.setProperty("http.server.port", String.valueOf(httpServerPort.get()));
                System.setProperty("center.server.port", String.valueOf(centerServerPort.get()));
                System.setProperty("server.public", centerPublicPath);
                System.setProperty("server.pkcs8", centerPrivatePath);
                System.setProperty("server.module.pub.dir", centerPubModulePath);
                System.setProperty("modules", centerModules);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Application.register(demoRunning::release);
            Application.main(new String[0]);
        }, INTEGRATION_TEST_THREAD_POOL);

        // demo service
        CompletableFuture.runAsync(() -> {
            try {
                demoRunning.acquire();

                System.setProperty("center.host", "127.0.0.1");
                System.setProperty("center.port", String.valueOf(centerServerPort));
                System.setProperty("module.name", "demo");
                System.setProperty("module.version", "1.0.0");
                System.setProperty("module.public", modulePublicPath);
                System.setProperty("module.pkcs8", modulePrivatePath);
                System.setProperty("ca.public", caPath);
                System.setProperty("job.server.port", String.valueOf(jobServerPort.get()));

                DemoApplication.register(mainRunning::release);
                last.countDown();
                DemoApplication.main(new String[0]);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, INTEGRATION_TEST_THREAD_POOL);

        // Wait last one boot completed.
        last.await();
        mainRunning.acquire();
        HTTP_SERVER_PORT.set(httpServerPort.get());
    }

    @AfterAll
    static void free() {
        mysql.stop();
    }


    @RepeatedTest(5)
    void demoJobCase() {
        // create job
        ArgusJson param = new ArgusJson();
        param.add("path", "demo/1.0.0/test/job");
        ArgusJson createParam = new ArgusJson();
        createParam.add("name", "task-" + UUID.randomUUID());
        param.add("params", createParam);
        Response response = post(runningHttpServer(HTTP_SERVER_PORT.get()), param.toJsonString());
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
        AtomicReference<String> seq = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> {
            Map v = Transformer.fromJson(Objects.requireNonNull(response.body()).string(), Map.class);
            seq.set(v.get("seq").toString());
            Assertions.assertNotNull(seq.get());

        });

        log.info("job seq: {}", seq.get());

        // stop job
        ArgusJson param2 = new ArgusJson();
        param2.add("path", "demo/1.0.0/@stop-job@");
        ArgusJson stopParam = new ArgusJson();
        stopParam.add("seq", seq.get());
        param2.add("params", stopParam);

        Response response2 = post(runningHttpServer(HTTP_SERVER_PORT.get()), param2.toJsonString());
        Assertions.assertNotNull(response2);
        Assertions.assertTrue(response2.isSuccessful());
        Assertions.assertDoesNotThrow(() -> {
            Map v = Transformer.fromJson(Objects.requireNonNull(response2.body()).string(), Map.class);
            Assertions.assertEquals(v.get("code"), 1);
        });
    }

    private static void exec(List<String> commands, String outputPath) {

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(new File(outputPath));


        try {
            Process process = processBuilder.start();
            int exitValue = process.waitFor();

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            System.out.println("Exit Value: " + exitValue);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int availablePort() {
        try (
                ServerSocket socket = new ServerSocket(0);
        ) {
            return socket.getLocalPort();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String runningHttpServer(Integer port) {
        return String.format(DemoIntegrationTest.HTTP_SERVER_URL, port);
    }

}
