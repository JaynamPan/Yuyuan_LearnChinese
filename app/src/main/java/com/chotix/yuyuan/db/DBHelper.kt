package com.chotix.yuyuan.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

class DBHelper(private val mContext: Context) :
    SQLiteOpenHelper(mContext, DB_NAME, null, DB_VERSION) {
    companion object {
        private val DB_NAME: String = "test.db"
        private val DB_VERSION: Int = 1
    }

    init {
        createDatabase()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //do nothing
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //do nothing
    }

    private fun createDatabase() {
        val isDatabaseExist = checkDatabaseExist()
        if (!isDatabaseExist) {
            readableDatabase
            copyDatabase()
        }
    }

    private fun checkDatabaseExist(): Boolean {
        val dbFile = mContext.getDatabasePath(DB_NAME)
        return dbFile.exists()
    }

    private fun copyDatabase() {
        try {
            val inputStream = mContext.assets.open(DB_NAME)
            val outputStream = FileOutputStream(mContext.getDatabasePath(DB_NAME))
            val buffer = ByteArray(1024)
            var length: Int = 0
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}