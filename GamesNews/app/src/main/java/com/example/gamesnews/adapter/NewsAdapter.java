package com.example.gamesnews.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamesnews.activity.FullCoverageActivity;
import com.example.gamesnews.firestore.FavoriteCallback;
import com.example.gamesnews.firestore.NewsManager;
import com.example.gamesnews.model.Highlight;
import com.example.gamesnews.model.NewsResult;
import com.example.gamesnews.R;
import com.example.gamesnews.activity.WebViewActivity;
import com.example.gamesnews.model.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsResult> newsResults;
    private Context context;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private StoriesAdapter storiesAdapter;
    private OnFavoriteClickListener favoriteClickListener; // Ouvinte do clique no favorito

    public void setOnFavoriteClickListener(OnFavoriteClickListener favoriteClickListener) {
        this.favoriteClickListener = favoriteClickListener;
    }

    public NewsAdapter(List<NewsResult> newsResults, Context context) {
        this.newsResults = newsResults;
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsResult newsResult = newsResults.get(position);

        if (newsResult.getHighlight() != null) {
            Highlight highlight = newsResult.getHighlight();

            // Configura o título
            holder.tvNewsTitle.setText(highlight.getTitle());

            // Configura a thumbnail da notícia
            Picasso.get().load(highlight.getThumbnail()).placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.error_image_placeholder).into(holder.ivNewsThumbnail);

            // Configura o nome da fonte
            holder.tvSourceName.setText(highlight.getSource().getName());

            // Configura o ícone da fonte
            Picasso.get().load(highlight.getSource().getIcon()).into(holder.ivSourceIcon);

            holder.tvNewsDate.setText(calculateRelativeTime(highlight.getDate()));

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", highlight.getLink());
                context.startActivity(intent);
            });

            // Configurar a exibição das stories
            List<Story> stories = newsResult.getStories();
            if (stories != null && !stories.isEmpty()) {
                holder.rvStories.setVisibility(View.VISIBLE);
                storiesAdapter = new StoriesAdapter(stories, context);
                holder.rvStories.setAdapter(storiesAdapter);
                holder.rvStories.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            }

            // Verificar se a notícia já está salva nos favoritos
            firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("favorite_highlights")
                    .whereEqualTo("link", highlight.getLink())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // A notícia já está nos favoritos, exibe a estrela preenchida
                            holder.ivFavoriteStar.setImageResource(R.drawable.star_filled);
                            holder.isFavorite = true;
                        } else {
                            // Notícia não está nos favoritos, exibe a estrela vazia
                            holder.ivFavoriteStar.setImageResource(R.drawable.star);
                            holder.isFavorite = false;
                        }
                    });

            holder.ivFavoriteStar.setOnClickListener(v -> {
                    if (holder.isFavorite) {
                        new AlertDialog.Builder(context)
                                .setTitle("Remover Favorito")
                                .setMessage("Tem certeza de que deseja remover este destaque dos favoritos?")
                                .setPositiveButton("Sim", (dialog, which) -> {
                                    // Se o usuário confirmar, remove o item dos favoritos
                                    removeFavoriteHighlight(newsResult.getHighlight(), holder);
                                    if (favoriteClickListener != null) {
                                        favoriteClickListener.onFavoriteClick(holder.getAdapterPosition());
                                    }
                                })
                                .setNegativeButton("Não",  (dialog, which) -> {
                                    // Se o usuário cancelar, apenas fechar o diálogo
                                    dialog.dismiss();
                                }) // Não faz nada se o usuário cancelar
                                .show();
                    } else {
                        saveFavoriteHighlight(newsResult.getHighlight(), holder);
                    }
            });

            } else if (newsResult.getStories() != null && newsResult.getStories().size() == 1) {
            Story story = newsResult.getStories().get(0);
            holder.rvStories.setVisibility(View.GONE);

            // Configura o título
            holder.tvNewsTitle.setText(story.getTitle());

            // Configura a thumbnail da notícia
            Picasso.get().load(story.getThumbnail()).placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.error_image_placeholder).into(holder.ivNewsThumbnail);

            // Configura o nome da fonte
            holder.tvSourceName.setText(story.getSource().getName());

            // Configura o ícone da fonte
            Picasso.get().load(story.getSource().getIcon()).into(holder.ivSourceIcon);

            holder.tvNewsDate.setText(calculateRelativeTime(story.getDate()));

            // Verificar se a notícia já está salva nos favoritos
            firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("favorite_stories")
                    .whereEqualTo("link", story.getLink())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // A notícia já está nos favoritos, exibe a estrela preenchida
                            holder.ivFavoriteStar.setImageResource(R.drawable.star_filled);
                            holder.isFavorite = true;
                        } else {
                            // Notícia não está nos favoritos, exibe a estrela vazia
                            holder.ivFavoriteStar.setImageResource(R.drawable.star);
                            holder.isFavorite = false;
                        }
                    });

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", story.getLink()); // Passa o link da notícia para a WebViewActivity
                context.startActivity(intent);
            });

            holder.ivFavoriteStar.setOnClickListener(v -> {
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
            } else {
            holder.rvStories.setVisibility(View.GONE);

            // Configura o título
            holder.tvNewsTitle.setText(newsResult.getTitle());

            // Configura a thumbnail da notícia
            Picasso.get().load(newsResult.getThumbnail()).placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.error_image_placeholder).into(holder.ivNewsThumbnail);

            // Configura o nome da fonte
            holder.tvSourceName.setText(newsResult.getSource().getName());

            // Configura o ícone da fonte
            Picasso.get().load(newsResult.getSource().getIcon()).into(holder.ivSourceIcon);

            holder.tvNewsDate.setText(calculateRelativeTime(newsResult.getDate()));

            // Verificar se a notícia já está salva nos favoritos
            firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("favorites")
                    .whereEqualTo("link", newsResult.getLink())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // A notícia já está nos favoritos, exibe a estrela preenchida
                            holder.ivFavoriteStar.setImageResource(R.drawable.star_filled);
                            holder.isFavorite = true;
                        } else {
                            // Notícia não está nos favoritos, exibe a estrela vazia
                            holder.ivFavoriteStar.setImageResource(R.drawable.star);
                            holder.isFavorite = false;
                        }
                    });

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", newsResult.getLink()); // Passa o link da notícia para a WebViewActivity
                context.startActivity(intent);
            });

            holder.ivFavoriteStar.setOnClickListener(v -> {
                if (holder.isFavorite) {
                    new AlertDialog.Builder(context)
                            .setTitle("Remover Favorito")
                            .setMessage("Tem certeza de que deseja remover esta notícia dos favoritos?")
                            .setPositiveButton("Sim", (dialog, which) -> {
                                // Se o usuário confirmar, remove o item dos favoritos
                                removeFavoriteNews(newsResult, holder);
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
                    saveFavoriteNews(newsResult, holder);
                }
            });
        }

        if (newsResult.getStoryToken() != null && !newsResult.getStoryToken().isEmpty()) {
            holder.btnFullCoverage.setVisibility(View.VISIBLE);
            holder.btnFullCoverage.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullCoverageActivity.class);
                intent.putExtra("story_token", newsResult.getStoryToken());
                context.startActivity(intent);
            });
        } else {
            holder.btnFullCoverage.setVisibility(View.GONE);
        }
    }

    public void removeItem (int position){
        newsResults.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, newsResults.size());
    }

    private void saveFavoriteStory(Story story, NewsAdapter.NewsViewHolder holder) {
        NewsManager newsManager = new NewsManager(context);
        newsManager.saveFavoriteStory(currentUser, story, new FavoriteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "História adicionada aos favoritos!", Toast.LENGTH_SHORT).show();
                holder.ivFavoriteStar.setImageResource(R.drawable.star_filled);
                holder.isFavorite = true;
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Erro ao salvar história nos favoritos!", Toast.LENGTH_SHORT).show();
                Log.e("NewsAdapter", "Erro ao salvar história nos favoritos: " + e);
            }
        });

    }

    private void removeFavoriteStory(Story story, NewsAdapter.NewsViewHolder holder) {
        NewsManager newsManager = new NewsManager(context);
        newsManager.removeFavoriteStory(currentUser, story, new FavoriteCallback() {
            @Override
            public void onSuccess() {
                holder.isFavorite = false;
                holder.ivFavoriteStar.setImageResource(R.drawable.star);
                Toast.makeText(context, "História removida dos favoritos!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Erro ao remover história dos favoritos!", Toast.LENGTH_SHORT).show();
                Log.e("NewsAdapter", "Erro ao remover história dos favoritos: ", e);
            }
        });
    }
    public void setStoryOnFavoriteClickListener() {
        if(storiesAdapter != null) {
            storiesAdapter.setOnFavoriteClickListener((posicao) -> {
                newsResults.remove(posicao);
                storiesAdapter.notifyItemRemoved(posicao);
                storiesAdapter.notifyItemRangeChanged(posicao, newsResults.size());
            });
        }
    }

    public void updateList(ArrayList<NewsResult> filteredList) {
        this.newsResults = filteredList;
        notifyDataSetChanged();
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
                relativeDate = "Postado há " + yearsAgo + " ano atrás";
            } else {
                relativeDate = "Postado há " + yearsAgo + " anos atrás";
            }
        } else if (monthsAgo > 0) {
            if(monthsAgo == 1){
                relativeDate = "Postado há " + monthsAgo + " mês atrás";
            } else {
                relativeDate = "Postado há " + monthsAgo + " meses atrás";
            }
        } else if (daysAgo > 0) {
            if (daysAgo == 1) {
                relativeDate = "Postado ontem";
            } else {
                relativeDate = "Postado há " + daysAgo + " dias atrás";
            }
        } else {
            long hoursAgo = ChronoUnit.HOURS.between(postedTime, now);
            if (hoursAgo == 0) {
                relativeDate = "Postado há menos de uma hora atrás";
            } else if (hoursAgo == 1) {
                relativeDate = "Postado há " + hoursAgo + " hora atrás";
            } else {
                relativeDate = "Postado há " + hoursAgo + " horas atrás";
            }
        }

        return relativeDate;
    }

    @Override
    public int getItemCount() {
        return newsResults == null ? 0 : newsResults.size();
    }

    private void saveFavoriteNews(NewsResult newsResult, NewsViewHolder holder) {
        NewsManager newsManager = new NewsManager(context);
        newsManager.saveFavoriteNews(currentUser, newsResult, new FavoriteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Notícia adicionada aos favoritos!", Toast.LENGTH_SHORT).show();
                holder.ivFavoriteStar.setImageResource(R.drawable.star_filled);
                holder.isFavorite = true;
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Erro ao salvar notícia nos favoritos!", Toast.LENGTH_SHORT).show();
                Log.e("NewsAdapter", "Erro ao salvar notícia nos favoritos: " + e);
            }
        });

    }

    private void saveFavoriteHighlight(Highlight highlight, NewsViewHolder holder) {
        NewsManager newsManager = new NewsManager(context);
        newsManager.saveFavoriteHighlight(currentUser, highlight, new FavoriteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Destaque adicionado aos favoritos!", Toast.LENGTH_SHORT).show();
                holder.ivFavoriteStar.setImageResource(R.drawable.star_filled);
                holder.isFavorite = true;
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Erro ao salvar destaque nos favoritos!", Toast.LENGTH_SHORT).show();
                Log.e("NewsAdapter", "Erro ao salvar destaque nos favoritos: " + e);
            }
        });

    }

    private void removeFavoriteNews(NewsResult newsResult, NewsViewHolder holder) {
        NewsManager newsManager = new NewsManager(context);
        newsManager.removeFavorite(currentUser, newsResult, new FavoriteCallback() {
            @Override
            public void onSuccess() {
                holder.isFavorite = false;
                holder.ivFavoriteStar.setImageResource(R.drawable.star);
                Toast.makeText(context, "Notícia removida dos favoritos!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Erro ao remover notícia dos favoritos!", Toast.LENGTH_SHORT).show();
                Log.e("NewsAdapter", "Erro ao remover notícia dos favoritos: ", e);
            }
        });
    }


    private void removeFavoriteHighlight(Highlight highlight, NewsViewHolder holder) {
        NewsManager newsManager = new NewsManager(context);
        newsManager.removeFavoriteHighlight(currentUser, highlight, new FavoriteCallback() {
            @Override
            public void onSuccess() {
                holder.isFavorite = false;
                holder.ivFavoriteStar.setImageResource(R.drawable.star);
                Toast.makeText(context, "Destaque removido dos favoritos!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Erro ao remover destaque dos favoritos!", Toast.LENGTH_SHORT).show();
                Log.e("NewsAdapter", "Erro ao remover destaque dos favoritos: ", e);
            }
        });
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tvNewsTitle, tvSourceName, tvNewsDate;

        ImageView ivNewsThumbnail, ivSourceIcon, ivFavoriteStar;

        RecyclerView rvStories;
        boolean isFavorite;

        Button btnFullCoverage;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNewsTitle = itemView.findViewById(R.id.tv_news_title);
            ivNewsThumbnail = itemView.findViewById(R.id.iv_news_thumbnail);
            tvSourceName = itemView.findViewById(R.id.tv_source_name);
            ivSourceIcon = itemView.findViewById(R.id.iv_source_icon);
            tvNewsDate = itemView.findViewById(R.id.tv_news_date);
            ivFavoriteStar = itemView.findViewById(R.id.iv_favorite_star);
            rvStories = itemView.findViewById(R.id.rv_stories);
            btnFullCoverage = itemView.findViewById(R.id.btn_full_coverage);
        }
    }
}
