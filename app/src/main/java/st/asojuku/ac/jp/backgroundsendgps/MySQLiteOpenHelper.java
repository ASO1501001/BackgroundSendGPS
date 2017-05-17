package st.asojuku.ac.jp.backgroundsendgps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Itchy on 2017/05/17.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DB = "local.db";
    private static final String CREATE_TABLE = "CREATE TABLE localtbl(localname varchar(30) PRIMARY KEY,localmember varchar(30) NOT NULL);";
    private static final String DROP_TABLE = "DROP TABLE localtbl";

    public MySQLiteOpenHelper(Context context){
        super(context, DB, null, 1);

    }

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}

