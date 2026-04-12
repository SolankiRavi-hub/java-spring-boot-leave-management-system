package com.leavemanagement.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = "com.leavemanagement")
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/leave_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        ds.setUsername("root");
        ds.setPassword("Ravi#7861");
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.leavemanagement.model");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");
        emf.setJpaProperties(properties);

        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Bean
    public Object schemaInitializer(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS employees (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) UNIQUE NOT NULL," +
                "department VARCHAR(50)," +
                "password VARCHAR(255) NOT NULL," +
                "role VARCHAR(20) DEFAULT 'Employee'" +
                ")"
            );

            statement.execute(
                "CREATE TABLE IF NOT EXISTS leave_requests (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "employee_id INT NOT NULL," +
                "start_date DATE NOT NULL," +
                "end_date DATE NOT NULL," +
                "reason VARCHAR(500)," +
                "status VARCHAR(50) DEFAULT 'Pending'," +
                "FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE" +
                ")"
            );

            try (PreparedStatement insertAdmin = connection.prepareStatement(
                "INSERT INTO employees (name, email, department, password, role) " +
                "SELECT ?, ?, ?, ?, ? FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM employees WHERE email = ?)"
            )) {
                insertAdmin.setString(1, "Admin User");
                insertAdmin.setString(2, "admin@company.com");
                insertAdmin.setString(3, "Management");
                insertAdmin.setString(4, "admin123");
                insertAdmin.setString(5, "Admin");
                insertAdmin.setString(6, "admin@company.com");
                insertAdmin.executeUpdate();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize schema", ex);
        }

        return new Object();
    }
}
