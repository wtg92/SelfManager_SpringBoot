package manager.config;

import manager.entity.general.FileRecord;
import manager.entity.general.SystemMapping;
import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.entity.general.career.*;
import manager.entity.general.tool.ToolRecord;
import manager.util.HibernateNamingStrategy;

import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import static org.hibernate.cfg.AvailableSettings.*;

@Configuration
public class HibernateConfig {

    private final DataSource dataSource;
    private SessionFactory sessionFactory;
    @Value("${hibernate.showSQL}")
    private Boolean showSQL;

    public HibernateConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public SessionFactory sessionFactory() {

        Properties props = new Properties();

        // ===== Hibernate 基础配置 =====
        props.put(DIALECT, "org.hibernate.dialect.MySQLDialect");
        props.put(SHOW_SQL, showSQL.toString());
        props.put(FORMAT_SQL, showSQL.toString());
        props.put(HIGHLIGHT_SQL, showSQL.toString());


        props.put("hibernate.connection.datasource", dataSource);

        org.hibernate.cfg.Configuration cfg =
                new org.hibernate.cfg.Configuration();

        cfg.setProperties(props);
        cfg.setPhysicalNamingStrategy(new HibernateNamingStrategy());

        cfg.addAnnotatedClass(User.class);
        cfg.addAnnotatedClass(UserGroup.class);
        cfg.addAnnotatedClass(Plan.class);
        cfg.addAnnotatedClass(WorkSheet.class);
        cfg.addAnnotatedClass(PlanBalance.class);
        cfg.addAnnotatedClass(NoteBook.class);
        cfg.addAnnotatedClass(Note.class);
        cfg.addAnnotatedClass(Memo.class);
        cfg.addAnnotatedClass(ToolRecord.class);
        cfg.addAnnotatedClass(FileRecord.class);
        cfg.addAnnotatedClass(SystemMapping.class);

        this.sessionFactory = cfg.buildSessionFactory();
        return this.sessionFactory;
    }

    // 🧹 应用关闭时正确释放
    @PreDestroy
    public void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
