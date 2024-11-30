package com.example.kickunity.Team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

    public interface OnItemClickListener {
        void onItemClick(Long teamId);
    }

    private List<TeamSummaryResponse> teamList = new ArrayList<>();
    private final OnJoinClickListener onJoinClickListener;
    private final OnItemClickListener onItemClickListener;

    public TeamAdapter(OnJoinClickListener onJoinClickListener, OnItemClickListener onItemClickListener) {
        this.onJoinClickListener = onJoinClickListener;
        this.onItemClickListener = onItemClickListener;
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

        // 버튼 클릭 리스너
        holder.joinButton.setOnClickListener(v -> onJoinClickListener.onJoinClick(team.getId()));

        // 아이템 클릭 리스너 (아이템 전체 클릭)
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(team.getId()));

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
        ImageView teamLogoImage;  // 추가: 팀 로고 이미지

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            teamNameText = itemView.findViewById(R.id.teamNameText);
            teamRegionField = itemView.findViewById(R.id.activity_region_field);
            categoryField = itemView.findViewById(R.id.categoryfield);
            joinButton = itemView.findViewById(R.id.joinRequest);
        }
    }
}
