package bitxon.hz;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.ConcurrentMap;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
@Slf4j
@RestController
public class HzController {

    public static final String MAIN_CACHE = "main-cache";
    public static final String LOCKED_CACHE = "locked-cache";

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @GetMapping("/cache/{key}")
    public String get(@PathVariable("key") String key) {
        return getCache().get(key);
    }

    @PutMapping("/cache/{key}/{value}")
    public String put(@PathVariable("key") String key, @PathVariable("value") String value) {
        getCache().put(key, value);
        return value;
    }

    @SneakyThrows
    @GetMapping("/fenced-lock-cache/{key}")
    public String getWithLock(@PathVariable("key") String key) {
        log.info("GET {}", key);
        var lock = getLock(key);

        if (lock.tryLock(6, SECONDS)) {
            log.info("LOCK '{}' read", key);
            try {
                return getLockedCache().get(key);
            } finally {
                lock.unlock();
                log.info("UNLOCK '{}' read", key);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Lock not acquired");
        }
    }

    @SneakyThrows
    @PutMapping("/fenced-lock-cache/{key}/{value}")
    public String putWithLock(@PathVariable("key") String key, @PathVariable("value") String value) {
        log.info("PUT {}:{}", key, value);
        var lock = getLock(key);

        if (lock.tryLock(6, SECONDS)) {
            log.info("LOCK '{}' write", key);
            try {
                getLockedCache().put(key, "TEMP");
                SECONDS.sleep(5); // pretend that this is long operation
                getLockedCache().put(key, value);
                return value;
            } finally {
                lock.unlock();
                log.info("UNLOCK '{}' write", key);

            }
        } else {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Lock not acquired");
        }
    }


    private ConcurrentMap<String, String> getCache() {
        return hazelcastInstance.getMap(MAIN_CACHE);
    }

    private ConcurrentMap<String, String> getLockedCache() {
        return hazelcastInstance.getMap(LOCKED_CACHE);
    }

    private FencedLock getLock(String key) {
        return hazelcastInstance.getCPSubsystem().getLock(key);
    }

}
