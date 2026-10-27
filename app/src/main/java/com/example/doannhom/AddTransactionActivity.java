
package com.example.doannhom;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText edtAmount, edtNote;
    private RadioGroup rgType;
    private Spinner spCategory;
    private Button btnSave;

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        edtAmount = findViewById(R.id.edtAmount);
        edtNote = findViewById(R.id.edtNote);
        rgType = findViewById(R.id.rgType);

        RadioButton rbIncome = findViewById(R.id.rbIncome);
        RadioButton rbExpense = findViewById(R.id.rbExpense);

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbIncome) {
                // Thu nhập được chọn: xanh lá
                rbIncome.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#16A34A")));

                // Chi tiêu chưa chọn: xám
                rbExpense.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#F1F5F9")));

                rbIncome.setTextColor(
                        android.graphics.Color.WHITE);
                rbExpense.setTextColor(
                        android.graphics.Color.parseColor("#475569"));

            } else if (checkedId == R.id.rbExpense) {
                // Chi tiêu được chọn: đỏ
                rbExpense.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#DC2626")));

                // Thu nhập chưa chọn: xám
                rbIncome.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#F1F5F9")));

                rbExpense.setTextColor(
                        android.graphics.Color.WHITE);
                rbIncome.setTextColor(
                        android.graphics.Color.parseColor("#475569"));
            }
        });
        spCategory = findViewById(R.id.spCategory);
        btnSave = findViewById(R.id.btnSave);

        database = AppDatabase.getDatabase(this);


        // Danh mục mẫu để chọn
        String[] categories = {
                "Ăn uống",
                "Di chuyển",
                "Mua sắm",
                "Hóa đơn",
                "Lương",
                "Khác"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategory.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String amountText = edtAmount.getText().toString().trim();
        String note = edtNote.getText().toString().trim();

        // Kiểm tra số tiền
        if (amountText.isEmpty()) {
            edtAmount.setError("Vui lòng nhập số tiền");
            edtAmount.requestFocus();
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            edtAmount.setError("Số tiền không hợp lệ");
            return;
        }

        if (Double.isNaN(amount)
                || Double.isInfinite(amount)
                || amount <= 0) {
            edtAmount.setError("Số tiền phải lớn hơn 0");
            return;
        }

        // Xác định thu nhập hay chi tiêu
        int selectedId = rgType.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this,
                    "Vui lòng chọn loại giao dịch",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String type = selectedId == R.id.rbIncome
                ? "Thu nhập"
                : "Chi tiêu";

        String category = spCategory.getSelectedItem().toString();
        long date = System.currentTimeMillis();

        TransactionEntity transaction = new TransactionEntity(
                amount,
                type,
                note,
                category,
                date
        );

        btnSave.setEnabled(false);

        // Ghi dữ liệu vào Room ở luồng nền
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                database.transactionDao().insert(transaction);

                runOnUiThread(() -> {
                    Toast.makeText(
                            AddTransactionActivity.this,
                            "Lưu giao dịch thành công!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);

                    Toast.makeText(
                            AddTransactionActivity.this,
                            "Không lưu được giao dịch",
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }
}
