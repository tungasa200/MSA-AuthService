package com.yjmedia.yvisbig.baseauth.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Service DB DataSource 설정
 * MH_USERS 테이블 조회를 위한 별도 DB 연결
 *
 * 활성화: service.datasource.enabled=true
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "service.datasource.enabled", havingValue = "true", matchIfMissing = false)
@MapperScan(basePackages = "com.yjmedia.yvisbig.baseauth.module.auth",
        sqlSessionFactoryRef = "serviceSqlSessionFactory",
        annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class ServiceDataSourceConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean(name = "serviceDataSource")
    @ConfigurationProperties("service.datasource")
    public DataSource serviceDataSource() {
        log.info("Creating Service DataSource for MH_USERS access");
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "serviceSqlSessionFactory")
    public SqlSessionFactory serviceSqlSessionFactory(
            @Autowired @Qualifier("serviceDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(
                applicationContext.getResources("classpath:/mapper/auth/userlogin.xml"));
        sqlSessionFactoryBean.setConfigLocation(
                applicationContext.getResource("classpath:/mybatis/mybatis-config.xml"));
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);

        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "serviceSqlSession")
    public SqlSessionTemplate serviceSqlSessionTemplate(
            @Autowired @Qualifier("serviceSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}