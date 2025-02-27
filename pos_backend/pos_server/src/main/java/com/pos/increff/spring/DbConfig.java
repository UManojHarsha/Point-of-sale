package com.pos.increff.spring;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.util.Properties;

@EnableTransactionManagement
@Configuration
public class DbConfig {

	@Autowired
	private ApplicationProperties applicationConfig;

	public static final String PACKAGE_POJO = "com.pos.increff.pojo";

	@Bean(name = "dataSource")
	public DataSource getDataSource() {
		BasicDataSource bean = new BasicDataSource();
		bean.setDriverClassName(applicationConfig.getJdbcDriver());
		bean.setUrl(applicationConfig.getJdbcUrl() + "?serverTimezone=UTC");
		bean.setUsername(applicationConfig.getJdbcUsername());
		bean.setPassword(applicationConfig.getJdbcPassword());
		bean.setInitialSize(2);
		bean.setDefaultAutoCommit(false);
		bean.setMinIdle(2);
		bean.setValidationQuery("Select 1");
		bean.setTestWhileIdle(true);
		bean.setTimeBetweenEvictionRunsMillis(10 * 60 * 100);
		return bean;
	}

	@Bean(name = "entityManagerFactory")
	@Autowired
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource);
		bean.setPackagesToScan(PACKAGE_POJO);
		HibernateJpaVendorAdapter jpaAdapter = new HibernateJpaVendorAdapter();
		bean.setJpaVendorAdapter(jpaAdapter);
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
		jpaProperties.put("hibernate.show_sql", "false");
		jpaProperties.put("hibernate.hbm2ddl.auto", "update");
		jpaProperties.put("hibernate.jdbc.time_zone", "UTC");
		jpaProperties.put("hibernate.physical_naming_strategy", "com.pos.increff.util.ImprovedNamingStrategy");
		jpaProperties.put("hibernate.implicit_naming_strategy", "org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl");
		bean.setJpaProperties(jpaProperties);
		return bean;
	}
	
	@Bean(name = "transactionManager")
	@Autowired
	public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {
		JpaTransactionManager bean = new JpaTransactionManager();
		bean.setEntityManagerFactory(emf.getObject());
		return bean;
	}
}
