package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MusicListActivity extends AppCompatActivity {
    int position2=0;//标记歌曲位置
    ListView listView;
    Button Playing;
    ImageView imageViewSelected;
    TextView textViewSelectedTitle;
    TextView textViewSelectedArtist;
    RelativeLayout relativeLayoutEndItem;
    private Button PlayAll;
    private Button randomPlay;
    private Button buttonLast;
    private Button buttonNext;
    static List<Song> songList;
SongAdapter sa ;
    public MusicListActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        init();
        songList = MusicUtils.getAllMusics(this);
        Log.i(TAG, "onCreate: " + songList.size());
        Log.i(TAG, "onCreate: " + songList);
        sa = new SongAdapter(this, R.layout.music_list_item, songList);
        listView.setAdapter(sa);
        initSelectedSong();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position2 =position;
                Song song = (Song) parent.getItemAtPosition(position);
                if(!song.equals(selectSong)){// 如果当前播放的不是这首歌
                    changeSelectSong(song);
                    playSelectSong();
                }else{
                    if(!Player.isPlaying()){// 没有正在播放
                        playSelectSong();// 播放
                    }else{// 跳转到详细页面
                        // 跳转到主页面
                        jumpMain();
                    }
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MusicListActivity.this,"LongClick",Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "delete");
                    Song song = (Song) parent.getItemAtPosition(position);
                    DeleteDialog(song);

                return true;
            }
        });

        relativeLayoutEndItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectSong != null){
                    // 跳转到主页面
                    jumpMain();
                }
            }
        });
        Playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectSong == null){
                    initSelectedSong();// 尝试初始化
                }
                if(Player.isPlaying()){
                    pauseSelectSong();
                }else{
                    playSelectSong();
                }
            }
        });
        PlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSelectedSong();// 尝试初始化

                playSelectSong();
            }
        });
        randomPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomInitSelectedSong();
                playSelectSong();
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position2>=songList.size()-1){
                    position2=0;
                }else {
                    position2++;
                }
                changeSelectSong(songList.get(position2));
                playSelectSong();
            }
        });
        buttonLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position2<=0){
                    position2=songList.size()-1;
                }else {
                    position2--;
                }
                changeSelectSong(songList.get(position2));
                playSelectSong();
            }
        });
    }
    private void jumpMain(){
        if(selectSong != null) {
            // 跳转到主页面
            Intent intent = new Intent(MusicListActivity.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("song", selectSong);// 添加歌曲
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    private void DeleteDialog(final Song songId) { //删除歌曲
        Log.v(TAG, "delete");
        new AlertDialog.Builder(this).setTitle("删除歌曲")
                .setMessage("是否真的删除歌曲?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        songList.remove(songId);
                        sa.notifyDataSetChanged();
                        //差刷新
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }
    private static Song selectSong;

    public void initSelectedSong(){
        if(!songList.isEmpty()){// 初始化选中歌曲
            changeSelectSong(songList.get(0));
        }
    }
    public void randomInitSelectedSong(){ //随机播放
        if(!songList.isEmpty()){// 初始化选中歌曲
            changeSelectSong(songList.get(new Random().nextInt(songList.size()-1)));
        }
    }
    public void changeSelectSong(Song song) {
        if (song != null) {
            selectSong = song;
            if(song.getBitmap() != null)
                imageViewSelected.setImageBitmap(song.getBitmap());
            textViewSelectedTitle.setText(song.getTitle());
            textViewSelectedArtist.setText(song.getArtist());
        }
    }

    public void playSelectSong() {
        if (selectSong != null) {
            Player.setPlayPath(selectSong.getPath());
            Player.play();
            //imageViewPlaying.setImageResource(R.mipmap.pause);
        }
    }

    public void pauseSelectSong() {
        if (selectSong != null) {
            if(Player.isPlaying())
                Player.pause();
            //imageViewPlaying.setImageResource(R.mipmap.list_play);
        }
    }

    private static final String TAG = "MusicListActivity";

    private void init() {
        listView = findViewById(R.id.listViewMucisList);
        Playing = findViewById(R.id.Playing);
        imageViewSelected = findViewById(R.id.imageViewSelected);
        textViewSelectedTitle = findViewById(R.id.textViewSelectedTitle);
        textViewSelectedArtist = findViewById(R.id.textViewSelectedArist);
        relativeLayoutEndItem = findViewById(R.id.relativeLayoutEndItem);
        PlayAll = findViewById(R.id.PlayAll);
        randomPlay = findViewById(R.id.randomPlay);
        buttonLast = findViewById(R.id.buttonLast);
        buttonNext = findViewById(R.id.buttonNext);
    }

    /**
     * 歌曲的list适配器
     */
    class SongAdapter extends ArrayAdapter<Song> {
        private int resourceId;

        public SongAdapter(@NonNull Context context, int resource, @NonNull List<Song> objects) {
            super(context, resource, objects);
            this.resourceId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null, false);
            Song song = getItem(position);
            TextView index = view.findViewById(R.id.textViewIndex);
            index.setText(position + 1 + "");
            TextView title = view.findViewById(R.id.textViewTitle);
            title.setText(song.getTitle());
            TextView artist = view.findViewById(R.id.textViewArtist);
            artist.setText(song.getArtist());
            return view;
        }
    }
}
