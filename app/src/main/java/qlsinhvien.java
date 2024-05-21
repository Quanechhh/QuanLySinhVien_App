import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class qlsinhvien extends SQLiteOpenHelper {
    public static final String TABLE_CLASS = "tbllop";
    private static final String DATABASE_NAME = "qlsinhvien.db";
    private static final int DATABASE_VERSION = 2;
    public static final String COLUMN_ID = "malop";
    public static final String COLUMN_NAME = "tenlop";
    public static final String COLUMN_NUMBER = "siso";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CLASS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_NUMBER + " TEXT " +
                    ");";

    public qlsinhvien(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        onCreate(db);
    }

}
