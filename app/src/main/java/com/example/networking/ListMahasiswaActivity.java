package com.example.networking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.networking.adapter.MahasiswaAdapter;
import com.example.networking.api.ApiConfig;
import com.example.networking.databinding.ActivityListMahasiswaBinding;
import com.example.networking.model.Mahasiswa;
import com.example.networking.model.MahasiswaResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListMahasiswaActivity extends AppCompatActivity {
    private ActivityListMahasiswaBinding binding;
    private MahasiswaAdapter adapter;
    private List<Mahasiswa> mahasiswaList = new ArrayList<>();
    private ActivityResultLauncher<Intent> updateLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListMahasiswaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        updateLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        getAllMahasiswa();
                    }
                }
        );

        adapter = new MahasiswaAdapter(mahasiswaList, this, updateLauncher);
        binding.rvMahasiswa.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMahasiswa.setAdapter(adapter);

        getAllMahasiswa();
    }

    private void getAllMahasiswa() {
        showLoading(true);
        Call<MahasiswaResponse> client = ApiConfig.getApiService().getAllMahasiswa();
        client.enqueue(new Callback<MahasiswaResponse>() {
            @Override
            public void onResponse(Call<MahasiswaResponse> call, Response<MahasiswaResponse> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        mahasiswaList.clear();
                        mahasiswaList.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
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
                Toast.makeText(ListMahasiswaActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllMahasiswa();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getAllMahasiswa();
        }
    }
}