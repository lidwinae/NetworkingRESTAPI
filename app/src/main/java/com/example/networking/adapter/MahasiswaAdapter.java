package com.example.networking.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import java.io.IOException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networking.R;
import com.example.networking.UpdateMahasiswaActivity;
import com.example.networking.api.ApiConfig;
import com.example.networking.databinding.ItemMahasiswaBinding;
import com.example.networking.model.AddMahasiswaResponse;
import com.example.networking.model.Mahasiswa;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.ViewHolder> {
    private List<Mahasiswa> mahasiswaList;
    private Context context;
    private final ActivityResultLauncher<Intent> updateLauncher;

    public MahasiswaAdapter(List<Mahasiswa> mahasiswaList, Context context, ActivityResultLauncher<Intent> updateLauncher) {
        this.mahasiswaList = mahasiswaList;
        this.context = context;
        this.updateLauncher = updateLauncher;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMahasiswaBinding binding = ItemMahasiswaBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mahasiswa mahasiswa = mahasiswaList.get(position);

        holder.binding.tvItemNrp.setText(mahasiswa.getNrp());
        holder.binding.tvItemNama.setText(mahasiswa.getNama());
        holder.binding.tvItemEmail.setText(mahasiswa.getEmail());
        holder.binding.tvItemJurusan.setText(mahasiswa.getJurusan());

        holder.binding.btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateMahasiswaActivity.class);
            intent.putExtra("id", mahasiswa.getId());
            intent.putExtra("nrp", mahasiswa.getNrp());
            intent.putExtra("nama", mahasiswa.getNama());
            intent.putExtra("email", mahasiswa.getEmail());
            intent.putExtra("jurusan", mahasiswa.getJurusan());
            ((Activity) context).startActivityForResult(intent, 1);
        });

        holder.binding.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Yakin ingin menghapus data ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        deleteMahasiswa(mahasiswa.getId(), position);
                    })
                    .setNegativeButton("Tidak", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return mahasiswaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMahasiswaBinding binding;

        public ViewHolder(@NonNull ItemMahasiswaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void deleteMahasiswa(String id, int position) {
        Call<AddMahasiswaResponse> client = ApiConfig.getApiService().deleteMahasiswa(id);
        client.enqueue(new Callback<AddMahasiswaResponse>() {
            @Override
            public void onResponse(Call<AddMahasiswaResponse> call, Response<AddMahasiswaResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        mahasiswaList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mahasiswaList.size());
                        Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Hapus berhasil tapi tidak ada response", Toast.LENGTH_SHORT).show();
                        mahasiswaList.remove(position);
                        notifyDataSetChanged();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "unknown error";
                        Toast.makeText(context, "Gagal menghapus: " + errorBody, Toast.LENGTH_SHORT).show();
                        Log.e("DELETE_ERROR", errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AddMahasiswaResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DELETE_FAILURE", t.getMessage());
            }
        });
    }
}