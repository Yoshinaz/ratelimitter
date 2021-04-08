package com.agoda.rate.utils.limitter;

import lombok.Setter;

import javax.xml.ws.Service;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceAvailableTask extends TimerTask {
    private final Map<String,AtomicBoolean > isPausing;
    private final String key;
    public ServiceAvailableTask(Map<String, AtomicBoolean> isPausing, String key){
        this.isPausing = isPausing;
        this.key = key;
    }
    @Override
    public void run() {
        System.out.println("unpause:"+key);
        isPausing.put(key,new AtomicBoolean (false));
    }
}
