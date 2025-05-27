package com.example.networking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.networking.api.ApiConfig;
import com.example.networking.databinding.ActivityMainBinding;
import com.example.networking.model.AddMahasiswaResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAdd.setOnClickListener(view -> {
            addDataMahasiswa();
        });

        binding.btnList.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListMahasiswaActivity.class);
            startActivity(intent);
        });

        binding.btnSearch.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SearchMahasiswaActivity.class);
            startActivity(intent);
        });
    }

    private void addDataMahasiswa() {
        showLoading(true);
        String nrp = binding.edtNrp.getText().toString();
        String nama = binding.edtNama.getText().toString();
        String email = binding.edtEmail.getText().toString();
        String jurusan = binding.edtJurusan.getText().toString();

        if (nrp.isEmpty() || nama.isEmpty() || email.isEmpty() || jurusan.isEmpty()) {
            Toast.makeText(MainActivity.this, "Silahkan lengkapi form terlebih dahulu",
                    Toast.LENGTH_SHORT).show();
            showLoading(false);
        } else {
            Call<AddMahasiswaResponse> client = ApiConfig.getApiService().addMahasiswa(nrp, nama, email, jurusan);
            client.enqueue(new Callback<AddMahasiswaResponse>() {
                @Override
                public void onResponse(Call<AddMahasiswaResponse> call, Response<AddMahasiswaResponse> response) {
                    showLoading(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Toast.makeText(MainActivity.this,
                                    "Berhasil menambahkan, silahkan cek data pada halaman list!",
                                    Toast.LENGTH_SHORT).show();
                            clearForm();
                        }
                    } else {
                        if (response.body() != null) {
                            Log.e("", "onFailure: " + response.body().getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<AddMahasiswaResponse> call, Throwable t) {
                    showLoading(false);
                    Log.e("Error Retrofit", "onFailure: " + t.getMessage());
                }
            });
        }
    }

    private void showLoading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void clearForm() {
        binding.edtNrp.setText("");
        binding.edtNama.setText("");
        binding.edtEmail.setText("");
        binding.edtJurusan.setText("");
    }
}