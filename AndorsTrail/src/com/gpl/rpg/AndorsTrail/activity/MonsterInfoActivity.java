package com.gpl.rpg.AndorsTrail.activity;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.CombatController;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.view.ItemEffectsView;
import com.gpl.rpg.AndorsTrail.view.RangeBar;
import com.gpl.rpg.AndorsTrail.view.TraitsInfoView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public final class MonsterInfoActivity extends Activity {
	
	private WorldContext world;
	
	private TextView monsterinfo_title;
	private TextView monsterinfo_difficulty;
	private ItemEffectsView monsterinfo_onhiteffects;
    private RangeBar hp;
    private ViewGroup monsterinfo_container;
    private TextView monsterinfo_max_ap;
	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.getWorld();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.monsterinfo);

        monsterinfo_title = (TextView) findViewById(R.id.monsterinfo_title);
        monsterinfo_difficulty = (TextView) findViewById(R.id.monsterinfo_difficulty);
        monsterinfo_max_ap = (TextView) findViewById(R.id.monsterinfo_max_ap);
        
        Button b = (Button) findViewById(R.id.monsterinfo_close);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MonsterInfoActivity.this.finish();
			}
		});

        monsterinfo_onhiteffects = (ItemEffectsView) findViewById(R.id.actorinfo_onhiteffects);
        hp = (RangeBar) findViewById(R.id.monsterinfo_healthbar);
        hp.init(R.drawable.ui_progress_health, R.string.status_hp);
        monsterinfo_container = (ViewGroup) findViewById(R.id.monsterinfo_container);
    }

    @Override
	protected void onResume() {
    	super.onResume();
    	
    	Monster monster = Dialogs.getMonsterFromIntent(getIntent(), world);
        if (monster == null) {
        	finish();
        	return;
        }  
        
        updateTitle(monster);
    	updateTraits(monster);
    }

	private void updateTitle(Monster monster) {
		monsterinfo_title.setText(monster.getName());
		world.tileManager.setImageViewTile(monsterinfo_title, monster);
        monsterinfo_difficulty.setText(getMonsterDifficultyResource(world, monster));
	}

	private void updateTraits(Monster monster) {
		TraitsInfoView.update(monsterinfo_container, monster);
		monsterinfo_onhiteffects.update(
        		null, 
        		null, 
        		monster.getOnHitEffectsAsList(), 
        		null,
        		false);
        hp.update(monster.getMaxHP(), monster.getCurrentHP());
        monsterinfo_max_ap.setText(Integer.toString(monster.getMaxAP()));
    }

	public static int getMonsterDifficultyResource(WorldContext world, Monster monster) {
		final int difficulty = CombatController.getMonsterDifficulty(world, monster);
		if (difficulty >= 80) return R.string.monster_difficulty_veryeasy;
		else if (difficulty >= 60) return R.string.monster_difficulty_easy;
		else if (difficulty >= 40) return R.string.monster_difficulty_normal;
		else if (difficulty >= 20) return R.string.monster_difficulty_hard;
		else if (difficulty == 0) return R.string.monster_difficulty_impossible;
		else return R.string.monster_difficulty_veryhard;
	}
}
