package com.example.networking;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.networking.api.ApiConfig;
import com.example.networking.databinding.ActivityUpdateMahasiswaBinding;
import com.example.networking.model.AddMahasiswaResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateMahasiswaActivity extends AppCompatActivity {
    private ActivityUpdateMahasiswaBinding binding;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateMahasiswaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        id = getIntent().getStringExtra("id");
        binding.edtNrp.setText(getIntent().getStringExtra("nrp"));
        binding.edtNama.setText(getIntent().getStringExtra("nama"));
        binding.edtEmail.setText(getIntent().getStringExtra("email"));
        binding.edtJurusan.setText(getIntent().getStringExtra("jurusan"));

        binding.btnUpdate.setOnClickListener(view -> {
            updateDataMahasiswa();
        });
    }

    private void updateDataMahasiswa() {
        showLoading(true);
        String nrp = binding.edtNrp.getText().toString();
        String nama = binding.edtNama.getText().toString();
        String email = binding.edtEmail.getText().toString();
        String jurusan = binding.edtJurusan.getText().toString();

        if (nrp.isEmpty() || nama.isEmpty() || email.isEmpty() || jurusan.isEmpty()) {
            Toast.makeText(this, "Silahkan lengkapi form terlebih dahulu",
                    Toast.LENGTH_SHORT).show();
            showLoading(false);
        } else {
            Call<AddMahasiswaResponse> client = ApiConfig.getApiService()
                    .updateMahasiswa(id, nrp, nama, email, jurusan);
            client.enqueue(new Callback<AddMahasiswaResponse>() {
                @Override
                public void onResponse(Call<AddMahasiswaResponse> call,
                                       Response<AddMahasiswaResponse> response) {
                    showLoading(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().isStatus()) {
                                Toast.makeText(UpdateMahasiswaActivity.this,
                                        "Update berhasil",
                                        Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(UpdateMahasiswaActivity.this,
                                        response.body().getMessage() != null ?
                                                response.body().getMessage() : "Update gagal",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UpdateMahasiswaActivity.this,
                                    "Response body kosong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UpdateMahasiswaActivity.this,
                                "Error: " + response.message(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AddMahasiswaResponse> call, Throwable t) {
                    showLoading(false);
                    Toast.makeText(UpdateMahasiswaActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
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
}