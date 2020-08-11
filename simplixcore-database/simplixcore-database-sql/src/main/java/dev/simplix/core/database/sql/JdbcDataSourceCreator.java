package dev.simplix.core.database.sql;

import com.mysql.cj.jdbc.MysqlDataSource;
import javax.sql.DataSource;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JdbcDataSourceCreator {

  public final String OPTIONS =
      "?jdbcCompliantTruncation=false&autoReconnect=true&serverTimezone=Europe/Berlin&zeroDateTimeBehavior=convertToNull"
      + "&max_allowed_packet=512M";

  public DataSource createSource(String host, String port, String data) {
    MysqlDataSource source = new MysqlDataSource();
    source.setUrl("jdbc:mysql://" + host + ":" + port + "/" + data + OPTIONS);
    return source;
  }
}
