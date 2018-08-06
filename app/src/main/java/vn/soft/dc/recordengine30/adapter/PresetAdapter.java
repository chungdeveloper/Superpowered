package vn.soft.dc.recordengine30.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.soft.dc.recordengine.model.Preset;
import vn.soft.dc.recordengine30.R;
import vn.soft.dc.recordengine30.popup.PopupChoosePresetFragment;

/**
 * Created by MSi-Gaming on 5/25/2017.
 */

public class PresetAdapter extends RecyclerView.Adapter<PresetAdapter.ViewHolder> {

    private PopupChoosePresetFragment.OnPresetChoose onPresetChoose;
    private List<Preset> musics;
    private LayoutInflater inflater;

    public PresetAdapter(Context context, List<Preset> musics) {
        this.musics = musics;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnPresetChoose(PopupChoosePresetFragment.OnPresetChoose onPresetChoose) {
        this.onPresetChoose = onPresetChoose;
    }

    @Override
    public PresetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(PresetAdapter.ViewHolder holder, final int position) {
        final Preset music = musics.get(position);
        String[] path = music.getName().split("/");
        holder.tvContent.setText(path[path.length - 1]);
        if (onPresetChoose == null) return;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPresetChoose.onChoose(music);
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
