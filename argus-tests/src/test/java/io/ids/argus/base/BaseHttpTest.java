package io.ids.argus.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ids.argus.core.conf.log.ArgusLogger;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author jalr4real[jalrhex@gmail.com]
 * @since 2023-11-02
 */
public class BaseHttpTest implements BaseTest {

    private final OkHttpClient client = new OkHttpClient();

    private final ArgusLogger log = new ArgusLogger(BaseHttpTest.class);

    protected String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }


    protected Response post(String url, String requestBody) {

        RequestBody body = RequestBody.create(requestBody.getBytes(StandardCharsets.UTF_8));
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json; charset=UTF-8")
                .post(body)
                .build();

        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
    protected Response put(String url, String requestBody) {

        RequestBody body = RequestBody.create(requestBody.getBytes(StandardCharsets.UTF_8));
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json; charset=UTF-8")
                .put(body)
                .build();

        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
