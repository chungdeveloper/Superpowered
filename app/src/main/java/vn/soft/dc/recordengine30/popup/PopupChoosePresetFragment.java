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

import vn.soft.dc.recordengine.model.Preset;
import vn.soft.dc.recordengine.model.PresetManager;
import vn.soft.dc.recordengine30.R;
import vn.soft.dc.recordengine30.adapter.PresetAdapter;

/**
 * Created by MSi-Gaming on 5/25/2017.
 */

public class PopupChoosePresetFragment extends DialogFragment {

    public interface OnPresetChoose {
        public void onChoose(Preset preset);
    }

    private List<Preset> musics;
    private OnPresetChoose onPresetChoose;
    private PresetAdapter musicAdapter;
    private RecyclerView recyclerView;

    public PopupChoosePresetFragment setOnPresetChoose(OnPresetChoose onPresetChoose) {
        this.onPresetChoose = onPresetChoose;
        return this;
    }

    public static PopupChoosePresetFragment newInstance() {
        Bundle args = new Bundle();
        PopupChoosePresetFragment fragment = new PopupChoosePresetFragment();
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
        musics = new PresetManager().getPlayList();
        musicAdapter = new PresetAdapter(getActivity().getApplicationContext(), musics);
        musicAdapter.setOnPresetChoose(onMusicChooseAdapter);
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
    }

    private OnPresetChoose onMusicChooseAdapter = new OnPresetChoose() {
        @Override
        public void onChoose(Preset music) {
            getDialog().dismiss();
            if (onPresetChoose == null) return;
            onPresetChoose.onChoose(music);
        }
    };

    @Override
    public void show(FragmentManager manager, String tag) {
        if (PopupChoosePresetFragment.this.isAdded()) return;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}
