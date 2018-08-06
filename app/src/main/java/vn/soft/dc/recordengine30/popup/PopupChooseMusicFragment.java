package vn.soft.dc.recordengine30.popup;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.List;

import vn.soft.dc.recordengine30.R;
import vn.soft.dc.recordengine30.adapter.MusicAdapter;
import vn.soft.dc.recordengine30.manager.SongsManager;
import vn.soft.dc.recordengine30.model.Music;

/**
 * Created by MSi-Gaming on 5/25/2017.
 */

public class PopupChooseMusicFragment extends DialogFragment {

    public interface OnMusicChoose {
        public void onChoose(Music music);
    }

    public static final String PATH_MUSIC = "path";
    private List<Music> musics;
    private OnMusicChoose onMusicChoose;
    private MusicAdapter musicAdapter;
    private RecyclerView recyclerView;

    public PopupChooseMusicFragment setOnMusicChoose(OnMusicChoose onMusicChoose) {
        this.onMusicChoose = onMusicChoose;
        return this;
    }

    public static PopupChooseMusicFragment newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PATH_MUSIC, path);
        PopupChooseMusicFragment fragment = new PopupChooseMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_choose, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rcList);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musics = new SongsManager(getArguments().getString(PATH_MUSIC)).getPlayList();
        musicAdapter = new MusicAdapter(getActivity().getApplicationContext(), musics);
        musicAdapter.setOnMusicChoose(onMusicChooseAdapter);
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
    }

    private OnMusicChoose onMusicChooseAdapter = new OnMusicChoose() {
        @Override
        public void onChoose(Music music) {
            getDialog().dismiss();
            if (onMusicChoose == null) return;
            onMusicChoose.onChoose(music);
        }
    };

    @Override
    public void show(FragmentManager manager, String tag) {
        if (PopupChooseMusicFragment.this.isAdded()) return;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}
