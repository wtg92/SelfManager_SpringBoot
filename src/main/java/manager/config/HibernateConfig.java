package manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class HibernateConfig {
    @Autowired
    private DataSource dataSource;

    @Value("${hibernate.showSQL}")
    private Boolean showSQL;



    @Bean
    public void sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("manager.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());
//        return null;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.show_sql", showSQL);
        properties.put("hibernate.format_sql", showSQL);
        properties.put("hibernate.dialect","org.hibernate.dialect.MySQLDialect");
        return properties;
    }

}
