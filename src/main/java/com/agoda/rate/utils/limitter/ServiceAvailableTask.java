package com.agoda.rate.utils.limitter;

import lombok.Setter;

import javax.xml.ws.Service;
import java.util.Map;
import java.util.TimerTask;

public class ServiceAvailableTask extends TimerTask {
    private final Map<String,Boolean> isPausing;
    private final String key;
    public ServiceAvailableTask(Map<String,Boolean> isPausing, String key){
        this.isPausing = isPausing;
        this.key = key;
    }
    @Override
    public void run() {
        System.out.println("unpause:"+key);
        isPausing.put(key,false);
    }
}
