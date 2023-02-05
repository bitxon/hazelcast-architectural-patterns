package bitxon.hz;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.ConcurrentMap;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
        var lock = getLock(key);

        if (lock.tryLock(6, SECONDS)) {
            try {
                return getLockedCache().get(key);
            } finally {
                lock.unlock();
            }
        } else {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Lock not acquired");
        }
    }

    @SneakyThrows
    @PutMapping("/fenced-lock-cache/{key}/{value}")
    public String putWithLock(@PathVariable("key") String key, @PathVariable("value") String value) {
        var lock = getLock(key);

        if (lock.tryLock(6, SECONDS)) {
            try {
                getLockedCache().remove(key);
                SECONDS.sleep(5); // pretend that this is long operation
                getLockedCache().put(key, value);
                return value;
            } finally {
                lock.unlock();
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
