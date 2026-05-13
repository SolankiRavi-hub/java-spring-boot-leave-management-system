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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:mail.properties", ignoreResourceNotFound = true)
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
                "leave_type VARCHAR(50) DEFAULT 'Casual' NOT NULL," +
                "FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE" +
                ")"
            );

            statement.execute(
                "CREATE TABLE IF NOT EXISTS leave_quota (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "employee_id INT NOT NULL," +
                "leave_type VARCHAR(50) NOT NULL," +
                "total_days INT NOT NULL," +
                "used_days INT DEFAULT 0," +
                "remaining_days INT NOT NULL," +
                "year INT NOT NULL," +
                "UNIQUE KEY unique_quota (employee_id, leave_type, year)," +
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

            // Initialize quotas for all employees
            int currentYear = java.time.Year.now().getValue();
            try (PreparedStatement insertQuotas = connection.prepareStatement(
                "INSERT INTO leave_quota (employee_id, leave_type, total_days, used_days, remaining_days, year) " +
                "SELECT e.id, ?, ?, 0, ?, ? FROM employees e " +
                "WHERE NOT EXISTS (SELECT 1 FROM leave_quota lq WHERE lq.employee_id = e.id AND lq.leave_type = ? AND lq.year = ?)"
            )) {
                // Casual Leave
                insertQuotas.setString(1, "Casual");
                insertQuotas.setInt(2, 12);
                insertQuotas.setInt(3, 12);
                insertQuotas.setInt(4, currentYear);
                insertQuotas.setString(5, "Casual");
                insertQuotas.setInt(6, currentYear);
                insertQuotas.executeUpdate();

                // Sick Leave
                insertQuotas.setString(1, "Sick");
                insertQuotas.setInt(2, 7);
                insertQuotas.setInt(3, 7);
                insertQuotas.setInt(4, currentYear);
                insertQuotas.setString(5, "Sick");
                insertQuotas.setInt(6, currentYear);
                insertQuotas.executeUpdate();

                // Personal Leave
                insertQuotas.setString(1, "Personal");
                insertQuotas.setInt(2, 3);
                insertQuotas.setInt(3, 3);
                insertQuotas.setInt(4, currentYear);
                insertQuotas.setString(5, "Personal");
                insertQuotas.setInt(6, currentYear);
                insertQuotas.executeUpdate();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize schema", ex);
        }

        return new Object();
    }

    @Bean
    public JavaMailSender javaMailSender(Environment env) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // Priority: OS environment variables > mail.properties > sensible defaults
        String host = System.getenv("MAIL_HOST");
        if (host == null) host = env.getProperty("mail.host");
        String portStr = System.getenv("MAIL_PORT");
        if (portStr == null) portStr = env.getProperty("mail.port");
        String username = System.getenv("MAIL_USERNAME");
        if (username == null) username = env.getProperty("mail.username");
        String password = System.getenv("MAIL_PASSWORD");
        if (password == null) password = env.getProperty("mail.password");

        mailSender.setHost(host != null ? host : "localhost");
        try {
            mailSender.setPort(portStr != null ? Integer.parseInt(portStr) : 25);
        } catch (NumberFormatException ex) {
            mailSender.setPort(25);
        }

        if (username != null) {
            mailSender.setUsername(username);
        }
        if (password != null) {
            mailSender.setPassword(password);
        }

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        boolean auth = (username != null);
        props.put("mail.smtp.auth", auth ? "true" : "false");
        String starttls = System.getenv("MAIL_STARTTLS");
        if (starttls == null) starttls = env.getProperty("mail.starttls", "false");
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", "false");

        return mailSender;
    }
}
