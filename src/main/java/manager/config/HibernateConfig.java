package manager.config;

import manager.entity.general.FileRecord;
import manager.entity.general.SystemMapping;
import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.entity.general.career.*;
import manager.entity.general.tool.ToolRecord;
import manager.util.HibernateNamingStrategy;
import org.hibernate.SessionFactory;
import static org.hibernate.cfg.AvailableSettings.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO 还没有使用数据库连接池 ？？ 之前使用的是Druid
 */
@Configuration
public class HibernateConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;


    @Value("${hibernate.showSQL}")
    private Boolean showSQL;

    @Bean
    public SessionFactory sessionFactory() {
        try {
            return new org.hibernate.cfg.Configuration()

                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(UserGroup.class)
                    .addAnnotatedClass(Plan.class)
                    .addAnnotatedClass(WorkSheet.class)
                    .addAnnotatedClass(PlanBalance.class)
                    .addAnnotatedClass(NoteBook.class)
                    .addAnnotatedClass(Note.class)
                    .addAnnotatedClass(Memo.class)
                    .addAnnotatedClass(ToolRecord.class)
                    .addAnnotatedClass(FileRecord.class)
                    .addAnnotatedClass(SystemMapping.class)

                    .setProperty(JAKARTA_JDBC_URL, url)
                    .setProperty(JAKARTA_JDBC_USER, username)
                    .setProperty(JAKARTA_JDBC_PASSWORD, password)
                    .setProperty(JAKARTA_JDBC_DRIVER,driver)

                    .setPhysicalNamingStrategy(new HibernateNamingStrategy())
                    .setProperty("hibernate.c3p0.min_size", "1")
                    .setProperty("hibernate.c3p0.max_size", "100")
                    .setProperty("hibernate.c3p0.timeout","200")
                    .setProperty("hibernate.c3p0.max_statements", "100")
                    .setProperty("hibernate.c3p0.idle_test_period","5000")

                    .setProperty(DIALECT,"org.hibernate.dialect.MySQLDialect")
                    .setProperty(SHOW_SQL, showSQL.toString())
                    .setProperty(FORMAT_SQL, showSQL.toString())
                    .setProperty(HIGHLIGHT_SQL, showSQL.toString())
                    .buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
            throw new RuntimeException("There was an error building the factory...!");
        }

    }
}
