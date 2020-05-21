package com.miaxis.postal.data.dao;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.miaxis.postal.data.converter.DateConverter;
import com.miaxis.postal.data.converter.StringListConverter;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.util.FileUtil;

import java.io.File;

@Database(entities = {Config.class, Courier.class, IDCardRecord.class, Express.class}, version = 4)
@TypeConverters({StringListConverter.class, DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DBName = FileUtil.MAIN_PATH + File.separator + "Postal.db";

    private static AppDatabase instance;

    public static AppDatabase getInstance () {
        return instance;
    }

    //should be init first
    public static void initDB(Application application) {
        instance = createDB(application);
    }

    private static AppDatabase createDB(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, DBName)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                    }
                })
                .fallbackToDestructiveMigration()
                .build();
    }

    private static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//          旧表添加新的字段
//          database.execSQL("ALTER TABLE User " + " ADD COLUMN book_id TEXT");
//          创建新的数据表
//          database.execSQL("CREATE TABLE IF NOT EXISTS `book` (`book_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT)");
        }
    };

    public abstract ConfigDao configDao();

    public abstract CourierDao courierDao();

    public abstract IDCardRecordDao idCardRecordDao();

    public abstract ExpressDao expressDao();

}
