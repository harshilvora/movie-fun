package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateScheduler {

    private static final long SECONDS = 1000;
    private static final long MINUTES = 60 * SECONDS;

    private DataSource dataSource;

    private final AlbumsUpdater albumsUpdater;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AlbumsUpdateScheduler(AlbumsUpdater albumsUpdater, DataSource dataSource) {
        this.albumsUpdater = albumsUpdater;
        this.dataSource = dataSource;
    }

    private final RowMapper<Date> mapper = (rs, rowNum) -> rs.getDate("started_at");

    private final ResultSetExtractor<Date> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;


    @Scheduled(initialDelay = 15 * SECONDS, fixedRate = 2 * MINUTES)
    public void run() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        try {
            logger.debug("Starting albums update");

            /*Date date = jdbcTemplate.query("Select * from album_scheduler_task",extractor);
            Date current = new Date();
            long diff = current.getTime() - date.getTime();
            long diffMinutes = diff / (60 * 1000);
            long diffSeconds = diff / 1000;
            if(diffMinutes >= 2)
                albumsUpdater.update();
            else
                logger.debug("Still Waiting, time left: {}",diffSeconds);*/
            if (startAlbumSchedulerTask()) {

                albumsUpdater.update();
                logger.debug("Finished albums update");

            } else {
                logger.debug("Nothing to start");
            }

            logger.debug("Finished albums update");

        } catch (Throwable e) {
            logger.error("Error while updating albums", e);
        }
    }

    private boolean startAlbumSchedulerTask() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int updatedRows = jdbcTemplate.update(
                "UPDATE album_scheduler_task" +
                        " SET started_at = now()" +
                        " WHERE started_at IS NULL" +
                        " OR started_at < date_sub(now(), INTERVAL 2 MINUTE)"
        );

        return updatedRows > 0;
    }
}
