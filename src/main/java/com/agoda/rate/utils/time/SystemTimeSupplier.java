package com.agoda.rate.utils.time;


public class SystemTimeSupplier implements TimeSupplier {

    @Override
    public long get() {
        return System.currentTimeMillis() / 1000L;
    }
}
