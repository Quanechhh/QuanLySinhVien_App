package com.example.qunlsinhvin;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText edtmalop, edttenlop, edtsiso;
    Button btnthem, btnxoa, btnsua;
    ListView LV;
    ArrayList<String> myList;
    ArrayAdapter<String> myAdapter;
    SQLiteDatabase myDatabase;
    String selectedMalop = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtmalop = findViewById(R.id.edtMaLop);
        edttenlop = findViewById(R.id.edtTenLop);
        edtsiso = findViewById(R.id.edtSiSo);
        btnthem = findViewById(R.id.btnThem);
        btnxoa = findViewById(R.id.btnXoa);
        btnsua = findViewById(R.id.btnSua);

        // Setup ListView
        LV = findViewById(R.id.lv);
        myList = new ArrayList<>();
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
        LV.setAdapter(myAdapter);

        // Create and open SQLite database
        myDatabase = openOrCreateDatabase("qlsinhvien.db", MODE_PRIVATE, null);

        // Create table if it doesn't exist
        try {
            String sql = "CREATE TABLE tbllop(malop TEXT primary key, tenlop TEXT, siso INTEGER)";
            myDatabase.execSQL(sql);
        } catch (Exception e) {
            Log.e("Error", "Table đã tồn tại");
        }

        // Fetch and display data from the database
        fetchDataAndDisplay();

        LV.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = myList.get(position);
            String[] parts = selectedItem.split(", ");
            selectedMalop = parts[0].split(": ")[1];
            String tenlop = parts[1].split(": ")[1];
            int siso = Integer.parseInt(parts[2].split(": ")[1]);

            edtmalop.setText(selectedMalop);
            edttenlop.setText(tenlop);
            edtsiso.setText(String.valueOf(siso));
        });

        btnthem.setOnClickListener(view -> {
            String malop = edtmalop.getText().toString();
            String tenlop = edttenlop.getText().toString();
            int siso = Integer.parseInt(edtsiso.getText().toString());

            // Kiểm tra nếu malop đã tồn tại trong cơ sở dữ liệu
            Cursor cursor = myDatabase.rawQuery("SELECT * FROM tbllop WHERE malop = ?", new String[]{malop});
            if (cursor.moveToFirst()) {
                // malop đã tồn tại
                Toast.makeText(MainActivity.this, "Mã lớp đã tồn tại, không thể thêm", Toast.LENGTH_SHORT).show();
            } else {
                // Tiến hành thêm mới
                ContentValues myValue = new ContentValues();
                myValue.put("malop", malop);
                myValue.put("tenlop", tenlop);
                myValue.put("siso", siso);
                String msg = "";
                if (myDatabase.insert("tbllop", null, myValue) == -1) {
                    msg = "Thêm bị lỗi! Hãy thử lại.";
                } else {
                    msg = "Thêm thành công";
                    fetchDataAndDisplay(); // Cập nhật ListView sau khi thêm mới dữ liệu
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        });


        btnxoa.setOnClickListener(view -> {
            if (selectedMalop != null) {
                int n = myDatabase.delete("tbllop", "malop = ?", new String[]{selectedMalop});
                String msg = "";
                if (n == 0) {
                    msg = "Xóa không thành công";
                } else {
                    msg = n + " xóa thành công";
                    fetchDataAndDisplay(); // Update the ListView after deleting data
                    clearFields();
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Vui lòng chọn lớp để xóa", Toast.LENGTH_SHORT).show();
            }
        });

        btnsua.setOnClickListener(view -> {
            if (selectedMalop != null) {
                String newMalop = edtmalop.getText().toString();
                String tenlop = edttenlop.getText().toString();
                int siso = Integer.parseInt(edtsiso.getText().toString());

                // Kiểm tra nếu newMalop đã tồn tại trong cơ sở dữ liệu
                Cursor cursor = myDatabase.rawQuery("SELECT * FROM tbllop WHERE malop = ?", new String[]{newMalop});
                if (cursor.moveToFirst() && !newMalop.equals(selectedMalop)) {
                    // malop đã tồn tại và không phải là cái đang được chọn
                    Toast.makeText(MainActivity.this, "Mã lớp đã tồn tại, chỉnh sửa không thành công", Toast.LENGTH_SHORT).show();
                } else {
                    // Tiến hành cập nhật
                    ContentValues myValue = new ContentValues();
                    myValue.put("malop", newMalop);
                    myValue.put("tenlop", tenlop);
                    myValue.put("siso", siso);

                    int n = myDatabase.update("tbllop", myValue, "malop = ?", new String[]{selectedMalop});
                    String msg = "";
                    if (n == 0) {
                        msg = "Chỉnh sửa không thành công";
                    } else {
                        msg = n + " chỉnh sửa thành công";
                        fetchDataAndDisplay(); // Cập nhật ListView sau khi cập nhật dữ liệu
                        clearFields();
                    }
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            } else {
                Toast.makeText(MainActivity.this, "Vui lòng chọn lớp để chỉnh sửa", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void fetchDataAndDisplay() {
        myList.clear();
        Cursor cursor = myDatabase.rawQuery("SELECT * FROM tbllop", null);
        if (cursor.moveToFirst()) {
            do {
                String malop = cursor.getString(0);
                String tenlop = cursor.getString(1);
                int siso = cursor.getInt(2);
                myList.add("Mã lớp: " + malop + ", Tên lớp: " + tenlop + ", Sĩ số: " + siso);
            } while (cursor.moveToNext());
        }
        cursor.close();
        myAdapter.notifyDataSetChanged();
    }

    private void clearFields() {
        edtmalop.setText("");
        edttenlop.setText("");
        edtsiso.setText("");
        selectedMalop = null;
    }
}
