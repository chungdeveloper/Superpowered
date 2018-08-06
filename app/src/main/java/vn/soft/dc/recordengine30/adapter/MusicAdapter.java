package vn.soft.dc.recordengine30.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.soft.dc.recordengine30.R;
import vn.soft.dc.recordengine30.model.Music;
import vn.soft.dc.recordengine30.popup.PopupChooseMusicFragment;

/**
 * Created by MSi-Gaming on 5/25/2017.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private PopupChooseMusicFragment.OnMusicChoose onMusicChoose;
    private List<Music> musics;
    private LayoutInflater inflater;

    public MusicAdapter(Context context, List<Music> musics) {
        this.musics = musics;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnMusicChoose(PopupChooseMusicFragment.OnMusicChoose onMusicChoose) {
        this.onMusicChoose = onMusicChoose;
    }

    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(MusicAdapter.ViewHolder holder, final int position) {
        final Music music = musics.get(position);
        String[] path = music.getUrl().split("/");
        holder.tvContent.setText(path[path.length - 1]);
        if (onMusicChoose == null) return;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMusicChoose.onChoose(music);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
        }
    }
}
