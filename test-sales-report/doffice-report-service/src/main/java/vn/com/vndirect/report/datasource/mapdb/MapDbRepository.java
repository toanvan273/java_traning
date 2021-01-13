package vn.com.vndirect.report.datasource.mapdb;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class MapDbRepository<T extends Serializable> implements DbStorageRepository<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapDbRepository.class);

    private DB db;

    private String fileDbFolderPath;

    private String fileDbName;

    public MapDbRepository(Environment env, String fileDbFolder, String fileDbName, boolean isNeedCleanOnStart) {
        String currentDir = env.getProperty("user.dir");
        this.fileDbFolderPath = currentDir + File.separator + "temp" + File.separator + fileDbFolder;
        this.fileDbName = fileDbName;
        File dbFolder = new File(fileDbFolderPath);
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }
        if (isNeedCleanOnStart) {
            File[] dbFiles = dbFolder.listFiles();
            for (File f : dbFiles) {
                boolean b = f.delete();
                LOGGER.info("Delete db file {}, deleted ? {}", f.getName(), b);
            }
        }
        this.db = DBMaker.fileDB(fileDbFolderPath + File.separator + fileDbName)
                .fileChannelEnable()
                .checksumHeaderBypass()
                .fileDeleteAfterClose()
                .closeOnJvmShutdown()
                .make();
    }

    public MapDbRepository() {
        this.db = DBMaker.memoryDB().closeOnJvmShutdown().make();
    }

    @Override
    public T add(@NotNull String mapName, @NotNull String key, @NotNull T value) throws Exception {
        return creatOrOpen(mapName).put(key, value);
    }

    @Override
    public T update(@NotNull String mapName, @NotNull String key, @NotNull T newValue) throws RuntimeException {
        HTreeMap<String, T> map = creatOrOpen(mapName);
        if (!map.containsKey(key)) throw new RuntimeException("Cant update cause dont have key: " + key);
        return map.replace(key, newValue);
    }

    @Override
    public boolean delete(@NotNull String mapName, @NotNull String key) throws Exception {
        return creatOrOpen(mapName).remove(mapName, key);
    }

    @Override
    public boolean delete(@NotNull String mapName, @NotEmpty List<String> keys) throws Exception {
        for (String key : keys) {
            if (!delete(mapName, key)) return false;
        }
        return true;
    }

    @Override
    public boolean creatOrReplace() {
        try {
            this.db.close();
            File dbFolder = new File(fileDbFolderPath);
            if (!dbFolder.exists()) {
                dbFolder.mkdirs();
            } else {
                File[] dbFiles = dbFolder.listFiles();
                for (File f : dbFiles) {
                    boolean b = f.delete();
                    LOGGER.info("Delete db file {}, deleted ? {}", f.getName(), b);
                }
            }
            this.db = DBMaker.fileDB(fileDbFolderPath + File.separator + fileDbName)
                    .fileChannelEnable()
                    .checksumHeaderBypass()
                    .fileDeleteAfterClose()
                    .closeOnJvmShutdown()
                    .make();
            return true;
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return false;
        }
    }

    @Override
    public boolean contains(@NotNull String mapName, @NotNull String key) throws Exception {
        return creatOrOpen(mapName).containsKey(key);
    }

    @Override
    public Optional<T> findOne(@NotNull String mapName, @NotNull String key) throws Exception {
        return Optional.ofNullable((T) creatOrOpen(mapName).get(key));
    }

    @Override
    public HTreeMap<String, T> findAll(@NotNull String mapName) throws Exception {
        return creatOrOpen(mapName);
    }

    @Override
    public long countAll(@NotNull String mapName) throws Exception {
        return creatOrOpen(mapName).sizeLong();
    }

    public Iterable<String> listAllMapDbNames() throws Exception {
        return this.db.getAll().keySet();
    }

    private HTreeMap<String, T> creatOrOpen(@NotNull String mapName) {
        DB.HashMapMaker<String, T> hashMapMaker = this.db.hashMap(mapName)
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.JAVA);
        if (!this.db.exists(mapName)) {
            return hashMapMaker.create();
        }
        return hashMapMaker.open();
    }
}