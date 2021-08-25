package com.kos.crossstich.activityes;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kos.crossstich.items.StitchItem;
import com.kos.crossstich.adapters.StitchAdapter;
import com.kos.crossstich.db.DbManager;
import com.kos.crossstich.R;
import com.kos.util.SaveLoad;
import com.kos.util.SaveManager;
import com.kos.util.Trial;

import java.util.ArrayList;

import static com.kos.crossstich.adapters.StitchAdapter.stitchName;

//v2.1 Full
public class MainActivity extends AppCompatActivity {
    Trial trialClass = new Trial();
    static String endOfTrial;
    ArrayList<StitchItem> stitches;

    private DbManager dbManager;
    public EditText et_add_stich;
    private RecyclerView rvNewCrossStich;
    private StitchAdapter stitchAdapter;
    Dialog dialogAddStitch;
    public static Dialog deleteStitch;
    ImageButton bt_dialog_addStich_V;


    TextView tv_trial;
    TextView tv_trial2;

    //@RequiresApi(api = Build.VERSION_CODES.R)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        dbManager.openDb();
        stitchAdapter.updateStitchAdapter(dbManager.getStitchFromDb());

        endOfTrial = trialClass.trial(dbManager);
        if (endOfTrial.equals("end")) {
            tv_trial.setVisibility(View.VISIBLE);
            tv_trial2.setVisibility(View.VISIBLE);
            tv_trial2.setText("Время пробной версии истекло");
            tv_trial.setText("Ссылка на полную версию");
            tv_trial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://play.google.com/store/apps/details?id=com.kos.crossstich";

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //saveLoadclass.autoSave(this, dbManager);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.closeDb();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SaveLoad save = new SaveManager(getApplicationContext());
                    save.saveToDevice(getApplicationContext(), true);
                }
            }).start();
            Log.d("my", "сработало");
        }*/
    }

    private void init() {
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        tv_trial = (TextView) findViewById(R.id.trial);
        tv_trial2 = (TextView) findViewById(R.id.trial2);


        stitches = new ArrayList<>();
        dbManager = new DbManager(this);

        dialogAddStitch = new Dialog(this);
        dialogAddStitch.setContentView(R.layout.dialog_add_stitch);
        bt_dialog_addStich_V = dialogAddStitch.findViewById(R.id.bt_dialog_addStich_V);
        et_add_stich = dialogAddStitch.findViewById(R.id.et_add_stich);

        deleteStitch = new Dialog(this);
        deleteStitch.setContentView(R.layout.dialog_delete_stitch);
        rvNewCrossStich = findViewById(R.id.rv_New_CrossStich);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvNewCrossStich.setLayoutManager(layoutManager);
        stitchAdapter = new StitchAdapter(stitches, this);
        getItemTouchHelper().attachToRecyclerView(rvNewCrossStich);
        rvNewCrossStich.setAdapter(stitchAdapter);


    }

    public void but_add(View view) {
        dialogAddStitch.show();
    }

    public void addStitch(View view) {
        String stitchName = et_add_stich.getText().toString();
        dbManager.insertStitchToDb(stitchName, "some text");
        et_add_stich.setText("");
        stitchAdapter.updateStitchAdapter(dbManager.getStitchFromDb());
        dialogAddStitch.dismiss();
    }

    private ItemTouchHelper getItemTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deleteStitch.show();
            }
        });
    }


    public void onClickDeleteStitchYes(View view) {
        Toast.makeText(getBaseContext(), "Процесс завершен", Toast.LENGTH_SHORT).show();
        dbManager.deleteStitchFromDb(stitchName);
        stitchAdapter.updateStitchAdapter(dbManager.getStitchFromDb());
        dbManager.deleteAllTreadsFromCurrentStitch(stitchName);
        deleteStitch.dismiss();
    }

    public void onClickDeleteStitchNo(View view) {
        deleteStitch.dismiss();
        stitchAdapter.updateStitchAdapter(dbManager.getStitchFromDb());
    }

    public void onClickallThreads(View view) {
        endOfTrial = trialClass.trial(dbManager);
        Log.d("my", "endOfTrial = " + endOfTrial);
        Intent intentGoTOThreadsActivity = new Intent(this, ThreadsActivity.class);
        Intent intentGoTOMainActivity = new Intent(this, MainActivity.class);

        if (endOfTrial.equals("end")) {
            startActivity(intentGoTOMainActivity);
        } else {
            startActivity(intentGoTOThreadsActivity);
        }
    }

    public void onClickGoToFabric(View view) {
        endOfTrial = trialClass.trial(dbManager);
        Intent intentGoTOMainActivity = new Intent(this, MainActivity.class);
        Intent intentGoToFabricActivity = new Intent(this, FabricActivity.class);

        if (endOfTrial.equals("end")) {
            startActivity(intentGoTOMainActivity);
        } else {
            startActivity(intentGoToFabricActivity);
        }
    }

    public void gotoSaveLoadActivity(View view) {
        Intent intent = new Intent(this, SaveLoadActivity.class);
        startActivity(intent);
    }

}

