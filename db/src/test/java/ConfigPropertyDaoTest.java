import com.gps.itunes.media.player.db.ConfigPropertyDao;
import com.gps.itunes.media.player.db.DbManager;
import com.gps.itunes.media.player.db.DbManagerImpl;
import com.gps.itunes.media.player.db.model.ConfigProperty;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.SQLException;

/**
 * Created by leogps on 12/08/2018.
 */
@Test
public class ConfigPropertyDaoTest {

    private DbManager dbManager;

    @BeforeClass
    public void init() throws SQLException, ClassNotFoundException {
        dbManager = DbManagerImpl.getInstance();
        dbManager.initialize();
    }

    @Test
    public void insertTest() throws SQLException {
        ConfigPropertyDao configPropertyDao = new ConfigPropertyDao(dbManager.getConnection());

        configPropertyDao.deleteAll();

        ConfigProperty configProperty = new ConfigProperty();
        configProperty.setProperty("font_size");
        configProperty.setValue("24");

        ConfigProperty inserted = configPropertyDao.insert(configProperty);

        Assert.assertEquals(configProperty.getProperty(), inserted.getProperty());
        Assert.assertEquals(configProperty.getValue(), inserted.getValue());

        configPropertyDao.deleteAll();
    }

}
