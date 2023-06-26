package com.opcooc.storage.converter;

import com.opcooc.storage.adapter.DriverAdapter;
import com.opcooc.storage.adapter.S3DriverAdapter;
import com.opcooc.storage.constant.DriverType;
import com.opcooc.storage.spring.boot.autoconfigure.DriverProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3DriverAdapterConverter implements DriverAdapterConverter {

    @Override
    public DriverAdapter convert(String driver, DriverProperties properties) {
        if (!DriverType.S3.equals(properties.getType())) {
            return null;
        }
        DriverAdapter s3DriverAdapter = new S3DriverAdapter(driver, properties);
        log.info("opcooc-storage - s3 driver adapter instantiate success.");
        return s3DriverAdapter;
    }
}
