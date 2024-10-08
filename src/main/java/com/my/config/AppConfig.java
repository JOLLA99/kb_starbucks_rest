package com.my.config;

import com.my.order.vo.OrderInfo;
import com.my.order.vo.OrderLine;
import com.my.customer.vo.Customer;
import com.my.product.vo.Product;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.my")
public class AppConfig {
    public AppConfig() {
        System.out.println("AppConfig created");
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
        config.setJdbcUrl("jdbc:log4jdbc:mysql://localhost:3306/shop?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8&useUnicode=true&allowMultiQueries=true");

        config.setUsername("root");
        config.setPassword("1234");

        config.setConnectionTimeout(30000);
        config.setMinimumIdle(3);
        config.setMaximumPoolSize(10);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(true);

        return new HikariDataSource(config);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        org.apache.ibatis.session.Configuration configuration = getConfiguration();
        sessionFactory.setConfiguration(configuration);

        Resource[] mapperLocations = new Resource[] {
                new ClassPathResource("mapper/ProductMapper.xml"),
                new ClassPathResource("mapper/CustomerMapper.xml"),
                new ClassPathResource("mapper/OrderMapper.xml")

        };
        sessionFactory.setMapperLocations(mapperLocations);

        return sessionFactory.getObject();
    }
    private org.apache.ibatis.session.Configuration getConfiguration() {
        org.apache.ibatis.session.Configuration configuration =
                new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.getTypeAliasRegistry().registerAlias("Product", Product.class);
        configuration.getTypeAliasRegistry().registerAlias("OrderLine", OrderLine.class);
        configuration.getTypeAliasRegistry().registerAlias("OrderInfo", OrderInfo.class);
        configuration.getTypeAliasRegistry().registerAlias("Customer", Customer.class);
       return configuration;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}


