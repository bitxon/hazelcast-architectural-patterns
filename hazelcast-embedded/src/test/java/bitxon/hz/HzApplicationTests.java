package bitxon.hz;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient(timeout = "20000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HzApplicationTests {

    private static final String CACHE_NAME = "mainCache";

    private static final String GET_PATH = "/cache/{key}";
    private static final String PUT_PATH = "/cache/{key}/value/{value}";
    private static final String GET_LOCK_PATH = "/locked-cache/{key}";

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Test
    public void getValue() {
        //given
        getCache().put("key1", "AAAAA");

        //when
        var responseSpec = webTestClient
            .get()
            .uri(GET_PATH, "key1")
            .exchange();

        //then
        responseSpec
            .expectStatus().is2xxSuccessful()
            .expectBody().equals("AAAAA");
    }

    @Test
    public void putValue() {
        //when
        var responseSpec = webTestClient
            .put()
            .uri(PUT_PATH, "key2", "BBBBB")
            .exchange();

        //then
        responseSpec.expectStatus().is2xxSuccessful();
        assertEquals("BBBBB", getCache().get("key2"));
    }

    @Test
    public void getWithLock() {
        //given
        getCache().put("keyWithLock", "lockedValue");

        //when
        var future1 = CompletableFuture.supplyAsync(() ->
            webTestClient.get().uri(GET_LOCK_PATH, "keyWithLock").exchange());
        var future2 = CompletableFuture.supplyAsync(() ->
            webTestClient.get().uri(GET_LOCK_PATH, "keyWithLock").exchange());
        var future3 = CompletableFuture.supplyAsync(() ->
            webTestClient.get().uri(GET_LOCK_PATH, "keyWithLock").exchange());

        //then
        var resultStatuses = Stream.of(future1, future2, future3)
            .map(CompletableFuture::join)
            .map(x -> x.returnResult(String.class).getStatus().value())
            .toList();
        assertThat(resultStatuses)
            .as("One should succeed other should failed with timeout")
            .containsExactlyInAnyOrder(200, 408, 408);

    }

    @Test
    public void joinCluster() {
        //given
        Hazelcast.newHazelcastInstance();

        //then
        assertEquals(2, hazelcastInstance.getCluster().getMembers().size());
    }



    private ConcurrentMap<String, String> getCache() {
        return hazelcastInstance.getMap(CACHE_NAME);
    }
}
