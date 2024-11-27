package com.example.kickunity.Team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kickunity.R;

import java.util.ArrayList;
import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    public interface OnJoinClickListener {
        void onJoinClick(Long teamId);
    }

    private List<TeamSummaryResponse> teamList = new ArrayList<>();
    private final OnJoinClickListener onJoinClickListener;

    public TeamAdapter(OnJoinClickListener onJoinClickListener) {
        this.onJoinClickListener = onJoinClickListener;
    }

    public void setTeamList(List<TeamSummaryResponse> teamList) {
        this.teamList = teamList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        TeamSummaryResponse team = teamList.get(position);
        holder.teamNameText.setText(team.getTeamName());
        holder.teamRegionField.setText(team.getTeamRegion());
        holder.categoryField.setText(team.getTeamCategory());

        holder.joinButton.setOnClickListener(v -> onJoinClickListener.onJoinClick(team.getId()));
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamNameText;
        TextView teamRegionField;
        TextView categoryField;
        Button joinButton;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            teamNameText = itemView.findViewById(R.id.teamNameText);
            teamRegionField = itemView.findViewById(R.id.activity_region_field);
            categoryField = itemView.findViewById(R.id.categoryfield);
            joinButton = itemView.findViewById(R.id.joinRequest);
        }
    }
}
