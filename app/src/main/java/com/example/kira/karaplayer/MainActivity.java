package com.example.kira.karaplayer;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
   public MediaPlayer mediaPlayer;
    int x;
    int r=0;
    TextView mytime;
    TextView mytime2;
    ArrayList<iteams> mylist=new ArrayList<iteams>();
   public   SeekBar seekBar;
   ListView listView;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = new MediaPlayer();
        mytime=(TextView) findViewById(R.id.time);
        mytime2=(TextView) findViewById(R.id.time2);
        Button play=(Button) findViewById(R.id.play);
        Button pause=(Button)findViewById(R.id.pause);
        Button stop=(Button)findViewById(R.id.stop);
        seekBar=(SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                x=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
mediaPlayer.seekTo(x);
            }
        });
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
getsongs();
ListView listView=(ListView)findViewById(R.id.list);
listView.setAdapter(new Myadaptar());
listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
r=1;
            mediaPlayer.stop();
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setDataSource(mylist.get(position).path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            int x=(mediaPlayer.getDuration()/1000)/60;
            int y=(mediaPlayer.getDuration()/1000)%60;
      settime(x,y,mytime);
            new mytread().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
});
    }

    public void getsongs(){

        Uri myuri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
String selection=MediaStore.Audio.Media.IS_MUSIC+" !=0";
cursor=managedQuery(myuri,null,selection,null,null);
if(cursor.moveToFirst()){
    do{
        String name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        String path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        String artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        mylist.add(new iteams(name,path,artist));
    }while(cursor.moveToNext());
}
        cursor.close();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play :
                if(r==0&&mediaPlayer==null){
                mediaPlayer.start();
                    r=1;
            }
                     else if (!mediaPlayer.isPlaying()) {
               mediaPlayer.seekTo(x);
               mediaPlayer.start();
            }
            break;
            case R.id.pause :
               if( mediaPlayer != null) {
                mediaPlayer.pause();
               x = mediaPlayer.getCurrentPosition();
           }
                break;
            case R.id.stop :
                if( mediaPlayer != null) {
                mediaPlayer.stop();
                seekBar.setProgress(0);
                    mytime.setText("00:00");
                mytime2.setText("00:00");
                r=0;
           }
        }
    }

public class  Myadaptar extends BaseAdapter{

    @Override
    public int getCount() {
        return mylist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=getLayoutInflater();
       View v= layoutInflater.inflate(R.layout.add,null);
        TextView name=(TextView) v.findViewById(R.id.textView);
        TextView  artist=(TextView) v.findViewById(R.id.textView2);
        name.setText(mylist.get(position).name);
        artist.setText(mylist.get(position).artist);
        return v;
    }
}

    public class mytread extends  Thread {
        public void run(){
            while (mediaPlayer!=null){

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seekBar.post(new Runnable() {
                    @Override
                    public void run() {
                        if(r==0)
                            seekBar.setProgress(0);
                        else{
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            int x=(mediaPlayer.getCurrentPosition()/1000)/60;
                            int y=(mediaPlayer.getCurrentPosition()/1000)%60;
                        settime(x,y,mytime2);
                        }
                    }
                });
            }

        }
    }
    public void settime(int x,int y,TextView t){
        if(x<10&&y<10)
            t.setText("0"+x+":0"+y);
        else if(x<10&&y<59)
            t.setText("0"+x+":"+y);

        else{
            if(x<10)
                t.setText("0"+x+":"+y);
            if(y<10)
                t.setText(x+":0"+y);
            else
                t.setText(x+":"+y);
        }
    }
}
