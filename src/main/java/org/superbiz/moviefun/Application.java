package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials VCAPdatabase(@Value("${VCAP_SERVICES}") String VCAP_SERVICES){
        return  new DatabaseServiceCredentials(VCAP_SERVICES);
    }

    @Bean
    public HibernateJpaVendorAdapter createEntitiyManager(){
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setGenerateDdl(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        return  adapter;
    }


    @Bean
    public HikariDataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(serviceCredentials.jdbcUrl("albums-mysql"));
        return dataSource;
    }

    @Bean
    public HikariDataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(serviceCredentials.jdbcUrl("movies-mysql"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumcontainerEntitiyManager(HibernateJpaVendorAdapter createEntitiyManager, HikariDataSource albumsDataSource){
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(albumsDataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(createEntitiyManager);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("albumName");
        return localContainerEntityManagerFactoryBean;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean moviecontainerEntitiyManager(HibernateJpaVendorAdapter createEntitiyManager, HikariDataSource moviesDataSource){
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(moviesDataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(createEntitiyManager);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("movieName");
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager moviePlatformTransactionManager(EntityManagerFactory moviecontainerEntitiyManager ){
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(moviecontainerEntitiyManager);
        return jpaTransactionManager;


    }

    @Bean
    public PlatformTransactionManager albumPlatformTransactionManager(EntityManagerFactory albumcontainerEntitiyManager ){
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(albumcontainerEntitiyManager);
        return jpaTransactionManager;


    }

}
