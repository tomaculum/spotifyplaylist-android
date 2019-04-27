package com.example.spotifyplaylists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<PlaylistSimple> playlistList;

    public PlaylistAdapter(List<PlaylistSimple> playlistList, Context context) {
        this.playlistList = playlistList;
    }

    @Override
    public PlaylistAdapter.PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_row, parent,false);
        return new PlaylistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaylistAdapter.PlaylistViewHolder holder, int position) {
        holder.text.setText(playlistList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public void addItems(List<PlaylistSimple> playlistList) {
        this.playlistList.addAll(playlistList);
        this.notifyDataSetChanged();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder{

        protected TextView text;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.playlist_name);
        }
    }
}