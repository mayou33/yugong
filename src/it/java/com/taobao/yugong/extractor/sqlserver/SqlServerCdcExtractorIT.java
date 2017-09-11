package com.taobao.yugong.extractor.sqlserver;

import com.google.common.collect.Lists;
import com.taobao.yugong.BaseDbIT;
import com.taobao.yugong.common.db.DataSourceFactory;
import com.taobao.yugong.common.db.meta.Table;
import com.taobao.yugong.common.db.meta.TableMetaGenerator;
import com.taobao.yugong.common.model.DbType;
import com.taobao.yugong.common.model.YuGongContext;
import com.taobao.yugong.common.model.record.SqlServerIncrementRecord;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sql.DataSource;

public class SqlServerCdcExtractorIT extends BaseDbIT {

  public static final String SOURCE_SCHEMA = "HJ_Test3D";
  public static final String SOURCE_TABLE = "fruits";

  @Test
  public void fetchCdcRecord() throws Exception {
    YuGongContext context = new YuGongContext();
    DataSourceFactory dataSourceFactory = new DataSourceFactory();
    dataSourceFactory.start();
    DataSource dataSource = dataSourceFactory.getDataSource(getSqlServerConfig());
    Table tableMeta = TableMetaGenerator.getTableMeta(DbType.SQL_SERVER, dataSource, SOURCE_SCHEMA,
        SOURCE_TABLE);

    context.setTableMeta(tableMeta);
    context.setSourceDs(dataSource);
    context.setOnceCrawNum(200);

    SqlServerCdcExtractor extractor = new SqlServerCdcExtractor(context);
    
    JdbcTemplate jdbcTemplate = new JdbcTemplate(context.getSourceDs());
    List<SqlServerIncrementRecord> records = extractor.fetchCdcRecord(
        jdbcTemplate, Lists.newArrayList(), Lists.newArrayList());
    Assert.assertTrue(records.size() > 5);

    dataSourceFactory.stop();
  }

}