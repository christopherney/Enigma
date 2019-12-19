package com.android.launcher3.model;

public class GridBackupTable {

    private final SQLiteDatabase mDb;
    private int mRestoredHotseatSize;
    private int mRestoredGridX;
    private int mRestoredGridY;

    private boolean loadDbProperties() {
        try (Cursor c = mDb.query(BACKUP_TABLE_NAME, new String[] {
                        KEY_DB_VERSION,     // 0
                        KEY_GRID_X_SIZE,    // 1
                        KEY_GRID_Y_SIZE,    // 2
                        KEY_HOTSEAT_SIZE},  // 3
                "_id=" + ID_PROPERTY, null, null, null, null)) {
            if (!c.moveToNext()) {
                Log.e(TAG, "Meta data not found in backup table");
                return false;
            }
            if (mDb.getVersion() != c.getInt(0)) {
                return false;
            }

            mRestoredGridX = c.getInt(1);
            mRestoredGridY = c.getInt(2);
            mRestoredHotseatSize = c.getInt(3);
            return true;
        }
    }

    private static IconShape getShapeDefinition(String type, float radius) {
        switch (type) {
            case "Circle":
                return new Circle();
            case "RoundedSquare":
                return new RoundedSquare(radius);
            case "TearDrop":
                return new TearDrop(radius);
            case "Squircle":
                return new Squircle(radius);
            default:
                throw new IllegalArgumentException("Invalid shape type: " + type);
        }
    }
}