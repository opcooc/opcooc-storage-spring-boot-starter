package com.opcooc.storage.converter;

import com.opcooc.storage.adapter.DriverAdapter;
import com.opcooc.storage.spring.boot.autoconfigure.DriverProperties;

@FunctionalInterface
public interface DriverAdapterConverter {

    DriverAdapter convert(String driver, DriverProperties properties);

}
