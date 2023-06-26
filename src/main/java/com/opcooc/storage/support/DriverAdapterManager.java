package com.opcooc.storage.support;

import com.opcooc.storage.adapter.DriverAdapter;
import com.opcooc.storage.context.DynamicDriverContext;
import com.opcooc.storage.converter.DriverAdapterConverter;
import com.opcooc.storage.exception.StorageException;
import com.opcooc.storage.provider.DriverPropertiesProvider;
import com.opcooc.storage.service.NFSService;
import com.opcooc.storage.spring.boot.autoconfigure.DriverProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
public class DriverAdapterManager implements DriverAdapter, InitializingBean, ApplicationContextAware {

    @Setter
    private String primary;
    @Setter
    private Boolean strict = false;
    @Getter
    private List<DriverPropertiesProvider> providers = new ArrayList<>();
    @Getter
    private List<DriverAdapterConverter> converters = new ArrayList<>();
    private ApplicationContext applicationContext;
    private final Map<String, DriverAdapter> clientDriverMap = new LinkedHashMap<>();

    public DriverAdapter determineDriver() {
        return getClientDriver(DynamicDriverContext.peek());
    }

    private DriverAdapter determinePrimaryClientDriver() {
        log.debug("opcooc-storage - switch to the primary client driver");
        return clientDriverMap.get(primary);
    }

    /**
     * 获取当前所有的驱动
     *
     * @return 当前所有驱动
     */
    public Map<String, DriverAdapter> getCurrentClientDrivers() {
        return clientDriverMap;
    }

    /**
     * 获取驱动
     *
     * @param driver 驱动名称
     * @return 驱动
     */
    public DriverAdapter getClientDriver(String driver) {
        if (ObjectUtils.isEmpty(driver)) {
            return determinePrimaryClientDriver();
        } else if (clientDriverMap.containsKey(driver)) {
            log.debug("opcooc-storage - switch to the client driver named [{}]", driver);
            return clientDriverMap.get(driver);
        }
        if (Boolean.TRUE.equals(strict)) {
            throw new StorageException("could not find a client driver named" + driver);
        }
        return determinePrimaryClientDriver();
    }

    /**
     * 添加驱动
     *
     * @param driver        驱动名称
     * @param driverAdapter 驱动
     */
    public synchronized void addClientDriver(String driver, DriverAdapter driverAdapter) {
        if (!clientDriverMap.containsKey(driver)) {
            clientDriverMap.put(driver, driverAdapter);
            log.info("opcooc-storage - load a client driver named [{}] success", driver);
        } else {
            log.warn("opcooc-storage - load a client driver named [{}] failed, because it already exist", driver);
        }
    }

    /**
     * 删除驱动
     *
     * @param driver 驱动名称
     */
    public synchronized void removeClientDriver(String driver) {
        if (!StringUtils.hasText(driver)) {
            throw new StorageException("remove parameter could not be empty");
        }
        if (Boolean.TRUE.equals(strict) && primary.equals(driver)) {
            throw new StorageException("could not remove primary client driver");
        }
        if (clientDriverMap.containsKey(driver)) {
            DriverAdapter driverAdapter = clientDriverMap.get(driver);
            try {
                driverAdapter.close();
            } catch (Exception e) {
                log.error("opcooc-storage - remove the client driver named [{}]  failed", driver, e);
            }
            clientDriverMap.remove(driver);
            log.info("opcooc-storage - remove the client driver named [{}] success", driver);
        } else {
            log.warn("opcooc-storage - could not find a client driver named [{}]", driver);
        }
    }

    @Override
    public void close() throws IOException {
        log.info("opcooc-storage - start closing ....");
        for (Map.Entry<String, DriverAdapter> item : clientDriverMap.entrySet()) {
            log.info("opcooc-storage - closed {}", item.getKey());
            item.getValue().close();
        }
        log.info("opcooc-storage - all closed success,bye");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, DriverPropertiesProvider> adapterProviders = applicationContext.getBeansOfType(DriverPropertiesProvider.class);
        providers.addAll(adapterProviders.values());

        Map<String, DriverAdapterConverter> driverAdapterConverters = applicationContext.getBeansOfType(DriverAdapterConverter.class);
        converters.addAll(driverAdapterConverters.values());

        for (Map.Entry<String, DriverAdapter> item : loadDriverAdapter().entrySet()) {
            addClientDriver(item.getKey(), item.getValue());
        }
        // 检测默认驱动是否设置
        if (Boolean.FALSE.equals(strict) || clientDriverMap.containsKey(primary)) {
            log.info("opcooc-storage - initial loaded [{}] client driver,primary client driver named [{}]", clientDriverMap.size(), primary);
        } else {
            throw new StorageException("please check the setting of primary");
        }
    }

    private Map<String, DriverAdapter> loadDriverAdapter() {
        Map<String, DriverAdapter> result = new HashMap<>();
        for (DriverPropertiesProvider provider : getProviders()) {
            Map<String, DriverProperties> driverPropertiesMap = provider.loadProperties();
            if (CollectionUtils.isEmpty(driverPropertiesMap)) {
                continue;
            }
            for (Map.Entry<String, DriverProperties> item : driverPropertiesMap.entrySet()) {
                String driverName = item.getKey();
                DriverProperties driverProperties = item.getValue();
                DriverAdapter driver = convert(driverName, driverProperties);
                result.put(driverName, driver);
            }
        }
        return result;
    }

    private DriverAdapter convert(String driver, DriverProperties properties) {
        Assert.notNull(properties, "properties cannot be null");
        for (DriverAdapterConverter converter : this.getConverters()) {
            DriverAdapter authentication = converter.convert(driver, properties);
            if (authentication != null) {
                return authentication;
            }
        }
        return null;
    }

    @Override
    public NFSService connect() {
        return determineDriver().connect();
    }

    @Override
    public DriverProperties configuration() {
        return determineDriver().configuration();
    }
}
