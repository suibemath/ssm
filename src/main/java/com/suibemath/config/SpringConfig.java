package com.suibemath.config;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.FileUrlResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

/**
 * @author crt1314
 * @version 1.0.0
 *
 * Spring配置类
 * 注解扫描com.suibemath.service和com.suibemath.aspect包
 * 支持事务、AOP
 */
@Configuration
@ComponentScan({"com.suibemath.service", "com.suibemath.aspect"})
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class SpringConfig {

    /**
     * 配置Druid数据源
     * @return Druid数据源
     */
    @Bean
    public DataSource dataSource() {
        DataSource dataSource = null;
        try (InputStream is = SpringConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initial DataSource", e);
        }
        return dataSource;
    }

    /**
     * 配置事务管理器
     * @param dataSource 数据源
     * @return 事务管理器
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 配置MyBatis工厂
     * @param dataSource 数据源
     * @return MyBatis工厂
     */
    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        URL configLocation = SpringConfig.class.getClassLoader().getResource("mybatis-config.xml");
        sqlSessionFactoryBean.setConfigLocation(new FileUrlResource(Objects.requireNonNull(configLocation)));
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }

    /**
     * 配置Mapper扫描器
     * 扫描com.suibemath.mapper包
     * @return Mapper扫描器
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.suibemath.mapper");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return mapperScannerConfigurer;
    }
}
