
package com.example.doannhom;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public double amount;
    public String type;
    public String note;
    public String category;
    public long date;

    public TransactionEntity(
            double amount,
            String type,
            String note,
            String category,
            long date) {
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.category = category;
        this.date = date;
    }
}
