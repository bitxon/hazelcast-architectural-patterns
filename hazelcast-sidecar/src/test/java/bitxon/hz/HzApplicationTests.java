package bitxon.hz;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static bitxon.hz.HzController.LOCKED_CACHE;
import static bitxon.hz.HzController.MAIN_CACHE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient(timeout = "20000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HzApplicationTests {

	private static final String GET_PATH = "/cache/{key}";
	private static final String PUT_PATH = "/cache/{key}/{value}";
	private static final String GET_LOCK_PATH = "/fenced-lock-cache/{key}";
	private static final String PUT_LOCK_PATH = "/fenced-lock-cache/{key}/{value}";

	@Autowired
	private WebTestClient webTestClient;
	@Autowired
	private HazelcastInstance hazelcastInstance;

	@BeforeAll
	public static void beforeAll() {
		Hazelcast.newHazelcastInstance();
	}

	@AfterAll
	public static void afterAll() {
		Hazelcast.shutdownAll();
	}

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
		getLockedCache().put("keyWithLock", "lockedValue");

		//when
		var get1 = CompletableFuture.supplyAsync(() ->
			webTestClient.get().uri(GET_LOCK_PATH, "keyWithLock").exchange());
		var get2 = CompletableFuture.supplyAsync(() ->
			webTestClient.get().uri(GET_LOCK_PATH, "keyWithLock").exchange());
		var get3 = CompletableFuture.supplyAsync(() ->
			webTestClient.get().uri(GET_LOCK_PATH, "keyWithLock").exchange());

		//then
		var resultStatuses = Stream.of(get1, get2, get3)
			.map(CompletableFuture::join)
			.map(x -> x.returnResult(String.class).getStatus().value())
			.toList();
		assertThat(resultStatuses)
			.containsExactlyInAnyOrder(200, 200, 200);

	}

	@Test
	public void putAndGetWithLock() {
		//given
		getLockedCache().put("keyWithLock2", "lockedValue2");

		//when
		var get1 = CompletableFuture.supplyAsync(() ->
			webTestClient.get().uri(GET_LOCK_PATH, "keyWithLock2").exchange());
		var get2 = CompletableFuture.supplyAsync(() ->
			webTestClient.get().uri(GET_LOCK_PATH, "keyWithLock2").exchange());
		var put1 = CompletableFuture.supplyAsync(() ->
			webTestClient.put().uri(PUT_LOCK_PATH, "keyWithLock2", "RaNd0M").exchange());
		var get3 = CompletableFuture.supplyAsync(() ->
			webTestClient.get().uri(GET_LOCK_PATH, "keyWithLock2").exchange());

		//then
		var resultStatuses = Stream.of(get1, put1, get2, get3)
			.map(CompletableFuture::join)
			.map(x -> x.returnResult(String.class).getStatus().value())
			.toList();
		assertThat(resultStatuses)
			.containsExactlyInAnyOrder(200, 200, 200, 200);

		var resultBodies = Stream.of(get1, put1, get2, get3)
			.map(CompletableFuture::join)
			.map(x -> x.returnResult(String.class).getResponseBody().blockFirst())
			.toList();
		assertThat(resultBodies)
			.containsAnyOf("lockedValue2", "RaNd0M");
	}


	private ConcurrentMap<String, String> getCache() {
		return hazelcastInstance.getMap(MAIN_CACHE);
	}

	private ConcurrentMap<String, String> getLockedCache() {
		return hazelcastInstance.getMap(LOCKED_CACHE);
	}
}
