package com.example.doannhom;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    long insert(TransactionEntity transaction);

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<TransactionEntity> getAllTransactions();

    @Query("SELECT COUNT(*) FROM transactions")
    int getCount();

    // Tìm kiếm giao dịch theo ghi chú hoặc danh mục
    @Query("SELECT * FROM transactions WHERE note LIKE '%' || :keyword || '%' OR category LIKE '%' || :keyword || '%' ORDER BY date DESC")
    List<TransactionEntity> searchTransactions(String keyword);
}
