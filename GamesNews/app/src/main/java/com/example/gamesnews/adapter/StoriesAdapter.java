package com.example.gamesnews.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamesnews.R;
import com.example.gamesnews.activity.WebViewActivity;
import com.example.gamesnews.firestore.FavoriteCallback;
import com.example.gamesnews.firestore.NewsManager;
import com.example.gamesnews.model.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoryViewHolder> {

    private List<Story> stories;
    private Context context;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private OnFavoriteClickListener favoriteClickListener; // Ouvinte do clique no favorito

    public void setOnFavoriteClickListener(OnFavoriteClickListener favoriteClickListener) {
        this.favoriteClickListener = favoriteClickListener;
    }

    public StoriesAdapter(List<Story> stories, Context context) {
        this.stories = stories;
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = stories.get(position);

        // Configura o título da story
        holder.tvStoryTitle.setText(story.getTitle());

        // Configura o nome da fonte da story
        holder.tvStorySource.setText(story.getSource().getName());

        // Configura o tempo desde a postagem da story
        holder.tvStoryPostTime.setText(calculateRelativeTime(story.getDate()));

        Picasso.get().load(story.getSource().getIcon()).placeholder(R.drawable.image_placeholder)
                .error(R.drawable.error_image_placeholder).into(holder.ivStorySourceIcon);

        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("favorite_stories")
                .whereEqualTo("link", story.getLink())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // A notícia já está nos favoritos, exibe a estrela preenchida
                        holder.ivFavoriteStoryStar.setImageResource(R.drawable.star_filled);
                        holder.isFavorite = true;
                    } else {
                        // Notícia não está nos favoritos, exibe a estrela vazia
                        holder.ivFavoriteStoryStar.setImageResource(R.drawable.star);
                        holder.isFavorite = false;
                    }
                });

        holder.ivFavoriteStoryStar.setOnClickListener(v -> {
            if (holder.isFavorite) {
                new AlertDialog.Builder(context)
                        .setTitle("Remover Favorito")
                        .setMessage("Tem certeza de que deseja remover esta história dos favoritos?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            // Se o usuário confirmar, remove o item dos favoritos
                            removeFavoriteStory(story, holder);
                            if (favoriteClickListener != null) {
                                favoriteClickListener.onFavoriteClick(holder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton("Não", (dialog, which) -> {
                            // Se o usuário cancelar, apenas fechar o diálogo
                            dialog.dismiss();
                        }) // Não faz nada se o usuário cancelar
                        .show();
            } else {
                saveFavoriteStory(story, holder);
            }
        });

        // Configura o clique para abrir o link da story
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", story.getLink()); // Passa o link da story para a WebViewActivity
            context.startActivity(intent);
        });

    }

    private void saveFavoriteStory(Story story, StoriesAdapter.StoryViewHolder holder) {
        NewsManager newsManager = new NewsManager(context);
        newsManager.saveFavoriteStory(currentUser, story, new FavoriteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "História adicionada aos favoritos!", Toast.LENGTH_SHORT).show();
                holder.ivFavoriteStoryStar.setImageResource(R.drawable.star_filled);
                holder.isFavorite = true;
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Erro ao salvar história nos favoritos!", Toast.LENGTH_SHORT).show();
                Log.e("NewsAdapter", "Erro ao salvar história nos favoritos: " + e);
            }
        });

    }

    private void removeFavoriteStory(Story story,  StoriesAdapter.StoryViewHolder holder) {
        NewsManager newsManager = new NewsManager(context);
        newsManager.removeFavoriteStory(currentUser, story, new FavoriteCallback() {
            @Override
            public void onSuccess() {
                holder.isFavorite = false;
                holder.ivFavoriteStoryStar.setImageResource(R.drawable.star);
                Toast.makeText(context, "História removida dos favoritos!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Erro ao remover história dos favoritos!", Toast.LENGTH_SHORT).show();
                Log.e("NewsAdapter", "Erro ao remover história dos favoritos: ", e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stories == null ? 0 : stories.size();
    }

    // Método para calcular o tempo relativo da postagem
    private String calculateRelativeTime(ZonedDateTime postedTime) {
        ZonedDateTime now = ZonedDateTime.now();
        long daysAgo = ChronoUnit.DAYS.between(postedTime, now);
        long monthsAgo = ChronoUnit.MONTHS.between(postedTime, now);
        long yearsAgo = ChronoUnit.YEARS.between(postedTime, now);

        String relativeDate;

        if (yearsAgo > 0) {
            if(yearsAgo == 1){
                relativeDate = yearsAgo + " ano atrás";
            } else {
                relativeDate = yearsAgo + " anos atrás";
            }
        } else if (monthsAgo > 0) {
            if(monthsAgo == 1){
                relativeDate = monthsAgo + " mês atrás";
            } else {
                relativeDate = monthsAgo + " meses atrás";
            }
        } else if (daysAgo > 0) {
            if (daysAgo == 1) {
                relativeDate = "Ontem";
            } else {
                relativeDate = daysAgo + " dias atrás";
            }
        } else {
            long hoursAgo = ChronoUnit.HOURS.between(postedTime, now);
            if (hoursAgo == 0) {
                relativeDate = "Há menos de uma hora atrás";
            } else if (hoursAgo == 1) {
                relativeDate = hoursAgo + " hora atrás";
            } else {
                relativeDate = hoursAgo + " horas atrás";
            }
        }

        return relativeDate;
    }

    // ViewHolder da Story
    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvStoryTitle, tvStorySource, tvStoryPostTime;

        ImageView ivStorySourceIcon, ivFavoriteStoryStar;

        boolean isFavorite;
        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStoryTitle = itemView.findViewById(R.id.tv_story_title);
            tvStorySource = itemView.findViewById(R.id.tv_story_source);
            tvStoryPostTime = itemView.findViewById(R.id.tv_story_post_time);
            ivStorySourceIcon = itemView.findViewById(R.id.iv_story_source_icon);
            ivFavoriteStoryStar = itemView.findViewById(R.id.iv_favorite_story_star);
        }
    }
}
