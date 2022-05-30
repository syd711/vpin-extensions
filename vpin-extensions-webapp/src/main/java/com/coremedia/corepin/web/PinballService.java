package com.coremedia.corepin.web;

import com.coremedia.corepin.web.tables.TableInfo;
import com.coremedia.corepin.web.tables.TableResolver;
import com.coremedia.corepin.web.util.PropertiesStore;
import com.coremedia.corepin.web.util.RomProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PinballService implements InitializingBean {
  private final static Logger LOG = LoggerFactory.getLogger(PinballService.class);
  public static final String TABLE_OF_THE_MONTH_ROM = "table.of.the.month.rom";

  @Value("${server.home}")
  private String serverHome;

  @Autowired
  private TableResolver tableResolver;

  public String getTableOfTheMonthRom() {
    return PropertiesStore.get(TABLE_OF_THE_MONTH_ROM);
  }

  public void setTableOfTheMonth(String rom) {
    PropertiesStore.set(TABLE_OF_THE_MONTH_ROM, rom);
  }

  public List<TableInfo> getTables() {
    return tableResolver.getTables();
  }

  @Override
  public void afterPropertiesSet() {
    PropertiesStore.init(serverHome);
    RomProperties.init(serverHome);

    List<TableInfo> tables = getTables();
    LOG.info("Pinball Service initialized with {} tables.", tables.size());
  }
}
