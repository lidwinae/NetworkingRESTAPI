package com.example.networking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.networking.api.ApiConfig;
import com.example.networking.databinding.ActivitySearchMahasiswaBinding;
import com.example.networking.model.Mahasiswa;
import com.example.networking.model.MahasiswaResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMahasiswaActivity extends AppCompatActivity {
    private ActivitySearchMahasiswaBinding binding;
    private List<Mahasiswa> mahasiswaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchMahasiswaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mahasiswaList = new ArrayList<>();

        binding.btnSearch.setOnClickListener(view -> {
            showLoading(true);
            String nrp = binding.edtChckNrp.getText().toString();
            if (nrp.isEmpty()) {
                binding.edtChckNrp.setError("Silahkan Isi nrp terlebih dahulu");
                showLoading(false);
            } else {
                Call<MahasiswaResponse> client = ApiConfig.getApiService().getMahasiswa(nrp);
                client.enqueue(new Callback<MahasiswaResponse>() {
                    @Override
                    public void onResponse(Call<MahasiswaResponse> call, Response<MahasiswaResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                showLoading(false);
                                mahasiswaList = response.body().getData();
                                if (mahasiswaList != null && !mahasiswaList.isEmpty()) {
                                    setData(mahasiswaList);
                                } else {
                                    Toast.makeText(SearchMahasiswaActivity.this,
                                            "Data tidak ditemukan",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if (response.body() != null) {
                                Log.e("", "onFailure: " + response.message());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MahasiswaResponse> call, Throwable t) {
                        showLoading(false);
                        Log.e("Error Retrofit", "onFailure: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void setData(List<Mahasiswa> mahasiswaList) {
        binding.tvValNrp.setText(mahasiswaList.get(0).getNrp());
        binding.tvValNama.setText(mahasiswaList.get(0).getNama());
        binding.tvValEmail.setText(mahasiswaList.get(0).getEmail());
        binding.tvValJurusan.setText(mahasiswaList.get(0).getJurusan());
    }

    private void showLoading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}