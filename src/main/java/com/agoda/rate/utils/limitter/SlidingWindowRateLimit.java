package com.agoda.rate.utils.limitter;

import com.agoda.rate.config.RateLimitConfig;
import com.agoda.rate.utils.time.SystemTimeSupplier;
import com.agoda.rate.utils.time.TimeSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.concurrent.ThreadSafe;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@ThreadSafe
public class SlidingWindowRateLimit implements RateLimiter{
    private static final Logger LOG = LoggerFactory.getLogger(SlidingWindowRateLimit.class);

    private final Map<String,Boolean> isPausing;
    private final Map<String,LimitRule> rules;
    private final TimeSupplier timeSupplier;
    private final ConcurrentMap<String,ConcurrentMap<Long,Long>> requests;


    public SlidingWindowRateLimit(Map<String,LimitRule> rules){
        this.rules = rules;
        this.timeSupplier = new SystemTimeSupplier();
        this.requests = new ConcurrentHashMap<>();
        this.isPausing = new HashMap<>();
        for (Map.Entry<String,LimitRule> r : rules.entrySet()) {
            isPausing.put(r.getKey(),false);
        }
    }

    @Override
    public boolean isOverLimit(String key) {

        if(isPausing.get(key)){
            System.out.println(key+":pausing");
            return true;
        }
        final long currentTime = timeSupplier.get();
        LimitRule rule = getLimitRule(key);
        final long startTime = currentTime - rule.getDurationSeconds();
        final long nRequests = getCurrentWindows(key,startTime);

        if(nRequests > rule.getLimit()){
            System.out.println("over:"+nRequests);
            overLimitHandler(key);

            return true;
        }

        registerRequest(key,currentTime);

        return false;
    }

    private LimitRule getLimitRule(String key){
        LimitRule rule = rules.get(key);
        if(rule == null){
            rule = defaultConfig();
            rules.put(key,rule);
        }
        return rule;
    }

    private LimitRule defaultConfig(){
        return LimitRule.of(Duration.ofSeconds(10),50,Duration.ofSeconds(5));
    }

    private void overLimitHandler(String key){
        isPausing.put(key,true);
        final LimitRule rule = rules.get(key);
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(new ServiceAvailableTask(isPausing,key),rule.getStopDurationSeconds(), TimeUnit.SECONDS);
    }

    private long getCurrentWindows(String key,long startTime){
        long totalRequest =0L;
        ConcurrentMap<Long,Long> keyRequests = requests.get(key);
        if(keyRequests == null){
            return 0;
        }
        for (Map.Entry<Long,Long> r : keyRequests.entrySet()){
            if(r.getKey()>startTime){
                totalRequest+=r.getValue();
            }else{
                System.out.println("remove:"+r.getKey());
                requests.remove(r.getKey());
            }
        }
        return totalRequest;
    }

    private void registerRequest(String key,long time){
        ConcurrentMap<Long, Long> keyMap = requests.get(key);
        if(keyMap != null){
            keyMap.put(time,keyMap.get(time)==null?1: keyMap.get(time)+1);
            requests.put(key,keyMap);
        }else{
            ConcurrentHashMap<Long,Long> entry = new ConcurrentHashMap<Long,Long>();
            entry.put(time,1L);
            requests.put(key,entry);
        }

    }


}
