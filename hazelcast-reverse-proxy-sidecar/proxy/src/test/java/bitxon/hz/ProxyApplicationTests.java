package bitxon.hz;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles("test")
@SpringBootTest(classes = ProxyApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0) // will provide 'wiremock.server.port' to context
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProxyApplicationTests {

    @LocalServerPort
    Integer localPort;

    @BeforeAll
    void setUp() {
        RestAssured.port = localPort;
        stubFor(any(anyUrl()).willReturn(ok("Success Wiremock Hit")));
    }

    @ParameterizedTest
    @CsvSource({
        "GET,  /cache/key1",
        "GET,  /cache/key2",
        "PUT,  /cache/key1/valueA",
        "PUT,  /cache/key2/valueB",

        "GET, /fenced-lock-cache/key1",
        "GET, /fenced-lock-cache/key2",
        "PUT, /fenced-lock-cache/key1/valueC",
        "PUT, /fenced-lock-cache/key2/valueD",
    })
    void proxyAsIs(String method, String path) {
        //@formatter:off
        when()
            .request(method, path)
        .then()
            .statusCode(200)
            .body(is("Success Wiremock Hit"));
        //@formatter:on

        verify(
            new RequestPatternBuilder(new RequestMethod(method), urlEqualTo(path))
        );
    }

    @ParameterizedTest
    @CsvSource({
        "cache,        key100, value100",
        "locked-cache, key200, value200",
    })
    void putAndGetCachedValue(String cache, String key, String value) {
        //@formatter:off
        given()
            .pathParam("cache", cache)
            .pathParam("key", key)
            .pathParam("value", value)
        .when()
            .put("/{cache}/{key}/{value}")
        .then()
            .statusCode(200)
            .body(notNullValue());

        given()
            .pathParam("cache", cache)
            .pathParam("key", key)
        .when()
            .get("/{cache}/{key}")
        .then()
            .statusCode(200)
            .body(is(value));
        //@formatter:on
    }

}
