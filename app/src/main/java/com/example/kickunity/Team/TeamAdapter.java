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

    // 팀 상세 페이지로 이동할 때 호출될 리스너 인터페이스
    public interface OnDetailClickListener {
        void onDetailClick(Long teamId);
    }

    private List<TeamSummaryResponse> teamList = new ArrayList<>();
    private final OnDetailClickListener onDetailClickListener;

    // 생성자에서 리스너를 받음
    public TeamAdapter(OnDetailClickListener onDetailClickListener) {
        this.onDetailClickListener = onDetailClickListener;
    }

    // 팀 목록을 설정
    public void setTeamList(List<TeamSummaryResponse> teamList) {
        this.teamList = teamList;
        notifyDataSetChanged();  // RecyclerView 갱신
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 아이템 레이아웃을 인플레이트
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        TeamSummaryResponse team = teamList.get(position);

        // 팀 정보를 아이템에 바인딩
        holder.teamNameText.setText(team.getTeamName());
        holder.teamRegionField.setText(team.getTeamRegion());
        holder.categoryField.setText(team.getTeamCategory());

        // 버튼 클릭 시 팀 상세 페이지로 이동
        holder.detailButton.setOnClickListener(v -> {
            onDetailClickListener.onDetailClick(team.getId());  // 버튼 클릭 시 팀 상세 페이지로 이동
        });
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamNameText;
        TextView teamRegionField;
        TextView categoryField;
        Button detailButton;  // 팀 상세 페이지로 이동할 버튼

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);

            teamNameText = itemView.findViewById(R.id.teamNameText);
            teamRegionField = itemView.findViewById(R.id.activity_region_field);
            categoryField = itemView.findViewById(R.id.categoryfield);
            detailButton = itemView.findViewById(R.id.chatButton);  // 아이템 내 버튼을 찾음
        }
    }
}
